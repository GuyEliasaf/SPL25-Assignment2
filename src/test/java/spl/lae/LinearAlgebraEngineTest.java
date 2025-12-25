package spl.lae;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import parser.ComputationNode;
import parser.ComputationNodeType;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraEngineTest {
    private LinearAlgebraEngine engine;

    @BeforeEach
    void setUp() {
        // אתחול המנוע עם 4 תהליכונים כפי שנדרש בהרצה [cite: 74, 311]
        engine = new LinearAlgebraEngine(4);
    }

    @Test
    @Timeout(5)
    void testEndToEndSimpleAddition() {
        // בדיקת זרימת נתונים מלאה: עץ עם פעולת חיבור אחת [cite: 82]
        double[][] m1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] m2 = {{10.0, 20.0}, {30.0, 40.0}};
        
        ComputationNode node1 = new ComputationNode(m1);
        ComputationNode node2 = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode(ComputationNodeType.ADD, List.of(node1, node2));

        // הרצת המנוע וקבלת המטריצה הסופית [cite: 177, 182]
        ComputationNode resultNode = engine.run(addNode);
        double[][] result = resultNode.getMatrix();

        assertArrayEquals(new double[]{11.0, 22.0}, result[0]);
        assertArrayEquals(new double[]{33.0, 44.0}, result[1]);
    }

    @Test
    void testMultiplyDimensionMismatch() {
        // בדיקת ולידציה: ניסיון להכפיל מטריצות עם מימדים לא תואמים 
        double[][] m1 = {{1, 2, 3}}; // 1x3
        double[][] m2 = {{1, 2}, {3, 4}}; // 2x2
        
        ComputationNode node1 = new ComputationNode(m1);
        ComputationNode node2 = new ComputationNode(m2);
        ComputationNode mulNode = new ComputationNode(ComputationNodeType.MULTIPLY, List.of(node1, node2));

        // המנוע אמור לזהות שגיאת מימדים ולזרוק Exception (שיטופל ב-Main) [cite: 195]
        assertThrows(IllegalArgumentException.class, () -> engine.run(mulNode));
    }

    

    @Test
    void testTransposeOperation() {
        // בדיקת פעולת טרנספוז וטעינה ל-Column Major [cite: 84]
        double[][] m = {{1, 2, 3}, {4, 5, 6}}; // 2x3
        ComputationNode node = new ComputationNode(m);
        ComputationNode transNode = new ComputationNode(ComputationNodeType.TRANSPOSE, List.of(node));

        double[][] result = engine.run(transNode).getMatrix(); // אמור להיות 3x2

        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(4, result[0][1]);
    }
}