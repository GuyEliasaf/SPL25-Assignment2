package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation)  {

        if(vector.length == 0) throw new IllegalArgumentException("vector has no values.");
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        readLock();
        try{
            return vector[index];
        }

        finally{
            readUnlock();
        }
        
    }

    public int length() {
        readLock();
        try{
            return vector.length;
        }

        finally{
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
         readLock();
        try{
            return orientation;
        }

        finally{
            readUnlock();
        }
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();

        try{
            if (orientation == VectorOrientation.ROW_MAJOR) {
            orientation = VectorOrientation.COLUMN_MAJOR;
            } 
            else {
            orientation = VectorOrientation.ROW_MAJOR;
            }
        }

        finally{
             writeUnlock();
        }
       
    }

    public void add(SharedVector other) {
        //Resource ordering
        if(System.identityHashCode(this) < System.identityHashCode(other)){
            writeLock();
            other.readLock();

            try {
                //check orientention and length
                if(!getOrientation().equals(other.getOrientation())) throw new IllegalArgumentException("Vectors must be of the same orientation.");
                if(this.length() != other.length()) throw new IllegalArgumentException("Vectors must be of the same length to add.");
                
                for (int i = 0; i < this.length(); i++) {
                    this.vector[i] += other.get(i);
                }

            }

            catch(Exception e){
                System.out.print(e.getMessage());
            }
            finally {
                other.readUnlock();
                writeUnlock();
            }
        }
        //Resource ordering
        else{
            other.readLock();
            writeLock();

            try {
                //check orientention and length
                if(!getOrientation().equals(other.getOrientation())) throw new IllegalArgumentException("Vectors must be of the same orientation.");
                if(this.length() != other.length()) throw new IllegalArgumentException("Vectors must be of the same length to add.");
                
                
                for (int i = 0; i < this.length(); i++) {
                    this.vector[i] += other.get(i);
                }

            }
            finally {
                writeUnlock();
                other.readUnlock();
            }
        }
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < this.length(); i++) {
            this.vector[i] = -this.vector[i];
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {
        //Resource ordering 
        if(System.identityHashCode(this) < System.identityHashCode(other)){
            readLock();
            other.readLock();
            try{
                if(length() != other.length()) throw new IllegalArgumentException("Vectors must be of the same length to dot.");
                if(!orientation.equals(VectorOrientation.ROW_MAJOR))
                {
                    if(length() != 1) throw new IllegalArgumentException("Vectors can't have more than 1 row while dot.");
                    return get(0)  * other.get(0); 
                }
                if(other.orientation.equals(VectorOrientation.ROW_MAJOR)) throw new IllegalArgumentException("Rows can't be multiply.");

                double ret = 0;
                for(int i = 0; i < length(); i++)
                    ret+=  get(i) * other.get(i);
                return ret;
            }
            finally{
                other.readUnlock();
                readUnlock();
            }
            }
        //Resource ordering    
        else{   
            other.readLock();
            readLock();
            try{
                if(length() != other.length()) throw new IllegalArgumentException("Vectors must be of the same length to dot.");
                if(!orientation.equals(VectorOrientation.ROW_MAJOR))
                {
                    if(length() != 1) throw new IllegalArgumentException("Vectors can't have more than 1 row while dot.");
                    return get(0)  * other.get(0);
                }
                
                if(other.orientation.equals(VectorOrientation.ROW_MAJOR)) throw new IllegalArgumentException("Rows can't be multiply.");

                double ret = 0;
                for(int i = 0; i < length(); i++)
                    ret+=  get(i) * other.get(i);
                return ret;
            }
            finally{
                readUnlock();
                other.readUnlock();
            }
            }

        
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();
        double[][]m = matrix.readRowMajor();
        try{
            if(m == null) throw new IllegalArgumentException("Matrix can't be null for multiplication.");
            if(length() != m.length) throw new IllegalArgumentException("Vectors length and the number of matrix rows must be of the same to multiplication.");
            if (m.length == 0) throw new IllegalArgumentException("Matrix has no rows.");
            if(orientation != VectorOrientation.ROW_MAJOR) throw new IllegalArgumentException("Vector must be ROW_MAJOR for vector-matrix multiplication.");

            int row = length();
            int column = m[0].length;
            double[] temp = new double[column];
            for(int i = 0; i < column; i++){
                for(int j = 0; j < row ; j++){
                    temp[i] += get(j) *m[j][i];
                }
            }
            this.vector = temp;
            this.orientation = VectorOrientation.ROW_MAJOR;
        }

        finally{
            writeUnlock();
        }
    }
}
