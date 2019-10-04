import helper.DummyServerTalker;
import helper.DummyUserInputManager;
import no.ntnu.trostespel.PlayerUpdateDispatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
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

        for (int i = 0; i < 8; i++) {
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
        lock.await(10000, TimeUnit.MILLISECONDS);

    }

}