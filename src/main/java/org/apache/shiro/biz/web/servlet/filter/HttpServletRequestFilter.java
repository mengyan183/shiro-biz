package org.apache.shiro.biz.web.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;


public class HttpServletRequestFilter extends OncePerRequestFilter {

	protected String redirectURL = "";
	protected String dispatchURL = "";
	
	@Override
	protected void onFilterConfigSet() throws Exception {
    }

	protected void doFilterInternal(ServletRequest request,ServletResponse response, FilterChain filterchain)
		throws ServletException, IOException {
		 Subject subject = SecurityUtils.getSubject();
		 if(subject.isAuthenticated()) {
			 //request.getRequestDispatcher("/WEB-INF/jsp/authenticated.jsp").forward(req, resp);
		 } else {
			 //request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
		 }
	}

	public void destroy() {
		super.destroy();
		this.filterConfig = null;
	}
}
