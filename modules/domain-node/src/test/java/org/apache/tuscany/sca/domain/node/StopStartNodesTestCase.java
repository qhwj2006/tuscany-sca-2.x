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

package org.apache.tuscany.sca.domain.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import itest.nodes.Helloworld;

import java.net.URI;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.client.SCAClientFactory;

/**
 * This shows how to test the Calculator service component.
 */
@Ignore("TUSCANY-3391")
public class StopStartNodesTestCase{

    private static DomainNode clientNode;
    private static DomainNode serviceNode;
    
    @Test
    public void testTwoNodesSameDomain() throws Exception {
        serviceNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-service-2.0-Beta1.jar"});
        clientNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-client-2.0-Beta1.jar"});

        Helloworld service = SCAClientFactory.newInstance(URI.create("vm://fooDomain")).getService(Helloworld.class, "HelloworldService");
        assertNotNull(service);
        assertEquals("Hello Petra", service.sayHello("Petra"));

        Helloworld client = SCAClientFactory.newInstance(URI.create("vm://fooDomain")).getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));

        serviceNode.stop();

        client = SCAClientFactory.newInstance(URI.create("vm://fooDomain")).getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        try {
            assertEquals("Hi Hello Petra", client.sayHello("Petra"));
            fail();
        } catch (Exception e) {
            // expected
        }

        serviceNode = new DomainNode("vm://fooDomain", new String[]{"target/test-classes/itest-nodes-helloworld-service-2.0-Beta1.jar"});
        client = SCAClientFactory.newInstance(URI.create("vm://fooDomain")).getService(Helloworld.class, "HelloworldClient");
        assertNotNull(client);
        assertEquals("Hi Hello Petra", client.sayHello("Petra"));
    }

    @After
    public void tearDownAfterClass() throws Exception {
        if (clientNode != null) {
            clientNode.stop();
        }
        if (serviceNode != null) {
            serviceNode.stop();
        }
    }
}
