package com.example.taskmanager.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtils {

    public static String getClientIP() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) return "unknown";

        HttpServletRequest request = attr.getRequest();
        return request.getRemoteAddr();
    }
}
