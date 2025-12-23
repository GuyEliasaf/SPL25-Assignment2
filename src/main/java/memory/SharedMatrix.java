package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        vectors = new SharedVector[matrix[0].length];
        for (int i = 0; i < matrix[0].length; i++) {
            double[] col = new double[matrix.length];
            for (int j = 0; j < matrix.length; j++) {
                col[j] = matrix[j][i];
            }
            vectors[i] = new SharedVector(col, VectorOrientation.COLUMN_MAJOR);
        }
    }

    public double[][] readRowMajor() {
        acquireAllVectorReadLocks(vectors);
        try {
            int rows = vectors.length;
            int cols = vectors[0].length();
            double[][] ret = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    ret[i][j] = vectors[i].get(j);
                }
            }
            return ret;
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