package com.quantlogic.stream;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class Timer {
    private final AtomicInteger tick;
    private final long currentTimeMillis;

    private Timer() {
        currentTimeMillis = System.currentTimeMillis();
        tick = new AtomicInteger(0);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("total time "+ (System.currentTimeMillis() - currentTimeMillis))));
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(tick::incrementAndGet, 1, 1, TimeUnit.MILLISECONDS);
    }

    public static final Timer INSTANCE = new Timer();

    public int getCurrentTime(){
        return tick.get();
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        while (true){
            System.out.println(timer.tick.get());
        }
    }
}
