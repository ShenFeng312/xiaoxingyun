package com.xiaoxinyun.answerservice.config.dubbo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.util.StringUtils;

/**
 * 验证用户信息
 */
@Slf4j
//@Activate(group = "answer")
public class VerifyFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String token = invocation.getAttachment("token");

        log.info("获取的token值为：" + token);

        return invoker.invoke(invocation);
    }


}