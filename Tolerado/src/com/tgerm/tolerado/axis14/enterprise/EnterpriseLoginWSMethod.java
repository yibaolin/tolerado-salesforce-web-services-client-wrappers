/*
Copyright (c) 2010 tgerm.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tgerm.tolerado.axis14.enterprise;

import javax.xml.rpc.ServiceException;

import com.sforce.soap.enterprise.LoginResult;
import com.sforce.soap.enterprise.SforceServiceLocator;
import com.sforce.soap.enterprise.SoapBindingStub;
import com.sforce.soap.enterprise.fault.ExceptionCode;
import com.sforce.soap.enterprise.fault.LoginFault;
import com.tgerm.tolerado.axis14.core.Credential;
import com.tgerm.tolerado.axis14.core.ToleradoException;
import com.tgerm.tolerado.axis14.core.ToleradoSession.SessionType;
import com.tgerm.tolerado.axis14.core.method.WSRecoverableMethod;

/**
 * Extends {@link WSRecoverableMethod} to implement salesforce login call
 * 
 * @author abhinav
 * 
 */
public class EnterpriseLoginWSMethod extends
		WSRecoverableMethod<LoginResult, ToleradoEnterpriseStub> {
	private Credential credential;

	public EnterpriseLoginWSMethod(Credential cred) {
		super("login");
		this.credential = cred;
	}

	/**
	 * This method is overriden, as login can never expire on a login call
	 */
	@Override
	protected boolean isLoginExpired(Exception t) {
		return false;
	}

	@Override
	protected SessionType getSessionType() {
		// Override this method, as we don't have stub during the login calls
		return SessionType.Enterprise;
	}

	@Override
	protected void reLogin(ToleradoEnterpriseStub stub) {
		// Override and do nothing, as its a Login Call already.
		// We need to stop parent from attempting any relogin on this call
	}

	@Override
	protected LoginResult invokeActual(ToleradoEnterpriseStub stub)
			throws Exception {
		SoapBindingStub binding;
		try {
			binding = (SoapBindingStub) new SforceServiceLocator().getSoap();
		} catch (ServiceException e1) {
			throw new ToleradoException(e1);
		}

		LoginResult loginResult = null;
		String userName = credential.getUserName();
		String password = credential.getPassword();
		try {
			loginResult = binding.login(userName, password);
		} catch (LoginFault ex) {
			// The LoginFault derives from AxisFault
			ExceptionCode exCode = ex.getExceptionCode();
			if (exCode == ExceptionCode.FUNCTIONALITY_NOT_ENABLED
					|| exCode == ExceptionCode.INVALID_CLIENT
					|| exCode == ExceptionCode.INVALID_LOGIN
					|| exCode == ExceptionCode.LOGIN_DURING_RESTRICTED_DOMAIN
					|| exCode == ExceptionCode.LOGIN_DURING_RESTRICTED_TIME
					|| exCode == ExceptionCode.ORG_LOCKED
					|| exCode == ExceptionCode.PASSWORD_LOCKOUT
					|| exCode == ExceptionCode.SERVER_UNAVAILABLE
					|| exCode == ExceptionCode.TRIAL_EXPIRED
					|| exCode == ExceptionCode.UNSUPPORTED_CLIENT) {
				throw new ToleradoException("Login failed for user :"
						+ userName + ", Reason:" + exCode.getValue(), ex);
			} else {
				throw ex;
			}
		} catch (Exception e) {
			throw e;
		}
		// Check if the password has expired
		if (loginResult.isPasswordExpired()) {
			throw new ToleradoException("Password has expired for user:"
					+ userName);
		}

		return loginResult;
	}
}
