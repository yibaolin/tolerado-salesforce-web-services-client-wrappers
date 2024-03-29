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

package com.tgerm.tolerado.axis14.metadata;

import javax.xml.rpc.ServiceException;

import com.sforce.soap._2006._04.metadata.AsyncResult;
import com.sforce.soap._2006._04.metadata.DebuggingHeader;
import com.sforce.soap._2006._04.metadata.LogType;
import com.sforce.soap._2006._04.metadata.MetadataBindingStub;
import com.sforce.soap._2006._04.metadata.MetadataServiceLocator;
import com.sforce.soap._2006._04.metadata.RetrieveRequest;
import com.sforce.soap._2006._04.metadata.SessionHeader;
import com.tgerm.tolerado.axis14.core.Credential;
import com.tgerm.tolerado.axis14.core.ToleradoException;
import com.tgerm.tolerado.axis14.core.ToleradoStub;
import com.tgerm.tolerado.axis14.core.method.WSRecoverableMethod;
import com.tgerm.tolerado.axis14.partner.ToleradoPartnerStub;

/**
 * {@link ToleradoPartnerStub} extension for salesforce metadata wsdl
 * 
 * @author abhinav
 * 
 */
public class ToleradoMetaStub extends ToleradoStub {
	private MetadataBindingStub binding;

	public ToleradoMetaStub(Credential c) {
		super(c);
	}

	@Override
	public void prepare() {
		try {
			binding = (MetadataBindingStub) new MetadataServiceLocator()
					.getMetadata();
		} catch (ServiceException e) {
			throw new ToleradoException(e);
		}
		binding._setProperty(MetadataBindingStub.ENDPOINT_ADDRESS_PROPERTY,
				session.getMetadataServerUrl());
		SessionHeader sh = new SessionHeader();
		sh.setSessionId(session.getSessionId());
		binding.setHeader(new MetadataServiceLocator().getServiceName()
				.getNamespaceURI(), "SessionHeader", sh);
		// set the debugging header
		DebuggingHeader dh = new DebuggingHeader();
		dh.setDebugLevel(LogType.Profiling);
		binding.setHeader(new MetadataServiceLocator().getServiceName()
				.getNamespaceURI(), "DebuggingHeader", dh);

	}

	/**
	 * Retrieve metadata call wrapper
	 * 
	 * @param retrieveRequest
	 * @return
	 */
	public AsyncResult retrieve(final RetrieveRequest retrieveRequest) {
		WSRecoverableMethod<AsyncResult, ToleradoMetaStub> wsMethod = new WSRecoverableMethod<AsyncResult, ToleradoMetaStub>(
				"retrieve") {
			@Override
			protected AsyncResult invokeActual(ToleradoMetaStub stub)
					throws Exception {
				return stub.getMetaBinding().retrieve(retrieveRequest);
			}
		};
		AsyncResult results = wsMethod.invoke(this);
		return results;
	}

	/**
	 * Gives handle to the internal {@link MetadataBindingStub}
	 * 
	 * @return {@link MetadataBindingStub}
	 */
	public MetadataBindingStub getMetaBinding() {
		return binding;
	}
}
