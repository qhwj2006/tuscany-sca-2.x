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

package org.apache.tuscany.sca.binding.jms.provider;

import javax.jms.JMSException;

import org.apache.tuscany.sca.binding.jms.JMSBinding;
import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.binding.jms.headers.HeaderReferenceInterceptor;
import org.apache.tuscany.sca.binding.jms.transport.TransportReferenceInterceptor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Phase;
import org.apache.tuscany.sca.provider.EndpointReferenceProvider;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * Implementation of the JMS reference binding provider.
 * 
 * @version $Rev$ $Date$
 */
public class JMSBindingReferenceBindingProvider implements EndpointReferenceProvider {

    private RuntimeEndpointReference endpointReference;
    private RuntimeComponentReference reference;
    private JMSBinding jmsBinding;
    private JMSResourceFactory jmsResourceFactory;
    private RuntimeComponent component;
    private InterfaceContract interfaceContract; 
    private ExtensionPointRegistry extensions;
    
    private ProviderFactoryExtensionPoint providerFactories;
       
    private WireFormatProviderFactory requestWireFormatProviderFactory;
    private WireFormatProvider requestWireFormatProvider;
    
    private WireFormatProviderFactory responseWireFormatProviderFactory;
    private WireFormatProvider responseWireFormatProvider;

    public JMSBindingReferenceBindingProvider(RuntimeEndpointReference endpointReference,  ExtensionPointRegistry extensions, JMSResourceFactory jmsResourceFactory) {
        this.endpointReference = endpointReference;
        this.reference = (RuntimeComponentReference) endpointReference.getReference();
        this.jmsBinding = (JMSBinding) endpointReference.getBinding();
        this.extensions = extensions;
        this.component = (RuntimeComponent) endpointReference.getComponent();
        this.jmsResourceFactory = jmsResourceFactory;
        
        // Get the factories/providers for operation selection        
        this.providerFactories = extensions.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        
        // Get the factories/providers for wire format
         this.requestWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getRequestWireFormat().getClass());
        if (this.requestWireFormatProviderFactory != null){
            this.requestWireFormatProvider = requestWireFormatProviderFactory.createReferenceWireFormatProvider(endpointReference);
        }
        
        this.responseWireFormatProviderFactory = 
            (WireFormatProviderFactory)providerFactories.getProviderFactory(jmsBinding.getResponseWireFormat().getClass());
        if (this.responseWireFormatProviderFactory != null){
            this.responseWireFormatProvider = responseWireFormatProviderFactory.createReferenceWireFormatProvider(endpointReference);
        } 
        
        // create an interface contract that reflects both request and response
        // wire formats
        try {
            interfaceContract = (InterfaceContract)reference.getInterfaceContract().clone();
            
            requestWireFormatProvider.configureWireFormatInterfaceContract(interfaceContract);
            responseWireFormatProvider.configureWireFormatInterfaceContract(interfaceContract);
        } catch (CloneNotSupportedException ex){
            interfaceContract = reference.getInterfaceContract();
        }
    }

    public Invoker createInvoker(Operation operation) {

        if (jmsBinding.getDestinationName() == null) {
//            if (!reference.isCallback()) { // TODO: 2.x migration, is this check needed?
                throw new JMSBindingException("No destination specified for reference " + reference.getName());
//            }
        }

        if ( jmsBinding.getActivationSpecName() != null ) {
        	throw new JMSBindingException("Activation spec can not be specified on an SCA reference binding.");
        }
        Invoker invoker = null;
        invoker = new RRBJMSBindingInvoker(operation, jmsResourceFactory, endpointReference);
       
        return invoker;
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return interfaceContract;
    }

    public void start() {

    }

    public void stop() {
        try {
            jmsResourceFactory.closeConnection();
            jmsResourceFactory.closeResponseConnection();
        } catch (JMSException e) {
            throw new JMSBindingException(e);
        }
    }
    
    /*
     * set up the reference binding wire with the right set of jms reference
     * interceptors
     */
    public void configure() {
        
        InvocationChain bindingChain = endpointReference.getBindingInvocationChain();
        
        // add transport interceptor
        bindingChain.addInterceptor(Phase.REFERENCE_BINDING_TRANSPORT, 
                                    new TransportReferenceInterceptor(jmsBinding,
                                                                      jmsResourceFactory,
                                                                      endpointReference) );
        
        // add request wire format 
        bindingChain.addInterceptor(requestWireFormatProvider.getPhase(), 
                                    requestWireFormatProvider.createInterceptor());
        
        // add response wire format, but only add it if it's different from the request
        if (!jmsBinding.getRequestWireFormat().equals(jmsBinding.getResponseWireFormat())){
            bindingChain.addInterceptor(responseWireFormatProvider.getPhase(), 
                                        responseWireFormatProvider.createInterceptor());
        }
        
        // add the header processor that comes after the wire formatter but before the
        // policy interceptors
        bindingChain.addInterceptor(Phase.REFERENCE_BINDING_WIREFORMAT, 
                                    new HeaderReferenceInterceptor(extensions,
                                                                   jmsBinding,
                                                                   jmsResourceFactory,
                                                                   endpointReference) );
    }    

}
