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
package org.apache.tuscany.core.implementation.java;

import java.net.URI;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JavaComponentBuilderResourceTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testResourceInjection() throws Exception {
        ScopeContainer container = EasyMock.createNiceMock(ScopeContainer.class);
        ScopeRegistry registry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(registry.getScopeContainer(Scope.STATELESS)).andReturn(container);
        EasyMock.replay(registry);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("result");
        EasyMock.replay(host);
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setHost(host);
        builder.setScopeRegistry(registry);
        ConstructorDefinition<Foo> ctorDef = new ConstructorDefinition<Foo>(Foo.class.getConstructor());
        PojoComponentType type = new PojoComponentType();
        Resource resource = new Resource("resource", String.class, Foo.class.getDeclaredField("resource"));
        type.add(resource);
        type.setImplementationScope(Scope.STATELESS);
        type.setConstructorDefinition(ctorDef);
        JavaImplementation impl = new JavaImplementation(Foo.class, type);
        URI uri = URI.create("foo");
        ComponentDefinition<JavaImplementation> definition = new ComponentDefinition<JavaImplementation>(uri, impl);
        Wire resourceWire = EasyMock.createMock(Wire.class);
        EasyMock.expect(resourceWire.getTargetInstance()).andReturn("result");
        EasyMock.replay(resourceWire);

        Component parent = EasyMock.createMock(Component.class);
        EasyMock.replay(parent);
        JavaAtomicComponent component = (JavaAtomicComponent) builder.build(definition, null);
        Foo foo = (Foo) component.createInstance();
        assertEquals("result", foo.resource);
        EasyMock.verify(parent);
    }

    private static class Foo {

        protected String resource;

        public Foo() {
        }

    }
}


