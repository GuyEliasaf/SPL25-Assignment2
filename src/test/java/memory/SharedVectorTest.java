package memory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class SharedVectorTest {

    // ----------------- Constructor & basic access -----------------

    @Test
    @DisplayName("constructor: null vector -> NullPointerException")
    void constructorNullVectorThrows() {
        assertThrows(NullPointerException.class, () -> new SharedVector(null, VectorOrientation.ROW_MAJOR));
    }

    @Test
    @DisplayName("constructor: zero-length vector -> IllegalArgumentException")
    void constructorZeroLengthThrows() {
        assertThrows(IllegalArgumentException.class, () -> new SharedVector(new double[0], VectorOrientation.ROW_MAJOR));
    }

    @Test
    @DisplayName("length and get return correct values")
    void lengthAndGet() {
        SharedVector v = new SharedVector(new double[]{1.5, -2.5, 3.0}, VectorOrientation.ROW_MAJOR);
        assertEquals(3, v.length());
        assertEquals(1.5, v.get(0), 1e-9);
        assertEquals(-2.5, v.get(1), 1e-9);
        assertEquals(3.0, v.get(2), 1e-9);
    }

    @Test
    @DisplayName("get: index out of bounds throws")
    void getIndexOutOfBounds() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(-1));
    }

    // ----------------- Orientation & mutation -----------------

    @Test
    @DisplayName("transpose flips orientation and keeps values")
    void transposeBehavior() {
        SharedVector v = new SharedVector(new double[]{2.0, 4.0}, VectorOrientation.ROW_MAJOR);
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
        assertEquals(2.0, v.get(0), 1e-9);
        assertEquals(4.0, v.get(1), 1e-9);

        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    @DisplayName("negate flips the sign of all elements")
    void negateBehavior() {
        SharedVector v = new SharedVector(new double[]{1.0, -3.5, 0.0}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(-1.0, v.get(0), 1e-9);
        assertEquals(3.5, v.get(1), 1e-9);
        assertEquals(0.0, v.get(2), 1e-9);
    }

    @Test
    @DisplayName("explicit locks can be used and released")
    void lockUnlockMethods() {
        SharedVector v = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
        v.readLock();
        v.readUnlock();
        v.writeLock();
        v.writeUnlock();
        // If we reach here without exceptions, locks work as expected
        assertEquals(1, v.length());
    }

    // ----------------- add (in-place) -----------------

    @Test
    @DisplayName("add: normal case with same orientation and length")
    void addNormalCase() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{4.0,5.0,6.0}, VectorOrientation.ROW_MAJOR);
        a.add(b);
        assertArrayEquals(new double[]{5.0,7.0,9.0}, new double[]{a.get(0), a.get(1), a.get(2)}, 1e-9);
    }

    @Test
    @DisplayName("add: null other throws IllegalArgumentException")
    void addNullOtherThrows() {
        SharedVector a = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.add(null));
    }

    @Test
    @DisplayName("add: mismatched lengths throws")
    void addMismatchedLengthThrows() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    @DisplayName("add: mismatched orientation throws")
    void addMismatchedOrientationThrows() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3.0,4.0}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    // ----------------- dot product -----------------

    @Test
    @DisplayName("dot: null other throws IllegalArgumentException")
    void dotNullThrows() {
        SharedVector a = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.dot(null));
    }

    @Test
    @DisplayName("dot: mismatched lengths throws")
    void dotMismatchedLengthsThrows() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{1.0}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.dot(b));
    }

    @Test
    @DisplayName("dot: first vector must be ROW_MAJOR")
    void dotFirstNotRowThrows() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.COLUMN_MAJOR);
        SharedVector b = new SharedVector(new double[]{3.0,4.0}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.dot(b));
    }

    @Test
    @DisplayName("dot: second vector must be COLUMN_MAJOR")
    void dotSecondNotColumnThrows() {
        SharedVector a = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3.0,4.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> a.dot(b));
    }

    @Test
    @DisplayName("dot: basic computation")
    void dotBasicComputation() {
        SharedVector row = new SharedVector(new double[]{1.0,2.0,3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector col = new SharedVector(new double[]{4.0,5.0,6.0}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(32.0, row.dot(col), 1e-9);
    }

    @Test
    @DisplayName("dot: single-element non-row allowed and computed")
    void dotSingleElementNonRow() {
        SharedVector a = new SharedVector(new double[]{7.0}, VectorOrientation.COLUMN_MAJOR);
        SharedVector b = new SharedVector(new double[]{3.0}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(21.0, a.dot(b), 1e-9);
    }

    // ----------------- vecMatMul -----------------

    @Test
    @DisplayName("vecMatMul: null matrix throws IllegalArgumentException")
    void vecMatMulNullThrows() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(null));
    }

    @Test
    @DisplayName("vecMatMul: wrong orientation throws")
    void vecMatMulWrongOrientationThrows() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0,0.0},{0.0,1.0}});
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    @DisplayName("vecMatMul: dimension mismatch throws")
    void vecMatMulDimensionMismatchThrows() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0},{2.0},{3.0}}); // 3 rows
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    @DisplayName("vecMatMul: identity preserves vector")
    void vecMatMulIdentity() {
        SharedVector v = new SharedVector(new double[]{5.0,10.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0,0.0},{0.0,1.0}});
        v.vecMatMul(m);
        assertEquals(5.0, v.get(0), 1e-9);
        assertEquals(10.0, v.get(1), 1e-9);
    }

    @Test
    @DisplayName("vecMatMul: rectangular multiplication")
    void vecMatMulRectangular() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0,2.0,3.0},{4.0,5.0,6.0}});
        v.vecMatMul(m);
        assertEquals(3, v.length());
        assertEquals(9.0, v.get(0), 1e-9);
        assertEquals(12.0, v.get(1), 1e-9);
        assertEquals(15.0, v.get(2), 1e-9);
    }

    @Test
    @DisplayName("vecMatMul: multiplication by empty matrix yields empty vector")
    void vecMatMulEmptyMatrixYieldsEmpty() {
        SharedVector v = new SharedVector(new double[]{1.0,2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix empty = new SharedMatrix(new double[0][0]);
        v.vecMatMul(empty);
        assertEquals(0, v.length());
    }

    @Test
    @DisplayName("vecMatMul: single-row matrix expands vector")
    void vecMatMulSingleRow() {
        SharedVector v = new SharedVector(new double[]{2.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{3.0,4.0}});
        v.vecMatMul(m);
        assertEquals(2, v.length());
        assertEquals(6.0, v.get(0), 1e-9);
        assertEquals(8.0, v.get(1), 1e-9);
    }

    // ----------------- Large / stress checks -----------------

    @Test
    @DisplayName("add: large vectors (length 100)")
    void addLargeVectors() {
        int n = 100;
        double[] aData = new double[n];
        double[] bData = new double[n];
        for (int i = 0; i < n; i++) { aData[i] = i; bData[i] = n - i; }
        SharedVector a = new SharedVector(aData, VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(bData, VectorOrientation.ROW_MAJOR);
        a.add(b);
        for (int i = 0; i < n; i++) assertEquals(n, a.get(i), 1e-9);
    }

    @Test
    @DisplayName("dot: large vector dot product")
    void dotLarge() {
        int n = 50;
        double[] row = new double[n];
        double[] col = new double[n];
        for (int i = 0; i < n; i++) { row[i] = i; col[i] = 1; }
        SharedVector r = new SharedVector(row, VectorOrientation.ROW_MAJOR);
        SharedVector c = new SharedVector(col, VectorOrientation.COLUMN_MAJOR);
        double expected = 0;
        for (int i = 0; i < n; i++) expected += i;
        assertEquals(expected, r.dot(c), 1e-9);
    }

}
