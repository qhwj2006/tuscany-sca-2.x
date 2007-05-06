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

package org.apache.tuscany.implementation.java.invocation;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.scope.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * @version $Rev$ $Date$
 */
public class RuntimeJavaImplementationFactory extends DefaultJavaImplementationFactory {
    private JavaPropertyValueObjectFactory propertyValueObjectFactory;
    private DataBindingExtensionPoint dataBindingRegistry;
    private ProxyFactory proxyService;
    private WorkContext workContext;
    private ScopeRegistry scopeRegistry;

    public RuntimeJavaImplementationFactory(AssemblyFactory assemblyFactory,
                                            ScopeRegistry scopeRegistry,
                                            ProxyFactory proxyService,
                                            WorkContext workContext,
                                            DataBindingExtensionPoint dataBindingRegistry,
                                            JavaPropertyValueObjectFactory propertyValueObjectFactory) {
        super(assemblyFactory);
        this.scopeRegistry = scopeRegistry;
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.dataBindingRegistry = dataBindingRegistry;
        this.propertyValueObjectFactory = propertyValueObjectFactory;
    }

    @Override
    public JavaImplementation createJavaImplementation() {
        return new JavaImplementationProvider(scopeRegistry, proxyService, workContext, dataBindingRegistry,
                                              propertyValueObjectFactory);
    }

}
