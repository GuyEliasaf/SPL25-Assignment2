package scheduling;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class TiredThreadTest {

    @Test
    void newTaskNullThrows() throws InterruptedException {
        TiredThread t = new TiredThread(0, 1.0);
        t.start();
        try {
            assertThrows(IllegalArgumentException.class, () -> t.newTask(null));
        } finally {
            t.shutdown();
            t.join(500);
        }
    }

    @Test
    void newTaskOfferFullThrowsWhenNotStarted() {
        TiredThread t = new TiredThread(1, 1.0);
        t.newTask(() -> {});
        assertThrows(IllegalStateException.class, () -> t.newTask(() -> {}));
    }

    @Test
    void newTaskThrowsWhenWorkerNotAlive() throws InterruptedException {
        TiredThread t = new TiredThread(2, 1.0);
        t.shutdown(); 
        assertThrows(IllegalStateException.class, () -> t.newTask(() -> {}));
    }

    @Test
    @Timeout(5)
    void busyFlagReflectsRunningTaskAndThreadSurvivesException() throws InterruptedException {
        TiredThread t = new TiredThread(3, 1.0);
        t.start();
        try {
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);

            boolean offered = false;
            long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(1000);
            while (!offered && System.nanoTime() < deadline) {
                try {
                    t.newTask(() -> {
                        started.countDown();
                        try {
                            release.await(1, TimeUnit.SECONDS);
                        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    });
                    offered = true;
                } catch (IllegalStateException e) {
                    Thread.sleep(5);
                }
            }

            assertTrue(offered, "output:[" + offered + "] expected:[true]");

            boolean startedOk = started.await(500, TimeUnit.MILLISECONDS);
            assertTrue(startedOk, "output:[" + startedOk + "] expected:[true]");


            boolean throwOffered = false;
            long throwDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(1000);
            while (!throwOffered && System.nanoTime() < throwDeadline) {
                try {
                    t.newTask(() -> { throw new RuntimeException("boom"); });
                    throwOffered = true;
                } catch (IllegalStateException e) {
                    Thread.sleep(5);
                }
            }

            release.countDown();

            AtomicBoolean completed = new AtomicBoolean(false);
            boolean completeOffered = false;
            long completeDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(1000);
            while (!completeOffered && System.nanoTime() < completeDeadline) {
                try {
                    t.newTask(() -> completed.set(true));
                    completeOffered = true;
                } catch (IllegalStateException e) {
                    Thread.sleep(5);
                }
            }
            assertTrue(completeOffered, "output:[" + completeOffered + "] expected:[true]");

            for (int i = 0; i < 50 && !completed.get(); i++) Thread.sleep(10);
            boolean completedOk = completed.get();
            assertTrue(completedOk, "output:[" + completedOk + "] expected:[true]");

        } finally {
            t.shutdown();
            t.join(1000);
        }
    }

    @Test
    void compareToReflectsFatigue() {
        TiredThread a = new TiredThread(4, 2.0);
        TiredThread b = new TiredThread(5, 1.0);

        a.addTimeUsed(100); 
        b.addTimeUsed(50);  

        int cmpAB = a.compareTo(b);
        assertTrue(cmpAB > 0, "output:[" + cmpAB + "] expected:[>0]");
        int cmpBA = b.compareTo(a);
        assertTrue(cmpBA < 0, "output:[" + cmpBA + "] expected:[<0]");

        b.addTimeUsed(75); 
        assertTrue(a.compareTo(b) > 0, "output:[" + a.compareTo(b) + "] expected:[>0]");

        b.addTimeUsed(75); 
        assertEquals(0, a.compareTo(b), "output:[" + a.compareTo(b) + "] expected:[0]");
    }

    @Test
    @Timeout(5)
    void timeIdleAndTimeUsedAccounting() throws InterruptedException {
        TiredThread t = new TiredThread(6, 1.0);
        t.start();
        try {
            Thread.sleep(50);

            long beforeIdle = t.getTimeIdle();

            CountDownLatch started = new CountDownLatch(1);

            t.newTask(() -> {
                started.countDown();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });

            boolean startedOk = started.await(200, TimeUnit.MILLISECONDS);
            assertTrue(startedOk, "output:[" + startedOk + "] expected:[true]");

            t.addTimeUsed(500);

            Thread.sleep(50);

            long afterIdle = t.getTimeIdle();
            assertTrue(afterIdle > beforeIdle, "output:[" + afterIdle + "] expected:[>" + beforeIdle + "]");
            long used = t.getTimeUsed();
            assertTrue(used >= 500, "output:[" + used + "] expected:[>=500]");

        } finally {
            t.shutdown();
            t.join(1000);
        }
    }

    @Test
    @Timeout(5)
    void shutdownTerminatesThread() throws InterruptedException {
        TiredThread t = new TiredThread(7, 1.0);
        t.start();
        t.shutdown();
        t.join(1000);
        boolean alive = t.isAlive();
        assertFalse(alive, "output:[" + alive + "] expected:[false]");
    }
}
