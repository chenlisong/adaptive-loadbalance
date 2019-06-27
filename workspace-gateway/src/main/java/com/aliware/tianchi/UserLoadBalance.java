package com.aliware.tianchi;

import com.aliware.tianchi.common.TimeSliceOps;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoadBalance.class);


    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
//        System.out.println();
//        System.out.println("url 2 host is: " + url.getHost());
//        System.out.println("url keys is: " + url.getPath() + ", " + url.getAddress() + "," + url.getPort());
//        LOGGER.info("url 3 is: " + url.getHost());
//
//        LOGGER.info("invoke1 info : " + invokers.get(0).getUrl().getAddress());
//        LOGGER.info("invoke2 info : " + invokers.get(1).getUrl().getAddress());

//        String[] addrsses = new String[invokers.size()];
//
//        for(int i=0; i<invokers.size(); i++) {
//            addrsses[i] = invokers.get(i).getUrl().getAddress();
//        }
//        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
//        return invokers.get(StatisticOps.getIndex(addrsses));
        return TimeSliceOps.rebalance((List<Invoker<T>>)invokers);
    }
}
