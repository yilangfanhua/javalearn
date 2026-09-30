package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component 
public class RequestLogInterceptor implements HandlerInterceptor {
        private static final Logger log=
        LoggerFactory.getLogger(RequestLogInterceptor.class);

        private static final String START_TIME=RequestLogInterceptor.class.getName()+".START_TIME";


        @Override 
        public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler){
                request.setAttribute(
                START_TIME,
                System.currentTimeMillis()
        );
            log.info(
                "request start method={} uri={}",
                request.getMethod(),
                request.getRequestURI()
        );

            return true;

        }
        @Override
        public void afterCompletion(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler,
                Exception exception) {

            Long startTime =
                    (Long) request.getAttribute(START_TIME);

            long duration = startTime == null
                    ? 0
                    : System.currentTimeMillis() - startTime;

            log.info(
                    "request end method={} uri={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            if (exception != null) {
                log.error(
                        "request exception method={} uri={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        exception
                );
            }
        }


}
