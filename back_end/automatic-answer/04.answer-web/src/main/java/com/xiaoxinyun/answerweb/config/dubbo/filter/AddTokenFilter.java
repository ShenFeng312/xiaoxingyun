package com.xiaoxinyun.answerweb.config.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;


//@Activate
//@Activate(group = {"answer"})
public class AddTokenFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //添加token
        RpcContext.getContext().setAttachment("token", "123456");
        return invoker.invoke(invocation);
    }
}