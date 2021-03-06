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

package org.apache.tuscany.sca.core.context;

import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.context.impl.ComponentContextImpl;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ComponentContext;

/**
 * @version $Rev$ $Date$
 */
public class DefaultComponentContextFactory implements ComponentContextFactory {
    private final ExtensionPointRegistry registry;

    public DefaultComponentContextFactory(ExtensionPointRegistry registry) {
        this.registry = registry;
    }

    public ComponentContext createComponentContext(CompositeContext compositeContext, RuntimeComponent component) {
        return new ComponentContextImpl(registry, compositeContext, component);
    }

}
