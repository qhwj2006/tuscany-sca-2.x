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

package org.apache.tuscany.sca.binding.comet.runtime;

import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;

/**
 * Provider for references that have comet binding specified in the scdl. Not
 * used as comet binding references would occur in client browser's Javascript.
 */
public class CometReferenceBindingProvider implements ReferenceBindingProvider {

    /**
     * Endpoint for which the binding provider is created.
     */
    private final EndpointReference endpoint;

    public CometReferenceBindingProvider(final EndpointReference endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public Invoker createInvoker(final Operation operation) {
        return new CometInvoker(operation, this.endpoint);
    }

    /**
     * No behavior.
     */
    @Override
    public void start() {
    }

    /**
     * No behavior.
     */
    @Override
    public void stop() {
    }

    @Override
    public InterfaceContract getBindingInterfaceContract() {
        return null;
    }

    @Override
    public boolean supportsOneWayInvocation() {
        return true;
    }

}
