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

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;

public class InerfaceMatchTestCase {
    
    /**
     * Non-remoteable client and service interfaces where the interfaces match.
     * Components running in the same composite/JVM, i.e. no remote registry
     * 
     * @throws Exception
     */
    @Test
    public void testLocal() throws Exception {
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/local/MatchLocal.composite", 
                                                                     contributions);
        node1.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "LocalClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception " + ex.toString());
        }
        
        node1.stop(); 
    }
  
    /**
     * Remoteable client and service interfaces where the interfaces match.
     * Components running in the seaprate composite/JVM, i.e. there is a remote registry
     * 
     * @throws Exception
     */
    @Ignore("Remote interface matching doesn't take callback into account yet")
    @Test
    public void testDistributedRemotable() throws Exception {
        
        String [] contributions = {"./target/classes"};
        Node node1 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MissmatchDistributedClient.composite", 
                                                                     contributions);
        node1.start();

        Node node2 = NodeFactory.newInstance().createNode(URI.create("tuscany:InerfaceMissmatchTestCase"), 
                                                                     "org/apache/tuscany/sca/itest/interfaces/missmatch/distributed/MatchDistributedService.composite", 
                                                                     contributions);
        node2.start();
        
        ClientComponent local = node1.getService(ClientComponent.class, "DistributedClientComponent");
        ParameterObject po = new ParameterObject();
        
        try {
            String response = local.foo1(po);
            Assert.assertEquals("AComponent", response);
        } catch (ServiceRuntimeException ex){
            Assert.fail("Unexpected exception " + ex.toString());
        }
        
        node1.stop();
        node2.stop();

    }
}
