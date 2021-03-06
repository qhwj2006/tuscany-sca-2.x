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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

// @Ignore("TUSCANY-3138")
public class InterfacesTestCase {
    private static final String PKG = "org/apache/tuscany/sca/itest/interfaces/valid";
    private static String ROOT = new File("target/classes/" + PKG).toURI().toString();

    private static Node node;

    @BeforeClass
    public static void init() throws Exception {
        String location = ROOT;
        node = NodeFactory.newInstance().createNode("InterfacesTest.composite", new Contribution("c1", location));
        node.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        node.stop();
    }

    @Test
    public void testLocalClient() {
        LocalServiceComponent service = node.getService(LocalServiceComponent.class, "LocalServiceComponent");
        LocalClientComponent local = node.getService(LocalClientComponent.class, "LocalClientComponent");

        try {
            ParameterObject po = new ParameterObject();
            assertEquals("AComponent", local.foo1(po));
            assertEquals("AComponent", po.field1);

            assertEquals("AAComponent", local.foo2("A"));

            assertEquals("AAComponent1", local.foo3("A", 1));
            assertEquals("AAComponent1", local.foo4(1, "A"));
        } catch (Exception e) {
            fail();
        }

        try {
            // test local callback
            local.callback("CallBack");
            Thread.sleep(100);
            assertEquals("CallBack", local.getCallbackValue());

            local.callModifyParameter();
            Thread.sleep(100);
            assertEquals("AComponent", service.getPO().field1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("CallBack failed");
        }

        try {
            local.onewayMethod("OneWay");
            Thread.sleep(100);
            assertEquals("OneWay", local.getOnewayValue());
        } catch (Exception e) {
            fail("OneWay failed");
        }
    }

    @Test
    public void testRemoteClient() {
        RemoteServiceComponent service = node.getService(RemoteServiceComponent.class, "RemoteServiceComponent");
        RemoteClientComponent remote = node.getService(RemoteClientComponent.class, "RemoteClientComponent");

        try {
            // Test Pass By Value
            ParameterObject po = new ParameterObject("NotBComponent");
            assertEquals("BComponent", remote.foo1(po));
            assertEquals("NotBComponent", po.field1);

            assertEquals("BBComponent1", remote.foo2(1, "B"));

// TODO: TUSCANY-3479, investigate Node/SCAClient pass by reference            
//            // Test allowsPassByReference
//            assertEquals("BComponent", remote.foo3(po));
//            assertEquals("BComponent", po.field1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            remote.callback("CallBack");
            Thread.sleep(100);
            assertEquals("CallBack", remote.getCallbackValue());

            remote.callModifyParameter();
            Thread.sleep(100);
            assertEquals("CallBack", service.getPO().field1);
        } catch (Exception e) {
            fail("CallBack failed");
        }

        try {
            remote.onewayMethod("OneWay");
            Thread.sleep(100);
            assertEquals("OneWay", remote.getOnewayValue());
        } catch (Exception e) {
            fail("OneWay failed");
        }
    }

}
