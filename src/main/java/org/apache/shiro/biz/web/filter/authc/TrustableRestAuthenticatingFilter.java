/*
 * Copyright (c) 2018 (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.shiro.biz.web.filter.authc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.biz.utils.WebUtils;
import org.apache.shiro.biz.web.filter.authc.listener.LoginListener;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustableRestAuthenticatingFilter extends AbstractTrustableAuthenticatingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(TrustableRestAuthenticatingFilter.class);
	
	
	public TrustableRestAuthenticatingFilter() {
		super();
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Login submission detected.  Attempting to execute login.");
				}
				return executeLogin(request, response);
			} else {
				String mString = "Authentication url [" + getLoginUrl() + "] Not Http Post request.";
				if (LOG.isTraceEnabled()) {
					LOG.trace(mString);
				}
				WebUtils.writeJSONString(response, HttpServletResponse.SC_BAD_REQUEST, mString);
				return false;
			}
		} else {
			
			String mString = "Attempting to access a path which requires authentication.  Request the "
					+ "Authentication url [" + getLoginUrl() + "]";
			if (LOG.isTraceEnabled()) {
				LOG.trace(mString);
			}
			
			// 响应成功状态信息
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("status", "fail");
			data.put("message", mString);
			// 响应
			WebUtils.writeJSONString(response, data);
			
			return false;
		}
	}
	
    /**
     * Rewrite the response logic after successful login: JSON information write back
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
    	
    	// Call event listener
		if(getLoginListeners() != null && getLoginListeners().size() > 0){
			for (LoginListener loginListener : getLoginListeners()) {
				loginListener.onLoginSuccess(token, subject, request, response);
			}
		}
		
        // Response success status information
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", "success");
		data.put("message", "Authentication Success.");
		// 响应
		WebUtils.writeJSONString(response, data);
        
        //we handled the success , prevent the chain from continuing:
        return false;
    }
   
    
}
