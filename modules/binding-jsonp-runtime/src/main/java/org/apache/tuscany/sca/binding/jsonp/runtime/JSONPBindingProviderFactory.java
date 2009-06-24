/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.jsonp.runtime;

import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.jsonp.JSONPBinding;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.host.http.ServletHostExtensionPoint;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;

public class JSONPBindingProviderFactory implements BindingProviderFactory<JSONPBinding> {

    private ServletHost servletHost;

    public JSONPBindingProviderFactory(ExtensionPointRegistry extensionPoints) {
        ServletHostExtensionPoint servletHosts = extensionPoints.getExtensionPoint(ServletHostExtensionPoint.class);
        List<ServletHost> hosts = servletHosts.getServletHosts();
        if (!hosts.isEmpty()) {
            this.servletHost = hosts.get(0);
        }
    }

    public Class<JSONPBinding> getModelType() {
        return JSONPBinding.class;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(EndpointReference arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceBindingProvider createServiceBindingProvider(Endpoint endpoint) {
        return new JSONPServiceBindingProvider(endpoint, servletHost);
    }

}
