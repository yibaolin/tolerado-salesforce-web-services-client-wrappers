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

package com.tgerm.tolerado.samples.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.tgerm.tolerado.axis14.core.Credential;
import com.tgerm.tolerado.axis14.core.ToleradoException;

/**
 * Login configuration management class. It loads the config from a file named
 * "login.props" available in classpath at com/tgerm/tolerado/cfg/
 * 
 * One can call the {@link LoginCfg#self#getCredential()} method to get the
 * credentials, for running the samples
 * 
 * @author abhinav
 * 
 */
public class LoginCfg {
	public static final LoginCfg self = new LoginCfg();
	private Properties props;
	private Credential credential;

	private LoginCfg() {
		InputStream cfgStream = LoginCfg.class
				.getResourceAsStream("login.props");
		props = new Properties();
		try {
			props.load(cfgStream);
		} catch (IOException e) {
			throw new ToleradoException(e);
		}
		String user = props.getProperty("user");
		String pass = props.getProperty("pass");
		credential = new Credential(user, pass);
	}

	public Credential getCredential() {
		return credential;
	}
}
