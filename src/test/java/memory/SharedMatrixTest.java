package memory;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class SharedMatrixTest {

    // ----------------- Constructor tests -----------------

    @Test
    @DisplayName("constructor: null input -> empty matrix")
    void constructorNullProducesEmpty() {
        SharedMatrix m = new SharedMatrix(null);
        assertEquals(0, m.length(), "output:[" + m.length() + "] expected:[0]");
    }

    @Test
    @DisplayName("constructor: empty array -> empty matrix")
    void constructorEmptyArrayProducesEmpty() {
        SharedMatrix m = new SharedMatrix(new double[0][0]);
        assertEquals(0, m.length(), "output:[" + m.length() + "] expected:[0]");
    }

    @Test
    @DisplayName("constructor: row null -> throws")
    void constructorRowNullThrows() {
        double[][] data = { null };
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(data));
    }

    @Test
    @DisplayName("constructor: inconsistent row lengths -> throws")
    void constructorInconsistentRowsThrows() {
        double[][] data = { {1,2}, {1,2,3} };
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(data));
    }

    @Test
    @DisplayName("constructor: zero-length row -> throws")
    void constructorZeroLengthRowThrows() {
        double[][] data = { {} };
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(data));
    }

    // ----------------- loadRowMajor tests -----------------

    @Test
    @DisplayName("loadRowMajor: normal matrix")
    void loadRowMajorNormal() {
        double[][] data = { {1.0,2.0}, {3.0,4.0} };
        SharedMatrix m = new SharedMatrix();
        m.loadRowMajor(data);
        assertEquals(2, m.length(), "output:[" + m.length() + "] expected:[2]");
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation(), "output:[" + m.getOrientation() + "] expected:[" + VectorOrientation.ROW_MAJOR + "]");
        assertEquals(1.0, m.get(0).get(0), 1e-9, "output:[" + m.get(0).get(0) + "] expected:[1.0]");
        assertEquals(4.0, m.get(1).get(1), 1e-9, "output:[" + m.get(1).get(1) + "] expected:[4.0]");
    }

    @Test
    @DisplayName("loadRowMajor: null -> clears matrix")
    void loadRowMajorNullClears() {
        SharedMatrix m = new SharedMatrix(new double[][]{{1}});
        m.loadRowMajor(null);
        assertEquals(0, m.length(), "output:[" + m.length() + "] expected:[0]");
    }

    @Test
    @DisplayName("loadRowMajor: empty array -> empty matrix")
    void loadRowMajorEmptyArray() {
        SharedMatrix m = new SharedMatrix();
        m.loadRowMajor(new double[0][0]);
        assertEquals(0, m.length(), "output:[" + m.length() + "] expected:[0]");
    }

    @Test
    @DisplayName("loadRowMajor: null row throws")
    void loadRowMajorNullRowThrows() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = { {1,2}, null };
        assertThrows(IllegalArgumentException.class, () -> m.loadRowMajor(data));
    }

    @Test
    @DisplayName("loadRowMajor: jagged rows -> throws")
    void loadRowMajorJaggedThrows() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = { {1,2,3}, {4,5} };
        assertThrows(IllegalArgumentException.class, () -> m.loadRowMajor(data));
    }

    @Test
    @DisplayName("loadRowMajor: row of length zero -> throws via SharedVector")
    void loadRowMajorZeroLengthRowThrows() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = { {} };
        assertThrows(IllegalArgumentException.class, () -> m.loadRowMajor(data));
    }

    // ----------------- loadColumnMajor tests -----------------

    @Test
    @DisplayName("loadColumnMajor: normal transpose")
    void loadColumnMajorNormal() {
        double[][] rows = { {1,2,3}, {4,5,6} };
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(rows);

        assertEquals(3, m.length(), "output:[" + m.length() + "] expected:[3]");
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation(), "output:[" + m.getOrientation() + "] expected:[" + VectorOrientation.COLUMN_MAJOR + "]");
        assertEquals(1.0, m.get(0).get(0), 1e-9, "output:[" + m.get(0).get(0) + "] expected:[1.0]");
        assertEquals(4.0, m.get(0).get(1), 1e-9, "output:[" + m.get(0).get(1) + "] expected:[4.0]");

        double[][] back = m.readRowMajor();
        assertEquals(2, back.length, "output:[" + back.length + "] expected:[2]");
        assertArrayEquals(rows[0], back[0], 1e-9, "output:" + Arrays.toString(back[0]) + " expected:" + Arrays.toString(rows[0]));
        assertArrayEquals(rows[1], back[1], 1e-9, "output:" + Arrays.toString(back[1]) + " expected:" + Arrays.toString(rows[1]));
    }

    @Test
    @DisplayName("loadColumnMajor: single-row matrix")
    void loadColumnMajorSingleRow() {
        double[][] rows = { {7,8,9} };
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(rows);
        assertEquals(3, m.length(), "output:[" + m.length() + "] expected:[3]"); // 3 columns
        assertEquals(1, m.get(0).length(), "output:[" + m.get(0).length() + "] expected:[1]");
        double[][] back = m.readRowMajor();
        assertArrayEquals(rows[0], back[0], 1e-9, "output:" + Arrays.toString(back[0]) + " expected:" + Arrays.toString(rows[0]));
    }

    @Test
    @DisplayName("loadColumnMajor: null -> clears matrix")
    void loadColumnMajorNullClears() {
        SharedMatrix m = new SharedMatrix(new double[][]{{1,1}});
        m.loadColumnMajor(null);
        assertEquals(0, m.length(), "output:[" + m.length() + "] expected:[0]");
    }

    @Test
    @DisplayName("loadColumnMajor: null row throws")
    void loadColumnMajorNullRowThrows() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = { {1,2}, null };
        assertThrows(IllegalArgumentException.class, () -> m.loadColumnMajor(data));
    }

    @Test
    @DisplayName("loadColumnMajor: jagged rows -> throws")
    void loadColumnMajorJaggedThrows() {
        SharedMatrix m = new SharedMatrix();
        double[][] data = { {1,2,3}, {4,5} };
        assertThrows(IllegalArgumentException.class, () -> m.loadColumnMajor(data));
    }

    // ----------------- readRowMajor tests -----------------

    @Test
    @DisplayName("readRowMajor: empty returns empty 2D array")
    void readRowMajorEmpty() {
        SharedMatrix m = new SharedMatrix();
        double[][] r = m.readRowMajor();
        assertEquals(0, r.length, "output:[" + r.length + "] expected:[0]");
    }

    @Test
    @DisplayName("readRowMajor: deep copy - modifying result does not change matrix")
    void readRowMajorDeepCopy() {
        double[][] data = { {1,1}, {2,2} };
        SharedMatrix m = new SharedMatrix(data);
        double[][] r = m.readRowMajor();
        r[0][0] = 999;
        assertEquals(1.0, m.get(0).get(0), 1e-9, "output:[" + m.get(0).get(0) + "] expected:[1.0]");
    }

    @Test
    @DisplayName("readRowMajor: round-trip row<->column correctness")
    void readRowMajorRoundTrip() {
        double[][] original = { {1,2,3}, {4,5,6} };
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(original);
        double[][] out = m.readRowMajor();
        assertEquals(2, out.length, "output:[" + out.length + "] expected:[2]");
        assertArrayEquals(original[0], out[0], 1e-9, "output:" + Arrays.toString(out[0]) + " expected:" + Arrays.toString(original[0]));
        assertArrayEquals(original[1], out[1], 1e-9, "output:" + Arrays.toString(out[1]) + " expected:" + Arrays.toString(original[1]));
    }

    // ----------------- indexing & mutation tests -----------------

    @Test
    @DisplayName("get: index out of bounds throws")
    void getIndexOutOfBounds() {
        SharedMatrix m = new SharedMatrix(new double[][]{{1.0,2.0}});
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(-1));
    }

    @Test
    @DisplayName("get: modifying returned SharedVector mutates the matrix")
    void getModifiesMatrix() {
        double[][] data = { {1.0,2.0} };
        SharedMatrix m = new SharedMatrix(data);
        SharedVector v = m.get(0);
        v.negate(); 
        double[][] r = m.readRowMajor();
        assertEquals(-1.0, r[0][0], 1e-9, "output:[" + r[0][0] + "] expected:[-1.0]");
        assertEquals(-2.0, r[0][1], 1e-9, "output:[" + r[0][1] + "] expected:[-2.0]");
    }

    @Test
    @DisplayName("length and orientation reflect last load")
    void lengthAndOrientationAfterReload() {
        SharedMatrix m = new SharedMatrix(new double[][]{{1,2}});
        assertEquals(1, m.length(), "output:[" + m.length() + "] expected:[1]");
        // load columns
        m.loadColumnMajor(new double[][]{{1,2},{3,4}});
        assertEquals(2, m.length(), "output:[" + m.length() + "] expected:[2]");
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation(), "output:[" + m.getOrientation() + "] expected:[" + VectorOrientation.COLUMN_MAJOR + "]");
        // load rows
        m.loadRowMajor(new double[][]{{9,9,9}});
        assertEquals(1, m.length(), "output:[" + m.length() + "] expected:[1]");
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation(), "output:[" + m.getOrientation() + "] expected:[" + VectorOrientation.ROW_MAJOR + "]");
    }

    @Test
    @DisplayName("getOrientation: empty matrix throws")
    void getOrientationEmptyThrows() {
        SharedMatrix m = new SharedMatrix();
        assertThrows(IllegalStateException.class, () -> m.getOrientation());
    }

    // ----------------- small stress / consistency checks -----------------

    @Test
    @DisplayName("multiple loads keep consistency")
    void multipleLoadsConsistency() {
        SharedMatrix m = new SharedMatrix();
        m.loadRowMajor(new double[][]{{1,2},{3,4}});
        m.loadColumnMajor(new double[][]{{5,6},{7,8}});
        assertEquals(2, m.length(), "output:[" + m.length() + "] expected:[2]");
        double[][] out = m.readRowMajor();
        assertEquals(2, out.length, "output:[" + out.length + "] expected:[2]");
    }

}
