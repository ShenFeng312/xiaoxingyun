package com.xiaoxinyun.answerweb.config.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.HashMap;
import java.util.Map;

//@Activate(group = "answer")
@Activate
public class AddTokenFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Map<String, String> attachments = RpcContext.getContext().getAttachments();
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put("token", "123456");
        //设置需要的内容
        RpcContext.getContext().setAttachments(attachments);
        return invoker.invoke(invocation);
    }
}