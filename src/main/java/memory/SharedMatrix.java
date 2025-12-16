package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        int len = matrix.length;
        this.vectors = new SharedVector[len];
        for(int i = 0; i < len; i++){
            this.vectors[i] = new SharedVector(matrix[i].clone(), VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        int len = matrix.length;
        SharedVector[] newVectors = new SharedVector[len];
        for(int i = 0; i < len; i++){
            newVectors[i] = new SharedVector(matrix[i].clone(), VectorOrientation.ROW_MAJOR);
        }

        this.vectors = newVectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        int len = matrix[0].length;
        SharedVector[] newVectors = new SharedVector[len];
        for(int i = 0; i < len; i++){
            double[] column = new double[matrix.length];
            for(int j = 0; j < matrix.length; j++){
                column[j] = matrix[j][i];
            }
            newVectors[i] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
        }
        this.vectors = newVectors;
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        return null;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return null;
    }

    public int length() {
        // TODO: return number of stored vectors
        return 0;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        return null;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
    }
}
