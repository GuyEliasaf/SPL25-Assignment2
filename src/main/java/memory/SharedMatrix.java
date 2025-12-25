package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
    if (matrix == null || matrix.length == 0) {
        this.vectors = new SharedVector[0];
        return; 
    }
    else {
        if (matrix[0] == null) {
            throw new IllegalArgumentException("First row cannot be null");
        }
        
        int expectedCols = matrix[0].length;
        this.vectors = new SharedVector[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Row " + i + " cannot be null");
            }
            if (matrix[i].length != expectedCols) {
                throw new IllegalArgumentException("Inconsistent row lengths at row " + i);
            }
            this.vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }
}

    public void loadRowMajor(double[][] matrix) {
    if (matrix == null || matrix.length == 0) {
        this.vectors = new SharedVector[0];
        return;
    }

    int expectedCols = matrix[0].length;
    this.vectors = new SharedVector[matrix.length];
    
    for (int i = 0; i < matrix.length; i++) {
        if (matrix[i] == null) throw new IllegalArgumentException("Row cannot be null");
        if (matrix[i].length != expectedCols) throw new IllegalArgumentException("Inconsistent row lengths");
        
        this.vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
    }
}

    public void loadColumnMajor(double[][] matrix) {
    if (matrix == null || matrix.length == 0) {
        this.vectors = new SharedVector[0];
        return;
    }

    int rows = matrix.length;
    if (matrix[0] == null) throw new IllegalArgumentException("Row is null");
    int cols = matrix[0].length;

    for (int i = 0; i < rows; i++) {
        if (matrix[i] == null) throw new IllegalArgumentException("Row " + i + " is null");
        if (matrix[i].length != cols) throw new IllegalArgumentException("Inconsistent row lengths");
    }

    this.vectors = new SharedVector[cols];
    for (int j = 0; j < cols; j++) {
        double[] colData = new double[rows];
        for (int i = 0; i < rows; i++) {
            colData[i] = matrix[i][j];
        }
        this.vectors[j] = new SharedVector(colData, VectorOrientation.COLUMN_MAJOR);
    }
}

    public double[][] readRowMajor() {
        if (vectors == null) throw new IllegalArgumentException("Matrix has no vectors");
        if (vectors.length == 0) return new double[0][0];
        if (vectors[0] == null) throw new IllegalArgumentException("Matrix has null vector");
        acquireAllVectorReadLocks(vectors);
        try {
            if(this.vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR) {
                int rows = vectors.length;
                int cols = vectors[0].length();
                if(rows == 0) throw new IllegalArgumentException("Matrix has no rows");
                double[][] ret = new double[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        ret[i][j] = vectors[i].get(j);
                    }
                }
                return ret;
            }
            else{
                int cols = vectors.length;
                int rows = vectors[0].length();
                if(cols == 0) throw new IllegalArgumentException("Matrix has no rows");
                double[][] ret = new double[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        ret[i][j] = vectors[j].get(i);
                    }
                }
                return ret;
            }
            
        } finally {
            releaseAllVectorReadLocks(vectors);
        }
    }

    public SharedVector get(int index) {
        vectors[index].readLock();
        try {
            return vectors[index];
        } finally {
            vectors[index].readUnlock();
        }
    }

    public int length() {
        acquireAllVectorReadLocks(vectors);
        try {
            return vectors.length;
        } finally {
            releaseAllVectorReadLocks(vectors);
        }
    }

    public VectorOrientation getOrientation() {
        acquireAllVectorReadLocks(vectors);
        try {
            if (vectors.length == 0) {
                throw new IllegalStateException("Matrix has no vectors to determine orientation");
            }
            return vectors[0].getOrientation();
        } finally {
            releaseAllVectorReadLocks(vectors);
        }
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            vec.writeUnlock();
        }
    }
}