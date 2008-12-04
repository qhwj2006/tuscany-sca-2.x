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
package org.apache.tuscany.sca.implementation.java.context;

import org.apache.tuscany.sca.core.factory.InstanceWrapper;

/**
 * Interface for a factory that returns an injected component instance.
 * This is used by a Component implementation to create new instances of
 * application implementation objects as determined by the component scope's
 * lifecycle.
 * <p/>
 * The implementation of this interface may be supplied by the user,
 * may be generated during deployment, or may be dynamic.
 *
 * @version $Rev$ $Date$
 * @param <T> Type of the instance generated by the factory.
 */
public interface InstanceFactory<T> {
    /**
     * Creates a new instance of the component.
     * All injected values must be set but any @Init methods must not have been invoked.
     * 
     * @return A wrapper for the created component instance.
     */
    InstanceWrapper<T> newInstance();
}
