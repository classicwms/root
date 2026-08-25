package com.tekclover.wms.api.inbound.transaction.config;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WarehouseInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("------------Pre Handle method Start-----------Date: " + new Date());
		log.info("Request: " + request.getHeader("X-Amzn-Trace-Id"));
		log.info("Request-RemoteAddr: " + request.getRemoteAddr());
		log.info("Request-RemoteHost: " + request.getRemoteHost());
		log.info("Request-RemotePort: " + request.getRemotePort());
		log.info("Request-ContentLengthLong: " + request.getContentLengthLong());
		log.info("Request-ContentLength: " + request.getContentLength());
		
		try {
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				log.info("Request URI: " + request.getRequestURI());
			}
		} catch (Exception e) {
		}
		
		log.info("------------Pre Handle method End-----------");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("------------Post Handle method Start-----------Date: " + new Date());
		log.info("Request-X-Amzn-Trace-Id : " + request.getHeader("X-Amzn-Trace-Id"));
		log.info("Request-RemoteAddr: " + request.getRemoteAddr());
		log.info("Request-RemoteHost: " + request.getRemoteHost());
		log.info("Request-RemotePort: " + request.getRemotePort());
		log.info("Request-ContentLengthLong: " + request.getContentLengthLong());
		log.info("Request-ContentLength: " + request.getContentLength());
		log.info("------------Post Handle method End-----------");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) throws Exception {
		log.info("------------AfterCompletion Start-----------Date: " + new Date());
		log.info("Response : " + response.getStatus());
		log.info("------------AfterCompletion End-----------");
	}
}