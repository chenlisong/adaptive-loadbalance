package com.aliware.tianchi.common;

public class TimeSliceOps {

    private final static TimeSlice ts = new TimeSlice();

    public static void operate(RequestStat stat) {
        ts.request(stat);
    }

    public static int getBeforeIndex() {
        return ts.beforeIndex();
    }

    public static int getBeforeCount(RequestStat stat) {
        return ts.beforeCount(stat);
    }

    public static void main(String[] args) {
        TimeSliceOps.operate(RequestStat.FAIL);
    }

}
