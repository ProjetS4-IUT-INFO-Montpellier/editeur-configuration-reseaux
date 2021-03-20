package cheron_ezzaamari_soysal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class ComposantTest {

    @Test
    void test_composant_avecConcurrance() throws InterruptedException {
        final int NOMBRE_DE_THREADS = 10;
        final int NOMBRE_DE_COMPOSANTS = 100;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(NOMBRE_DE_THREADS);
        Runnable creer100Composants = () -> {
            for (int i = 0; i < NOMBRE_DE_COMPOSANTS; i++) {
                new Composant("Switch");
                latch.countDown();
            }
        };
        for (int i = 0; i < NOMBRE_DE_THREADS; i++)
            service.execute(creer100Composants);
        latch.await();
        assertEquals(NOMBRE_DE_THREADS * NOMBRE_DE_COMPOSANTS, new Composant("Switch").getId());
    }

}
