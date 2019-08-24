/**
 * Copyright (c) 1995-2009 Wonders Information Co.,Ltd.
 * 1518 Lianhang Rd,Shanghai 201112.P.R.C.
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Wonders Group.
 * (Social Security Department). You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with Wonders Group.
 * <p>
 * Distributable under GNU LGPL license by gnu.org
 */

package com.xiaoxinyun.answerweb.config;

import com.xiaoxinyun.commonutil.utils.CommUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 启动时初始化数据
 *
 * @author ucmed
 */
@Component
public class SystemInit implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(SystemInit.class);

    public static int a = 0;



    @Value("${server.port}")
    public String port;



    @Override
    public void afterPropertiesSet() throws Exception {
        if (a++ > 0) {
            return;
        }



        //输入swagger的 url
        String url = CommUtil.format("http://localhost:{0}/swagger-ui.html#/", port);
        logger.info(url);



        logger.info("初始化数据加载结束......................");
    }

}
