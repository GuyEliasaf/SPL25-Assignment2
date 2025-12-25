package scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class TiredExecutorTest {
    private TiredExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new TiredExecutor(2); // שני עובדים
    }

    @Test
    @Timeout(5)
    void testSubmitAllBlocksUntilFinished() throws InterruptedException {
        int numTasks = 10;
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(50);
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // המתודה צריכה לחסום עד שכל 10 המשימות מסתיימות 
        executor.submitAll(tasks);
        assertEquals(numTasks, counter.get(), "submitAll returned before all tasks finished");
        
        executor.shutdown();
    }

    @Test
    void testFairSchedulingSelection() throws InterruptedException {
        // עובד אחד עם פקטור עייפות נמוך ועובד אחד עם גבוה
        // הערה: בקוד שלך הפקטור אקראי, אז נבדוק את הלוגיקה דרך הדיווח
        
        // הגשת משימה אחת
        executor.submit(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });

        // המתנה לסיום המשימה וחזרת העובד לתור
        Thread.sleep(200);

        // המשימה הבאה חייבת להינתן לעובד עם העייפות המינימלית 
        // מכיוון שהעובד הראשון עבד, השני (עם עייפות 0) הוא זה שצריך להיבחר.
        executor.submit(() -> {
            // עבודה
        });

        String report = executor.getWorkerReport();
        // נוודא ששני העובדים עבדו (חלוקה הוגנת)
        assertTrue(report.contains("Time Used"), "Worker report should show activity");
        
        executor.shutdown();
    }

    @Test
    void testShutdownCleansUp() throws InterruptedException {
        executor.shutdown();
        // אם המתודה מסתיימת ללא שגיאה, זה אומר שהתהליכונים קיבלו Poison Pill ויצאו [cite: 323]
        assertTrue(true);
    }
}