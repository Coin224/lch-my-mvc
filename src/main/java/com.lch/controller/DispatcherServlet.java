package com.lch.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class DispatcherServlet extends HttpServlet {


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("dispatcher类");
        // 1.找到请求名字
        String uri = req.getRequestURI();
        String requestName = uri.substring(uri.lastIndexOf("/")+1);
        System.out.println(requestName.length());

//        StringBuffer url = req.getRequestURL();
//        System.out.println(url.toString());
        // 2.约定请求名字与对应的方法名字相同  约定优于配置
        Class clazz = this.getClass();
        try {
            Method method = clazz.getMethod(requestName,HttpServletRequest.class,HttpServletResponse.class);
            method.invoke(this,req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("login方法");
    }
}
