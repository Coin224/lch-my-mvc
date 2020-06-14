package com.lch.webmvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 这个类是专门用来转发的类
 * 所有的controller请求都转发到这个类中、
 * 这个类再通过反射去找对应的类和对应功能的方法
 */
public class DispatcherServlet extends HttpServlet {


    // 这个集合用来做一个缓存
    // 把一开始加载的配置文件读出来 放到这个集合中
    // 以后就不用再继续读取配置文件 提升了性能
    private static Map<String,String> realClassNameMap = new HashMap<>();


    // 这个集合用来存储执行方法的controller类对象 生命周期托管式来保证单例
    private static Map<String,Object> objectMap = new HashMap<>();

    static {
        try {
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("ApplicationContext.properties"));
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                realClassNameMap.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // 解析uri 得到类名字
    private String parseUri(String uri) {
        return uri.substring(uri.lastIndexOf("/")+1,uri.indexOf("."));
    }

    // 获取需要执行方法的类
    private Object getObject(String requestName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 2.通过类名字和配置文件找到类全名 applicationContext.properties
        String realClassName = realClassNameMap.get(requestName);
        // 先从map中获取对象 如果没有 再去反射创建对象
        Class clazz = Class.forName(realClassName);
        Object obj = objectMap.get(realClassName);
        if (obj == null) {
            synchronized (DispatcherServlet.class) {
                if (obj == null) {
                    obj = clazz.newInstance();
                    objectMap.put(realClassName, obj);
                }
            }
        }
        return obj;
    }


    private Method getMethod(Object obj ,String methodName) throws NoSuchMethodException {
        Class clazz = obj.getClass();
        return clazz.getMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1.获取请求的uri、得到类名字   约定请求的url：类名字.do?method=方法名字
            String uri = req.getRequestURI();
            String requestName = this.parseUri(uri);

            // 2.获取对象
           Object obj = this.getObject(requestName);

            // 3.获取方法的名字 反射找到执行的方法、调用service层的方法处理数据
            String methodName = req.getParameter("method");
           Method method = this.getMethod(obj,methodName);

            // 4.执行方法，返回一个路径
            String path = (String) method.invoke(obj,req,resp);
            // 4.响应信息
            req.getRequestDispatcher(path).forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            System.out.println("dispatcher类");
//            // 1.找到请求名字
//            String uri = req.getRequestURI();
//            String requestName = uri.substring(uri.lastIndexOf("/")+1);
//            System.out.println(requestName.length());
//
////        StringBuffer url = req.getRequestURL();
////        System.out.println(url.toString());
//            // 2.约定请求名字与对应的方法名字相同  约定优于配置
//            Class clazz = this.getClass();
//            Method method = clazz.getMethod(requestName,HttpServletRequest.class,HttpServletResponse.class);
//
//            // 3.调用service层中的方法去处理数据 (通过去找到对应的类找到对应的方法)
//            method.invoke(this,req,resp);
//            // 其他类的方法
//            // 4.响应信息
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
