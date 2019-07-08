package com.aliware.tianchi;

import com.aliware.tianchi.common.RequestStat;
import com.aliware.tianchi.common.StatisticOps;
import com.aliware.tianchi.common.TimeSlice;
import com.aliware.tianchi.common.TimeSliceOps;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.listener.CallbackListener;

import java.util.Map;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 *
 */
public class CallbackListenerImpl implements CallbackListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackListenerImpl.class);

    @Override
    public void receiveServerMsg(String msg) {
        System.out.println("receive msg from server :" + msg);

        Map<Invoker, TimeSlice> invokeTsMap = TimeSliceOps.getInvokeTsMap();
        if(invokeTsMap.size() > 0) {
            invokeTsMap.forEach((invoker, ts)-> {
//                LOGGER.info("address: " + invoker.getUrl().getAddress()+ ", index: "+ts.beforeIndex() + ", ok count: " + ts.beforeCount(RequestStat.OK)
//                        + ", fail count: " + ts.beforeCount(RequestStat.FAIL));
                System.out.println("address: " + invoker.getUrl().getAddress()+ ", index: "+ts.beforeIndex() + ", ok count: " + ts.beforeCount(RequestStat.OK)
                        + ", fail count: " + ts.beforeCount(RequestStat.FAIL));
            });
        }

    }

}
