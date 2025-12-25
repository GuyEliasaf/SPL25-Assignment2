package memory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class SharedVectorTest {
    @Test
    @DisplayName ("adding two vectors with the same length")
    void testAddSucces (){
        SharedVector v1 = new SharedVector(new double[]{10, 20}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{5, 5}, VectorOrientation.ROW_MAJOR);
        SharedVector v3 = new SharedVector(new double[]{10, 20}, VectorOrientation.COLUMN_MAJOR);
        SharedVector v4 = new SharedVector(new double[]{5, 5}, VectorOrientation.COLUMN_MAJOR);
        v1.add(v2);
        v3.add(v4);
        assertEquals(15.0, v1.get(0), 0.001); // Assert
        assertEquals(25.0, v1.get(1), 0.001);
        assertEquals(15.0, v3.get(0), 0.001); // Assert
        assertEquals(25.0, v3.get(1), 0.001);

    }

    @Test
    @DisplayName ("adding two vectors with different lengths should throw exception")
    void testAddMisMatchLength (){
        SharedVector v1 = new SharedVector(new double[]{10, 20}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{5}, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, ()->{v1.add(v2);});

    }

    @Test
    @DisplayName ("adding two vectors with different lengths should throw exception")
    void testAddMisMatchMajor (){
        SharedVector v1 = new SharedVector(new double[]{10, 20}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{5}, VectorOrientation.ROW_MAJOR);
        
        assertThrows(IllegalArgumentException.class, ()->{v1.add(v2);});

    }

    @Test
    @DisplayName("transpose vector from row major to column major and back")
    void testTranspose (){
        SharedVector v1 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.COLUMN_MAJOR);
        v1.transpose();
        v2.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v1.getOrientation());
        assertEquals(VectorOrientation.ROW_MAJOR, v2.getOrientation());
        assertEquals(1.0, v1.get(0), 0.001); // Assert
        assertEquals(2.0, v1.get(1), 0.001);
        assertEquals(3.0, v1.get(2), 0.001);

        assertEquals(1.0, v2.get(0), 0.001); // Assert V2
        assertEquals(2.0, v2.get(1), 0.001);
        assertEquals(3.0, v2.get(2), 0.001);

        v1.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v1.getOrientation());
        assertEquals(1.0, v1.get(0), 0.001); // Assert V1 again
        assertEquals(2.0, v1.get(1), 0.001);
        assertEquals(3.0, v1.get(2), 0.001);
    }

    @Test
    @DisplayName("negate vector")
    void testNegate(){
        SharedVector v1 = new SharedVector(new double[]{1, -2, 3}, VectorOrientation.ROW_MAJOR);
        v1.negate();
        assertEquals(-1.0, v1.get(0), 0.001); // Assert
        assertEquals(2.0, v1.get(1), 0.001);
        assertEquals(-3.0, v1.get(2), 0.001);
    }
    @Test
    @DisplayName("dot product should throw exception when other is null")
    void testDotNull() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.dot(null));
    }

    @Test
    @DisplayName("dot product should throw exception for different lengths")
    void testDotMismatchLength() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.dot(v2));
    }

    @Test
    @DisplayName("dot product should throw exception if first vector is not Row-Major")
    void testDotFirstNotRow() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.COLUMN_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1, 2}, VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.dot(v2));
    }

    @Test
    @DisplayName("dot product should throw exception if second vector is not Column-Major")
    void testDotSecondNotColumn() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        assertThrows(IllegalArgumentException.class, () -> v1.dot(v2));
    }
    @Test
    @DisplayName("dot product basic calculation 1")
    void testDotSuccess1() {
        SharedVector row = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);
        SharedVector col = new SharedVector(new double[]{4.0, 5.0, 6.0}, VectorOrientation.COLUMN_MAJOR);
        
        // חישוב: (1*4) + (2*5) + (3*6) = 4 + 10 + 18 = 32.0
        double result = row.dot(col);
        assertEquals(32.0, result, 0.001);
    }

    @Test
    @DisplayName("dot product basic calculation 2 (with zeros and negatives)")
    void testDotSuccess2() {
        SharedVector row = new SharedVector(new double[]{0.0, -1.0, 5.0}, VectorOrientation.ROW_MAJOR);
        SharedVector col = new SharedVector(new double[]{10.0, 2.0, 4.0}, VectorOrientation.COLUMN_MAJOR);
        
        // חישוב: (0*10) + (-1*2) + (5*4) = 0 - 2 + 20 = 18.0
        double result = row.dot(col);
        assertEquals(18.0, result, 0.001);
    }

    @Test
    @DisplayName("vecMatMul should throw exception if vector is not ROW_MAJOR")
    void testVecMatMulWrongOrientation() {
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.COLUMN_MAJOR);
        SharedMatrix m = new SharedMatrix(new double[][]{{1, 0}, {0, 1}});
        
        // vector is COLUMN_MAJOR, should be ROW_MAJOR
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    @DisplayName("vecMatMul should throw exception if dimensions do not match")
    void testVecMatMulDimensionMismatch() {
        // vector length is 2
        SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        // matrix with 3 rows
        SharedMatrix m = new SharedMatrix(new double[][]{{1, 1}, {1, 1}, {1, 1}});
        
        // vector length (2) != number of rows in matrix (3)
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }

    @Test
    @DisplayName("vecMatMul with identity matrix should keep vector unchanged")
    void testVecMatMulIdentity() {
        SharedVector v = new SharedVector(new double[]{5.0, 10.0}, VectorOrientation.ROW_MAJOR);
        SharedMatrix identity = new SharedMatrix(new double[][]{
            {1.0, 0.0},
            {0.0, 1.0}
        });

        v.vecMatMul(identity);

        // [5, 10] * I = [5, 10]
        assertEquals(5.0, v.get(0), 0.001);
        assertEquals(10.0, v.get(1), 0.001);
    }

    @Test
    @DisplayName("vecMatMul with rectangular matrix")
    void testVecMatMulRectangular() {
        // vector 1x2
        SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        // matrix 2x3
        SharedMatrix m = new SharedMatrix(new double[][]{
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
        });

        v.vecMatMul(m);

        //expected calculations
        //  0: (1*1) + (2*4) = 9
        //  1: (1*2) + (2*5) = 12
        //  2: (1*3) + (2*6) = 15

        assertEquals(3, v.length(), "Vector length should change to number of matrix columns");
        assertEquals(9.0, v.get(0), 0.001);
        assertEquals(12.0, v.get(1), 0.001);
        assertEquals(15.0, v.get(2), 0.001);
    }
    @Test
@DisplayName("vecMatMul with single-row matrix")
void testVecMatMulSingleRow() {
    SharedVector v = new SharedVector(new double[]{2.0}, VectorOrientation.ROW_MAJOR);
    SharedMatrix m = new SharedMatrix(new double[][]{{3.0, 4.0}}); // matrix 1x2

    v.vecMatMul(m);

    // [2.0] * [3.0, 4.0] = [6.0, 8.0]
    assertEquals(2, v.length());
    assertEquals(6.0, v.get(0), 0.001);
    assertEquals(8.0, v.get(1), 0.001);
}

@Test
@DisplayName("vecMatMul resulting in length 1 vector")
void testVecMatMulSingleColumn() {
    SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
    SharedMatrix m = new SharedMatrix(new double[][]{
        {10.0}, 
        {20.0}
    }); // matrix 2x1

    v.vecMatMul(m);

    // [1*10 + 2*20] = [50.0]
    assertEquals(1, v.length());
    assertEquals(50.0, v.get(0), 0.001);
}

@Test
@DisplayName("vecMatMul with empty matrix")
void testVecMatMulEmptyMatrix() {
    SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
    // creating an empty matrix (0 rows)
    SharedMatrix emptyM = new SharedMatrix(new double[0][0]);

    v.vecMatMul(emptyM);

    assertEquals(0, v.length(), "Vector should become empty when multiplied by empty matrix");
}

@Test
@DisplayName("vecMatMul should throw exception when matrix is null")
void testVecMatMulNull() {
    SharedVector v = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
    
    // checking that the function throws an exception and not crashing with NullPointerException
    assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(null));
}

// --- Large Input Tests for SharedVector ---

    @Test
    @DisplayName("add: Large vector addition (10 elements)")
    void testAddLargeVectors() {
        // Arrange: Two vectors with 10 elements each
        double[] data1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] data2 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);

        // Act
        v1.add(v2);

        // Assert: Each position should sum up to 11.0
        for (int i = 0; i < 10; i++) {
            assertEquals(11.0, v1.get(i), 0.001, "Mismatch at index " + i);
        }
    }

    @Test
    @DisplayName("dot: Large scale dot product calculation")
    void testDotLargeVectors() {
        // Arrange: Row [1,1,1,1,1] and Column [1,2,3,4,5]
        double[] rowData = {1.0, 1.0, 1.0, 1.0, 1.0};
        double[] colData = {1.0, 2.0, 3.0, 4.0, 5.0};
        SharedVector row = new SharedVector(rowData, VectorOrientation.ROW_MAJOR);
        SharedVector col = new SharedVector(colData, VectorOrientation.COLUMN_MAJOR);

        // Act: (1*1 + 1*2 + 1*3 + 1*4 + 1*5) = 1+2+3+4+5 = 15
        double result = row.dot(col);

        // Assert
        assertEquals(15.0, result, 0.001);
    }

    @Test
    @DisplayName("vecMatMul: Large 1x5 Vector multiplied by 5x3 Matrix")
    void testVecMatMulLarge() {
        // Arrange: Vector 1x5
        SharedVector v = new SharedVector(new double[]{1, 1, 1, 1, 1}, VectorOrientation.ROW_MAJOR);
        
        // Matrix 5x3 filled with 2.0
        double[][] matrixData = new double[5][3];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 3; j++) {
                matrixData[i][j] = 2.0; 
            }
        }
        SharedMatrix m = new SharedMatrix(matrixData);

        // Act: Result should be [10.0, 10.0, 10.0]
        v.vecMatMul(m);

        // Assert
        assertEquals(3, v.length(), "Resulting vector should have length 3 (matrix columns)");
        for (int i = 0; i < 3; i++) {
            assertEquals(10.0, v.get(i), 0.001, "Mismatch at index " + i);
        }
    }

    
}