package com.spiderrobotman.Gamemode4Engine.util;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 20 2016
 * Website: http://www.spiderrobotman.com
 */
public class TPS implements Runnable {
    public static long LAST_TICK = 0L;
    private static int TICK_COUNT = 0;
    private static long[] TICKS = new long[600];

    public static double getTPS() {
        return getTPS(100);
    }

    private static double getTPS(int ticks) {
        if (TICK_COUNT < ticks) {
            return 20.0D;
        }
        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return ticks / (elapsed / 1000.0D);
    }

    public static long getElapsed(int tickID) {
        long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();

        TICK_COUNT += 1;
    }
}
