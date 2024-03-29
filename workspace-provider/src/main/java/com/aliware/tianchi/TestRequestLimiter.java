package com.aliware.tianchi;

import com.aliware.tianchi.common.RequestStat;
import com.aliware.tianchi.common.RtStat;
import com.aliware.tianchi.common.TimeSliceOps;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.RequestLimiter;

/**
 * @author daofeng.xjf
 *
 * 服务端限流
 * 可选接口
 * 在提交给后端线程池之前的扩展，可以用于服务端控制拒绝请求
 */
public class TestRequestLimiter implements RequestLimiter {

    /**
     * @param request 服务请求
     * @param activeTaskCount 服务端对应线程池的活跃线程数
     * @return  false 不提交给服务端业务线程池直接返回，客户端可以在 Filter 中捕获 RpcException
     *          true 不限流
     */
    @Override
    public boolean tryAcquire(Request request, int activeTaskCount) {
        return RtStat.reject(activeTaskCount);

//        return false;
//        System.out.println("activeTaskCount: " + activeTaskCount);
//        if(TimeSliceOps.getCurrentSliceCost() > TimeSliceOps.slice || TimeSliceOps.getcurrentThreads() > activeTaskCount) {
//        if(TimeSliceOps.getCurrentSliceCost() > TimeSliceOps.slice) {
//            return false;
//        }
//        return true;
    }

}
