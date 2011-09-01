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

package org.apache.tuscany.sca.itest.interfaces;

import java.net.URI;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;

public class InerfaceMissmatchTestCase {
    
    /**
     * Non-remoteable client and service interfaces where the parameter types of one of the operations don't match.
     * Components running in the same composite/JVM, i.e. no remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testLocal() throws Exception {
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/local/MissmatchLocal.composite", 
                                                                     contributions);
        node1.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "LocalClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            local.foo1(po);
            Assert.fail("Expection exteption indicating that interfaces don't match");
        } catch (ServiceRuntimeException ex){
            Assert.assertTrue(ex.getMessage().startsWith("Unable to bind []"));
        }
        
        node1.stop(); 
    }
    
    /**
     * Non-remoteable client and service interfaces where the parameter types of one of the callback operations don't match.
     * Components running in the same composite/JVM, i.e. no remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testCallbackLocal() throws Exception {
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/local/MissmatchCallbackLocal.composite", 
                                                                     contributions);
        node1.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "LocalClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            local.foo1(po);
            Assert.fail("Expection exteption indicating that interfaces don't match");
        } catch (ServiceRuntimeException ex){
            Assert.assertTrue(ex.getMessage().startsWith("Unable to bind []"));
        }
        
        node1.stop(); 
    }    
   
    /**
     * Remoteable client and service interfaces where the parameter types of one of the operations don't match.
     * Components running in the same composite/JVM, i.e. no remote registry
     * Tuscany is not able to detect miss-match here?
     * 
     * @throws Exception
     */
    @Test
    public void testNonDistributedRemotable() throws Exception {
     
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/local/MissmatchRemoteable.composite", 
                                                                     contributions);
        node1.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "LocalClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            local.foo1(po);
        } catch (ServiceRuntimeException ex){
            Assert.assertTrue(ex.getMessage().startsWith("Unable to bind []"));
        }
        
        node1.stop();
    }
  
    /**
     * Remoteable client and service interfaces where the parameter types of one of the operations don't match.
     * Components running in the seaprate composite/JVM, i.e. there is a remote registry
     * Both of the interfaces are converted to WSDL and Tuscany is able to detect miss-match.
     * 
     * @throws Exception
     */
    @Test
    public void testDistributedRemotable() throws Exception {
        
        // Force the remote default binding to be web services
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://docs.oasis-open.org/ns/opencsa/sca/200912}binding.ws");
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchDistributedService.composite", 
                                                                     contributions);
        // for default binding on node2 to use a different port from node 1(which will default to 8080
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        ((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");        
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            local.foo1(po);
            Assert.fail("Expected exception indicating that interfaces don't match");
        } catch (ServiceRuntimeException ex){
            Assert.assertTrue(ex.getMessage().startsWith("Unable to bind []"));
        }
        
        node1.stop();
        node2.stop();
    }
    
    /**
     * Remoteable client and service interfaces where the parameter types of one of the operations don't match.
     * Components running in the seaprate composite/JVM, i.e. there is a remote registry
     * Both of the interfaces are converted to WSDL and Tuscany is able to detect miss-match.
     * 
     * @throws Exception
     */
    @Test  
    public void testCallbackDistributedRemotable() throws Exception {
        
        // Force the remote default binding to be web services
        System.setProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding", 
                           "{http://docs.oasis-open.org/ns/opencsa/sca/200912}binding.ws");
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchCallbackDistributedService.composite", 
                                                                     contributions);
        // for default binding on node2 to use a different port from node 1(which will default to 8080
        ((NodeImpl)node2).getConfiguration().addBinding(WebServiceBinding.TYPE, "http://localhost:8081/");
        ((NodeImpl)node2).getConfiguration().addBinding(SCABinding.TYPE, "http://localhost:8081/");        
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            local.foo1(po);
            Assert.fail("Expected exception indicating that interfaces don't match");
        } catch (ServiceRuntimeException ex){
            Assert.assertTrue(ex.getMessage().startsWith("Unable to bind []"));
        }
        
        node1.stop();
        node2.stop();

    }
}
