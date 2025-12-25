package scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {
    private TiredThread thread;
    private final double fatigueFactor = 1.5;

    @BeforeEach
    void setUp() {
        thread = new TiredThread(0, fatigueFactor);
    }

    @Test
    void testFatigueCalculation() throws InterruptedException {
        // עייפות ראשונית צריכה להיות 0 [cite: 330, 331]
        assertEquals(0.0, thread.getFatigue(), "Initial fatigue should be zero");

        thread.start();
        AtomicBoolean taskDone = new AtomicBoolean(false);

        // משימה שמבצעת "עבודה" קצרה
        thread.newTask(() -> {
            try {
                Thread.sleep(100); 
                taskDone.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // המתנה לסיום המשימה
        Thread.sleep(200);

        // בדיקת הנוסחה: fatigue = fatigueFactor * timeUsed [cite: 330]
        long timeUsed = thread.getTimeUsed();
        assertTrue(timeUsed > 0, "TimeUsed should be recorded after task");
        assertEquals(fatigueFactor * timeUsed, thread.getFatigue(), 0.001, "Fatigue calculation mismatch");
        
        thread.shutdown();
    }

    @Test
    void testCompareToLogic() {
        TiredThread thread1 = new TiredThread(1, 1.0);
        TiredThread thread2 = new TiredThread(2, 2.0);

        // במצב התחלתי (עייפות 0), הם צריכים להיות שווים
        assertEquals(0, thread1.compareTo(thread2));
    }

    @Test
    void testNewTaskExceptionOnDeadThread() {
        thread.shutdown();
        // אסור להוסיף משימה לעובד שלא בחיים
        assertThrows(IllegalStateException.class, () -> thread.newTask(() -> {}));
    }
}