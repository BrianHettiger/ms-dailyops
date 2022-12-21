package com.moblize.ms.dailyops.web.rest.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;

@Component
@WebFilter("/*")
@Slf4j
public class StatsFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        Instant start = Instant.now();
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) resp);
        try {
            chain.doFilter(req, responseWrapper);
        } catch (Exception exp){
            log.error("Error occurred while processing request: {}" , ((HttpServletRequest) req).getRequestURI(), exp);
        } finally {
            Instant finish = Instant.now();
            long time = Duration.between(start, finish).toMillis();
            log.info("{}: {} ms and size : {} bytes", ((HttpServletRequest) req).getRequestURI(), time, responseWrapper.getContentSize());
            responseWrapper.copyBodyToResponse();
        }
    }

    private void performResponseAudit(ContentCachingResponseWrapper responseWrapper)
        throws IOException {
            log.info("Response Body:: {}", getPayLoadFromByteArray(responseWrapper.getContentAsByteArray(),
                responseWrapper.getCharacterEncoding()));
        responseWrapper.copyBodyToResponse();
    }

    private String getPayLoadFromByteArray(byte[] requestBuffer, String charEncoding) {
        String payLoad = "";
        try {
            payLoad = new String(requestBuffer, charEncoding);
        } catch (UnsupportedEncodingException unex) {
            payLoad = "Unsupported-Encoding";
        }
        return payLoad;
    }

    @Override
    public void destroy() {
    }

}
