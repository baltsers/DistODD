package ODD;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class TaskTest {
    static class Task implements Callable<Boolean> {
        public String name;
        private int time;

        public Task(String s, int t) {
            name = s;
            time = t;
        }

        @Override
        public Boolean call() throws Exception {
            for (int i = 0; i < time; ++i) {
                System.out.println("task " + name + " round " + (i + 1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(name
                            + " is interrupted when calculating, will stop...");
                    return false; // 
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Task task1 = new Task("one", 5);
        Future<Boolean> f1 = executor.submit(task1);
        try {
            if (f1.get(2000, TimeUnit.MILLISECONDS)) { // 
                System.out.println("one complete successfully");
            }
        } catch (InterruptedException e) {
            System.out.println("future was interrupted during the sleeping");
            executor.shutdownNow();
        } catch (ExecutionException e) {
            System.out.println("future has mistakes during getting the result");
            executor.shutdownNow();
        } catch (TimeoutException e) {
            System.out.println("future is timeoouts");
            f1.cancel(true);
            // executor.shutdownNow();
            // executor.shutdown();
        } finally {
            executor.shutdownNow();
        }
    }
}
