package spl.lae;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import parser.ComputationNode;
import parser.ComputationNodeType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class LinearAlgebraEngineTest {

    @Test
    @Timeout(5)
    void endToEndAddWorks() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(4);
        double[][] m1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] m2 = {{10.0, 20.0}, {30.0, 40.0}};

        ComputationNode n = new ComputationNode(ComputationNodeType.ADD, List.of(new ComputationNode(m1), new ComputationNode(m2)));
        double[][] out = engine.run(n).getMatrix();

        assertArrayEquals(new double[]{11.0, 22.0}, out[0], 1e-9);
        assertArrayEquals(new double[]{33.0, 44.0}, out[1], 1e-9);
    }

    @Test
    @Timeout(5)
    void endToEndMultiplyWorks() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(4);

        double[][] a = {{1, 2, 3}, {4, 5, 6}};
        double[][] b = {{7, 8}, {9, 10}, {11, 12}};

        ComputationNode n = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(new ComputationNode(a), new ComputationNode(b)));
        double[][] out = engine.run(n).getMatrix();

        assertArrayEquals(new double[]{58.0, 64.0}, out[0], 1e-9);
        assertArrayEquals(new double[]{139.0, 154.0}, out[1], 1e-9);
    }

    @Test
    @Timeout(5)
    void negateOperationProducesNegativeMatrix() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        double[][] a = {{1.0, -2.0}, {3.0, 0.0}};
        ComputationNode n = new ComputationNode(ComputationNodeType.NEGATE, List.of(new ComputationNode(a)));
        double[][] out = engine.run(n).getMatrix();

        assertArrayEquals(new double[]{-1.0, 2.0}, out[0], 1e-9);
        assertArrayEquals(new double[]{-3.0, 0.0}, out[1], 1e-9);
    }

    @Test
    @Timeout(5)
    void transposeNonSquareWorks() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        double[][] a = {{1,2,3}}; 
        ComputationNode n = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(new ComputationNode(a)));
        double[][] out = engine.run(n).getMatrix();

        assertEquals(3, out.length);
        assertEquals(1, out[0].length);
        assertArrayEquals(new double[]{1.0}, out[0], 1e-9);
        assertArrayEquals(new double[]{2.0}, out[1], 1e-9);
        assertArrayEquals(new double[]{3.0}, out[2], 1e-9);
    }

    @Test
    @Timeout(5)
    void nestedOperationsAreResolvedLeftToRight() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(4);

        double[][] A = {{1,2}, {3,4}}; // 2x2
        double[][] B = {{2,0}, {1,2}}; // 2x2
        double[][] C = {{10, 10}, {10, 10}};

        ComputationNode mul = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(new ComputationNode(A), new ComputationNode(B)));
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, List.of(mul, new ComputationNode(C)));

        double[][] out = engine.run(root).getMatrix();

        assertArrayEquals(new double[]{14.0,14.0}, out[0], 1e-9);
        assertArrayEquals(new double[]{20.0,18.0}, out[1], 1e-9);
    }


    @Test
    @Timeout(5)
    void loadAndComputeNullThrows() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        assertThrows(IllegalArgumentException.class, () -> engine.loadAndCompute(null));
    }

    @Test
    @Timeout(5)
    void runNullNodeThrows() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        assertThrows(IllegalArgumentException.class, () -> engine.run(null));
    }


    @Test
    @Timeout(5)
    void addMismatchedRowCountsThrows() {
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        double[][] A = {{1,2}}; 
        double[][] B = {{1,2},{3,4}}; 
        ComputationNode n = new ComputationNode(ComputationNodeType.ADD, List.of(new ComputationNode(A), new ComputationNode(B)));
        assertThrows(IllegalArgumentException.class, () -> engine.run(n));
    }

}
