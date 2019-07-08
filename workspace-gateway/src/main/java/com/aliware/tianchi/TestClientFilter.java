package com.aliware.tianchi;

import com.aliware.tianchi.common.RequestStat;
import com.aliware.tianchi.common.TimeSliceOps;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author daofeng.xjf
 *
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try{
            Result result = invoker.invoke(invocation);
            TimeSliceOps.request4Stat(invoker, RequestStat.OK);
            return result;
        }catch (Exception e){
            TimeSliceOps.request4Stat(invoker, RequestStat.FAIL);
//            throw e;
        }

        return null;
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }
}
