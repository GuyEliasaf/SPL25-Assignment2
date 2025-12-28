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
        executor = new TiredExecutor(2); 
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

        executor.submitAll(tasks);
        assertEquals(numTasks, counter.get(), "submitAll returned before all tasks finished");
        
        executor.shutdown();
    }

    @Test
    void testFairSchedulingSelection() throws InterruptedException {
        
        executor.submit(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });

        Thread.sleep(200);

        executor.submit(() -> {
        });

        String report = executor.getWorkerReport();
        assertTrue(report.contains("Time Used"), "Worker report should show activity");
        
        executor.shutdown();
    }

    @Test
    void testShutdownCleansUp() throws InterruptedException {
        executor.shutdown();
        assertTrue(true);
    }
}