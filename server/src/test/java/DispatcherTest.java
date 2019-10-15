import helper.DummyServerTalker;
import helper.DummyUserInputManager;
import no.ntnu.trostespel.PlayerUpdateDispatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DispatcherTest {

    PlayerUpdateDispatcher dispatcher;
    DummyUserInputManager manager;


    @Before
    public void before() {
        manager = new DummyUserInputManager();
        dispatcher = new PlayerUpdateDispatcher();
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    System.out.println("test");
                    new DummyServerTalker(dispatcher);
                }
            };
            r.run();
        }
        CountDownLatch lock = new CountDownLatch(1);
        lock.await(100000, TimeUnit.MILLISECONDS);

    }

    private long time = 0;
    @Test
    public void test2() throws InterruptedException {
        time = System.currentTimeMillis();
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        Runnable dispatcher = () -> {
            long currenttime = System.currentTimeMillis();
            System.out.println(currenttime - time);
            time = currenttime;
        };
        s.scheduleAtFixedRate(dispatcher, 0, 100000 / 3, TimeUnit.MICROSECONDS);
        CountDownLatch lock = new CountDownLatch(1);
        lock.await(10000, TimeUnit.MILLISECONDS);
    }

}