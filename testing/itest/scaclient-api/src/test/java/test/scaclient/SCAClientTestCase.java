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

package test.scaclient;

import itest.HelloworldService;
import itest.RemoteHelloworldService;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * Test SCADomain.newInstance and invocation of a service.
 *
 * @version $Rev$ $Date$
 */
public class SCAClientTestCase extends TestCase {

    private Node node;

    @Test
    public void testDefault() throws Exception {

        node = NodeFactory.getInstance().createNode((String)null, new String[] {"target/classes"});
        node.start();

        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("default"));
        HelloworldService service = clientFactory.getService(HelloworldService.class, "HelloworldComponent/HelloworldService");
        assertEquals("Hello petra", service.sayHello("petra"));
        
        RemoteHelloworldService remoteService = clientFactory.getService(RemoteHelloworldService.class, "HelloworldComponent/RemoteHelloworldService");
        assertEquals("Hello petra", remoteService.sayHelloRemote("petra"));

    }

    @Test
    public void testExplicit() throws Exception {
        node = NodeFactory.getInstance().createNode(URI.create("myFooDomain"), new String[] {"target/classes"});
        node.start();

        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("myFooDomain"));
        HelloworldService service = clientFactory.getService(HelloworldService.class, "HelloworldComponent/HelloworldService");
        assertEquals("Hello petra", service.sayHello("petra"));
        
        RemoteHelloworldService remoteService = clientFactory.getService(RemoteHelloworldService.class, "HelloworldComponent/RemoteHelloworldService");
        assertEquals("Hello petra", remoteService.sayHelloRemote("petra"));
        assertEquals("Hello petra", service.sayHello("petra"));
    }

//    @Test
//    public void testWithoutServiceName() throws Exception {
//        node = NodeFactory.getInstance().createNode(URI.create("myFooDomain"), new String[] {"target/classes"});
//        node.start();
//
//        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("myFooDomain"));
//        HelloworldService service = clientFactory.getService(HelloworldService.class, "HelloworldComponent");
//        assertEquals("Hello petra", service.sayHello("petra"));
//    }

    @Test
    public void testWithBadServiceName() throws Exception {
        node = NodeFactory.getInstance().createNode(URI.create("myFooDomain"), new String[] {"target/classes"});
        node.start();

        SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("myFooDomain"));
        try {
           clientFactory.getService(HelloworldService.class, "HelloworldComponent/foo");
           fail();
        } catch (NoSuchServiceException e) {
            // expected
        }
    }

    @Test
    public void testWithBadDomainName() throws Exception {
        node = NodeFactory.getInstance().createNode(URI.create("myFooDomain"), new String[] {"target/classes"});
        node.start();

        try {
            SCAClientFactory clientFactory = SCAClientFactory.newInstance(URI.create("someBadDomainName"));
            fail();
        } catch (NoSuchDomainException e) {
            // expected
        }
    }
    
    //    @Test @Ignore
//    public void testHTTPURI() throws Exception {
//        node = NodeFactory.getInstance().createNode(URI.create("http://defaultDomain"), new String[] {"target/classes"});
//        node.start();
//
//        HelloworldService service = SCAClientFactory.newInstance(URI.create("http://defaultDomain")).getService(HelloworldService.class, "HelloworldComponent");
//        assertEquals("Hello petra", service.sayHello("petra"));
//    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
