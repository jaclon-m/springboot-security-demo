package com.jaclon.bootsecurity.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 允许请求跨域，添加status和data到response中
 * @author jaclon
 * @date 2019/8/6
 * @time 14:36
 */
public class ResponseUtil {

    public static void responseJson(HttpServletResponse response,int status,Object data){
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status);

            response.getWriter().write(JSONObject.toJSONString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
