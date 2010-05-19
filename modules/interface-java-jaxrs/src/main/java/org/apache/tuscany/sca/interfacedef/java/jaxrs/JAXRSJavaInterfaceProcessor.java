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

package org.apache.tuscany.sca.interfacedef.java.jaxrs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceVisitor;

public class JAXRSJavaInterfaceProcessor implements JavaInterfaceVisitor {
    private static Map<String, Class<?>> mapping = new HashMap<String, Class<?>>();
    static {
        mapping.put(HttpMethod.GET, GET.class);
        mapping.put(HttpMethod.POST, POST.class);
        mapping.put(HttpMethod.PUT, PUT.class);
        mapping.put(HttpMethod.DELETE, DELETE.class);
        mapping.put(HttpMethod.HEAD, HEAD.class);
        mapping.put(HttpMethod.OPTIONS, OPTIONS.class);
    }

    private boolean introspectHTTPMethod(JavaOperation operation) {
        Method method = operation.getJavaMethod();

        String methodName = null;
        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
        if (httpMethod != null) {
            methodName = httpMethod.value();
        }
        if (method.isAnnotationPresent(GET.class)) {
            methodName = HttpMethod.GET;
        } else if (method.isAnnotationPresent(POST.class)) {
            methodName = HttpMethod.POST;
        } else if (method.isAnnotationPresent(PUT.class)) {
            methodName = HttpMethod.PUT;
        } else if (method.isAnnotationPresent(DELETE.class)) {
            methodName = HttpMethod.DELETE;
        } else if (method.isAnnotationPresent(HEAD.class)) {
            methodName = HttpMethod.HEAD;
        } else if (method.isAnnotationPresent(OPTIONS.class)) {
            methodName = HttpMethod.OPTIONS;
        }

        boolean jaxrs = false;
        Class<?> type = mapping.get(methodName);
        if (type != null) {
            jaxrs = true;
            operation.getAttributes().put(type, Boolean.TRUE);
            Map<Object, Object> attrs = operation.getInterface().getAttributes();
            List<Operation> operations = (List<Operation>)attrs.get(type);
            if (operations == null) {
                operations = new ArrayList<Operation>();
                attrs.put(type, operations);
                operations.add(operation);
            } else {
                operations.add(operation);
            }
        }

        return jaxrs;

    }

    public void visitInterface(JavaInterface contract) throws InvalidInterfaceException {

        boolean hasJAXRSAnnotarions = false;

        for (Operation op : contract.getOperations()) {
            final JavaOperation operation = (JavaOperation)op;
            if (introspectHTTPMethod(operation)) {
                hasJAXRSAnnotarions = true;
            }
        }

        // Always set JAX-RS annotated interfaces as remotables
        if (hasJAXRSAnnotarions) {
            contract.setRemotable(true);
        }
    }
}
