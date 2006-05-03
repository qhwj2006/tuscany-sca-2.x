/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model;

import java.util.Map;
import java.util.HashMap;

/**
 * @version $Rev$ $Date$
 */
public class Component<T extends Implementation> extends ModelObject {
    private String name;
    private final T implementation;
    private final Map<String, ReferenceTarget> referenceTargets = new HashMap<String, ReferenceTarget>();
    private final Map<String, PropertyValue<?>> propertyValues = new HashMap<String, PropertyValue<?>>();

    public Component(T implementation) {
        this.implementation = implementation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getImplementation() {
        return implementation;
    }

    public Map<String, ReferenceTarget> getReferenceTargets() {
        return referenceTargets;
    }

    public Map<String, PropertyValue<?>> getPropertyValues() {
        return propertyValues;
    }
}
