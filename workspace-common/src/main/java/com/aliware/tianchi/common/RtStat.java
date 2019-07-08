package com.aliware.tianchi.common;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class RtStat {
    private static final Logger LOGGER = LoggerFactory.getLogger(RtStat.class);

    private static volatile Long startTime = null;

    private static volatile Integer baseAvgRt = null;

    private static LongAdder prepareCost = new LongAdder();

    private static LongAdder prepareCount = new LongAdder();

    private static int slice = 2000;

    private static long prepareDurationTimes = 10 * 1000L;

    private static volatile int currentActiveCountTask = 10;

    private static LongAdder upCost = new LongAdder();

    private static LongAdder upCount = new LongAdder();

    private static String nothing = "";

    public static void main(String[] args) {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(()->{
            System.out.println("123");
        },5500,500, TimeUnit.MILLISECONDS);
    }

    public static void stat(long cost, long now) {
        if(startTime == null) {
            synchronized(nothing) {
                if(startTime == null) {
                    startTime = now;
                }
            }
        }

        if(baseAvgRt == null) {
            if(now - startTime > prepareDurationTimes) {
                synchronized (nothing) {
                    if(baseAvgRt == null) {
                        baseAvgRt = prepareCost.intValue() / prepareCount.intValue();
                        startTime = now;
                    }
                }
            }else {
                prepareCount.increment();
                prepareCost.add(cost);
            }
        }else {
            upCost.add(cost);
            upCount.increment();

            if(now - startTime > slice) {
                synchronized (nothing) {
                    if(now - startTime > slice) {
                        updateActive();
                    }
                }
            }
        }
    }


    public static boolean reject(int activeTaskCount) {
//        System.out.println("activeTaskCount: " + activeTaskCount + ", baseAvgRt" + baseAvgRt);
        return activeTaskCount <= currentActiveCountTask;
    }

    public static void updateActive() {
        int newAvgRt = upCost.intValue() / upCount.intValue();
        if(newAvgRt < baseAvgRt * 1.1) {
            updateNextActiveTaskCount(currentActiveCountTask);
        }else {
            updateBeforeActiveTaskCount(currentActiveCountTask);
        }
        LOGGER.info("currentActiveCountTask: " + currentActiveCountTask + ", baseAvgRt: " + baseAvgRt + ", newAvgRt: " + newAvgRt);upCost.reset();
        upCount.reset();
        upCost.reset();
        startTime = System.currentTimeMillis();
    }

    public static void updateNextActiveTaskCount(int activeCountTask) {
        currentActiveCountTask = activeCountTask + getGap(activeCountTask);
        currentActiveCountTask = currentActiveCountTask > 650 ? 649 : currentActiveCountTask;
    }

    public static void updateBeforeActiveTaskCount(int activeCountTask) {
        int result = activeCountTask - getGap(activeCountTask);
        currentActiveCountTask = result > 10 ? result : 10;
    }

    private static int getGap(int activeCountTask) {
        if(activeCountTask < 190) {
            return 45;
        }else if(activeCountTask < 200) {
            return 3;
        }else if(activeCountTask < 440) {
            return 40;
        }else if(activeCountTask < 449) {
            return 3;
        }else if(activeCountTask < 650) {
            return 50;
        }else{
            return 80;
        }
    }

}
