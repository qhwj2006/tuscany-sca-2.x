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
package org.apache.tuscany.sca.binding.ws.axis2.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.wsdl.Port;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.jms.JMSListener;
import org.apache.axis2.transport.jms.JMSSender;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.axis2.policy.mtom.Axis2MTOMPolicyProvider;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.RuntimeAssemblyFactory;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.policy.util.PolicyHelper;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.oasisopen.sca.ServiceRuntimeException;

public class Axis2ServiceBindingProvider extends Axis2BaseBindingProvider implements ServiceBindingProvider {
    private static final Logger logger = Logger.getLogger(Axis2ServiceBindingProvider.class.getName());

    // Tuscany extensions
    private AssemblyFactory assemblyFactory;
    private ServletHost servletHost;
    private RuntimeComponent component;
    private RuntimeComponentService service;
    
    // the endpoint configuration that's driving this binding provider
    // and some convenience data retrieved from the endpoint
    private RuntimeEndpoint endpoint;
    private WebServiceBinding wsBinding;
    private Port wsdlPort;
    private String endpointURI;
    private String deployedURI;
    private InterfaceContract contract;
       
    // The Axis2 configuration that the binding creates
    private JMSSender jmsSender;
    private JMSListener jmsListener;    
       
    public Axis2ServiceBindingProvider(ExtensionPointRegistry extensionPoints,
                                       RuntimeEndpoint endpoint,
                                       ServletHost servletHost ) {
        super(extensionPoints);
        
        this.extensionPoints = extensionPoints;
        this.endpoint = endpoint;
        this.servletHost = servletHost;
        
        this.assemblyFactory = (RuntimeAssemblyFactory)modelFactories.getFactory(AssemblyFactory.class);
        this.wsBinding = (WebServiceBinding)endpoint.getBinding();
        this.component = (RuntimeComponent)endpoint.getComponent();
        this.service = (RuntimeComponentService)endpoint.getService();
        
        // A WSDL document should always be present in the binding
        if (wsBinding.getGeneratedWSDLDocument() == null) {
            throw new ServiceRuntimeException("No WSDL document for " + component.getName() + "/" + service.getName());
        }
        
        // Set to use the Axiom data binding
        contract = wsBinding.getBindingInterfaceContract();
        contract.getInterface().resetDataBinding(OMElement.class.getName());

        configContext = Axis2EngineIntegration.getAxisConfigurationContext(extensionPoints.getServiceDiscovery());
        
        // set the root context for this instance of Axis
        configContext.setContextRoot(servletHost.getContextPath());
        
        // Determine the configuration from the bindings "mayProvides" intents
           
        isSOAP12Required = PolicyHelper.isIntentRequired(wsBinding, Constants.SOAP12_INTENT);
        
        isMTOMRequired = PolicyHelper.isIntentRequired(wsBinding, Axis2BindingProviderFactory.MTOM_INTENT);
        
        // this is not correct as there may be other, custom, policies that 
        // require rampart. For example this is not going to pick up the case
        // of external policy attachment
        isRampartRequired = PolicyHelper.isIntentRequired(wsBinding, Constants.AUTHENTICATION_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, Constants.CONFIDENTIALITY_INTENT) ||
                            PolicyHelper.isIntentRequired(wsBinding, Constants.INTEGRITY_INTENT);                  
        
        
        // Apply the configuration from any other policies
        
        for (PolicyProvider pp : endpoint.getPolicyProviders()) {
            pp.configureBinding(this);
        }  

        // Update port addresses with runtime information
        // We can safely assume there is only one port here because you configure
        // a binding in the following ways: 
        // 1/ default             - one port generated = host domain : host port / structural path 
        // 2/ uri="absolute addr" - one port generated = host domain : uri port  / uri path
        // 3/ uri="relative addr" - one port generated = host domain : host port / structural path / relative path
        // 4/ wsdl.binding        - one port generated = host domain : host port / structural path 
        // 5/ wsdl.port           - one port generated = host domain : port port / port path
        // 6/ wsa:Address         - one port generated = host domain : address port / address path
        // 7/ 4 + 6               - as 6
        wsdlPort = (Port)wsBinding.getService().getPorts().values().iterator().next();
        
        if (wsdlPort == null){
            throw new ServiceRuntimeException("No WSDL port for ws binding of " + component.getName() + "/" + service.getName());
        }
        
        endpointURI = Axis2EngineIntegration.getPortAddress(wsdlPort);
        
        if (endpointURI.startsWith("jms:")) {
            deployedURI = endpointURI;
            isJMSRequired = true;
        } else {
            if (servletHost == null) {
                throw new ServiceRuntimeException("No Servlet host is avaible for HTTP web services");
            }
            deployedURI = servletHost.getURLMapping(endpointURI, httpSecurityContext).toString();
        } 
        
        Axis2EngineIntegration.setPortAddress(wsdlPort, deployedURI);  
        
        // Apply the configuration from the mayProvides intents        
        
        if (isRampartRequired){
            // TODO - do we need to go back to configurator?
        }
        
        if (isMTOMRequired) {
            new Axis2MTOMPolicyProvider(endpoint).configureBinding(configContext);
        }
        
        if (isJMSRequired){
            // TODO - do we need to go back to configurator?
        }  
        wsBinding.setURI(deployedURI);
        
        // Check the WSDL style as we only support some of them
        
        if (wsBinding.isRpcEncoded()){
            throw new ServiceRuntimeException("rpc/encoded WSDL style not supported.  Component " + endpoint.getComponent().getName() +
                                              " Service " + endpoint.getService() +
                                              " Binding " + endpoint.getBinding().getName());
        } 
        
        if (wsBinding.isDocEncoded()){
            throw new ServiceRuntimeException("doc/encoded WSDL style not supported.  Component " + endpoint.getComponent().getName() +
                                              " Service " + endpoint.getService() +
                                              " Binding " + endpoint.getBinding().getName());
        } 
        
      //  if (wsBinding.isDocLiteralUnwrapped()){
      //      throw new ServiceRuntimeException("doc/literal/unwrapped WSDL style not supported for endpoint " + endpoint);
      //  }
    }
    
    private static final String DEFAULT_QUEUE_CONNECTION_FACTORY = "TuscanyQueueConnectionFactory";

    public void start() {
        try {
            createAxisService(deployedURI, wsdlPort);
           
            if (deployedURI.startsWith("http://") || 
                deployedURI.startsWith("https://") || 
                deployedURI.startsWith("/")) {
                Axis2ServiceServlet servlet = new Axis2ServiceServlet();
                servlet.init(configContext);
                
                if (httpSecurityContext.isSSLEnabled()){
                    deployedURI = servletHost.addServletMapping(endpointURI, servlet, httpSecurityContext);
                } else {
                    deployedURI = servletHost.addServletMapping(endpointURI, servlet);
                }
            } else if (deployedURI.startsWith("jms")) {
                logger.log(Level.INFO, "Axis2 JMS URL=" + deployedURI);

                jmsListener = new JMSListener();
                jmsSender = new JMSSender();
                ListenerManager listenerManager = configContext.getListenerManager();
                TransportInDescription trsIn =
                    configContext.getAxisConfiguration().getTransportIn(org.apache.axis2.Constants.TRANSPORT_JMS);

                // get JMS transport parameters from the computed URL
//not in Axis2 1.5.1                    
//                Map<String, String> jmsProps = JMSUtils.getProperties(endpointURL);

                // collect the parameters used to configure the JMS transport
                OMFactory fac = OMAbstractFactory.getOMFactory();
                OMElement parms = fac.createOMElement(DEFAULT_QUEUE_CONNECTION_FACTORY, null);
/*
                for (String key : jmsProps.keySet()) {
                    OMElement param = fac.createOMElement("parameter", null);
                    param.addAttribute("name", key, null);
                    param.addChild(fac.createOMText(param, jmsProps.get(key)));
                    parms.addChild(param);
                }
*/
                Parameter queueConnectionFactory = new Parameter(DEFAULT_QUEUE_CONNECTION_FACTORY, parms);
                trsIn.addParameter(queueConnectionFactory);

                trsIn.setReceiver(jmsListener);

                configContext.getAxisConfiguration().addTransportIn(trsIn);
                TransportOutDescription trsOut =
                    configContext.getAxisConfiguration().getTransportOut(org.apache.axis2.Constants.TRANSPORT_JMS);
                //configContext.getAxisConfiguration().addTransportOut( trsOut );
                trsOut.setSender(jmsSender);

                if (listenerManager == null) {
                    listenerManager = new ListenerManager();
                    listenerManager.init(configContext);
                }
                listenerManager.addListener(trsIn, true);
                jmsSender.init(configContext, trsOut);
                jmsListener.init(configContext, trsIn);
                jmsListener.start();
            }
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }                                        
    }

    public void stop() {
        try {
            if (jmsListener != null) {
                jmsListener.stop();
                jmsListener.destroy();
            } else {
                servletHost.removeServletMapping(endpointURI);
            }
            
            if (jmsSender != null) {
                jmsSender.stop();
            }
    
            servletHost = null;

            // get the path to the service
            // [nash] Need a leading slash for WSDL imports to work with ?wsdl
            URI uriPath = new URI(deployedURI);
            String stringURIPath = uriPath.getPath();
            configContext.getAxisConfiguration().removeService(stringURIPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }        
    }

    public InterfaceContract getBindingInterfaceContract() {
        return wsBinding.getBindingInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return true;
    }
    
    // Service specific utility operations
    
    private void createAxisService(String endpointURL, Port port) throws AxisFault {
        AxisService axisService;
        if (wsBinding.getGeneratedWSDLDocument() != null) {
            axisService = Axis2EngineIntegration.createWSDLAxisService(endpointURL, port, wsBinding);
        } else {
            axisService = Axis2EngineIntegration.createJavaAxisService(endpointURL, configContext, service);
        }
        
        Axis2EngineIntegration.createAxisServiceProviders(axisService, endpoint, wsBinding, extensionPoints);
        
        configContext.getAxisConfiguration().addService(axisService);
    }    
}
