package com.example.settlement;

/**
 *
 * @author yangwu_i
 * @date 2023/5/10 22:49
 */
public class TaskRunner {
    private static int number;
    private static boolean ready;

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }

            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new Reader().start();
        number = 42;
        ready = true;
    }
}
