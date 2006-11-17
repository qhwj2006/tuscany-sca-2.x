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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * Verifies loading of a service definition from an XML-based assembly
 *
 * @version $Rev$ $Date$
 */
public class ServiceLoaderTestCase extends TestCase {
    private static final QName SERVICE = new QName(XML_NAMESPACE_1_0, "service");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");
    private static final QName INTERFACE_JAVA = new QName(XML_NAMESPACE_1_0, "interface.java");

    private ServiceLoader loader;
    private DeploymentContext deploymentContext;
    private XMLStreamReader mockReader;
    private LoaderRegistry mockRegistry;

    public void testWithNoInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
//        String target = "target";
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        replay(mockReader);
        ServiceDefinition serviceDefinition = loader.load(null, null, mockReader, null);
        assertNotNull(serviceDefinition);
        assertEquals(name, serviceDefinition.getName());
    }

    public void testWithInterface() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        String target = "target";
        ServiceContract sc = new ServiceContract() {
        };
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(INTERFACE_JAVA);
        expect(mockRegistry.load(null, null, mockReader, deploymentContext)).andReturn(sc);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(REFERENCE);
        expect(mockReader.getElementText()).andReturn(target);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(REFERENCE);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE);

        replay(mockReader);
        replay(mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(null, null, mockReader, deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(name, serviceDefinition.getName());
        assertSame(sc, serviceDefinition.getServiceContract());
    }
    
    public void testWithNoReference() throws LoaderException, XMLStreamException {
        String name = "serviceDefinition";
        String target = "target";
        ServiceContract sc = new ServiceContract() {
        };
        expect(mockReader.getName()).andReturn(SERVICE).anyTimes();
        expect(mockReader.getAttributeValue(null, "name")).andReturn(name);
        expect(mockReader.next()).andReturn(START_ELEMENT);
        expect(mockReader.getName()).andReturn(INTERFACE_JAVA);
        expect(mockRegistry.load(null, null, mockReader, deploymentContext)).andReturn(sc);
        expect(mockReader.next()).andReturn(END_ELEMENT);
        expect(mockReader.getName()).andReturn(SERVICE);

        replay(mockReader);
        replay(mockRegistry);

        ServiceDefinition serviceDefinition = loader.load(null, null, mockReader, deploymentContext);
        assertNotNull(serviceDefinition);
        assertEquals(name, serviceDefinition.getName());
        assertSame(sc, serviceDefinition.getServiceContract());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockReader = EasyMock.createStrictMock(XMLStreamReader.class);
        mockRegistry = EasyMock.createMock(LoaderRegistry.class);
        loader = new ServiceLoader(mockRegistry);
        deploymentContext = new RootDeploymentContext(null, null, null, null);
    }
}
