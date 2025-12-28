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
    void testCompareToLogic() {
        TiredThread thread1 = new TiredThread(1, 1.0);
        TiredThread thread2 = new TiredThread(2, 2.0);

        assertEquals(0, thread1.compareTo(thread2));
    }

    @Test
    void testNewTaskExceptionOnDeadThread() {
        thread.shutdown();
        assertThrows(IllegalStateException.class, () -> thread.newTask(() -> {}));
    }
}