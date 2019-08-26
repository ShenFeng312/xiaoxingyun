package com.xiaoxinyun.answerservice.config.dubbo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * 验证用户信息
 */
@Slf4j
//@Activate(group = "answer")
public class VerifyFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        log.info("DbProviderContextFilter attachments={}", RpcContext.getContext().getAttachments());

        String token = RpcContext.getContext().getAttachment("token");
        log.info("获取的token值为：" + token);

        return invoker.invoke(invocation);


    }


}