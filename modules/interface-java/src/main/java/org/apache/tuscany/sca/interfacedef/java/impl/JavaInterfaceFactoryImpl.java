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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

/**
 * A factory for the Java model.
 *
 * @version $Rev$ $Date$
 */
public abstract class JavaInterfaceFactoryImpl implements JavaInterfaceFactory {
    
    private List<JavaInterfaceVisitor> visitors = new ArrayList<JavaInterfaceVisitor>();
    private JavaInterfaceIntrospectorImpl introspector;
    private Map<Class<?>, JavaInterface> cache  = Collections.synchronizedMap(new WeakHashMap<Class<?>, JavaInterface>());
    
    public JavaInterfaceFactoryImpl() {
        introspector = new JavaInterfaceIntrospectorImpl(this);
    }

    public JavaInterface createJavaInterface() {
        return new JavaInterfaceImpl();
    }
    
    public JavaInterface createJavaInterface(Class<?> interfaceClass) throws InvalidInterfaceException {
        // TODO: Review if the sharing of JavaInterface is ok
        synchronized (interfaceClass) {
            JavaInterface javaInterface = cache.get(interfaceClass);
            if (javaInterface == null) {
                javaInterface = createJavaInterface();
                introspector.introspectInterface(javaInterface, interfaceClass);
                // Now that all introspection is complete we can mark the interface resolved
                javaInterface.setUnresolved(false);
                cache.put(interfaceClass, javaInterface);
            }
            return javaInterface;
        }
    }
    
    public void createJavaInterface(JavaInterface javaInterface, Class<?> interfaceClass) throws InvalidInterfaceException {
        introspector.introspectInterface(javaInterface, interfaceClass);
    }
    
    public JavaInterfaceContract createJavaInterfaceContract() {
        return new JavaInterfaceContractImpl();
    }

    public void addInterfaceVisitor(JavaInterfaceVisitor extension) {
        visitors.add(extension);
    }

    public void removeInterfaceVisitor(JavaInterfaceVisitor extension) {
        visitors.remove(extension);
    }

    public List<JavaInterfaceVisitor> getInterfaceVisitors() {
        return visitors;
    }

    public JavaOperation createJavaOperation(Method method) {
        JavaOperation op =  new JavaOperationImpl();
        op.setJavaMethod(method);
        op.setName(method.getName());
        return op;
    }
}
