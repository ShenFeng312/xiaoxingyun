package com.xiaoxinyun.answerweb.controller.base;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BaseController {

    @Autowired
    public HttpServletRequest request;
    @Autowired
    public HttpServletResponse response;

}
