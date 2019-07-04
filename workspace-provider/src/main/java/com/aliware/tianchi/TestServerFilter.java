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
 * 服务端过滤器
 * 可选接口
 * 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long begin = System.currentTimeMillis();
        try{
            Result result = invoker.invoke(invocation);
//            TimeSliceOps.operate4Cost(System.currentTimeMillis() - begin);
            return result;
        }catch (Exception e){
//            System.out.println("4444444....");
            //TimeSliceOps.operate(RequestStat.FAIL, System.currentTimeMillis() - begin);
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
//        if(result.hasException()) {
//            System.out.println("222222222: " + result.hasException() + result.getValue().toString());
//        }
        return result;
    }

}
