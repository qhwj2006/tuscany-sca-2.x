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
package org.apache.tuscany.sca.test.callback;

import org.oasisopen.sca.RequestContext;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * This class implements MyService and uses a callback.
 * 
 * In this scenario, we exlore using the RequestContext to retrieve the callBack reference
 * as @Callback injection is not performed for composite-scoped implementations (see JavaCAA Section 7.2.5)
 * 
 * Changing the service implementation to be @Scope("STATELESS") would make plain @Callback injection work
 */
@Service(MyService.class)
@Scope("COMPOSITE")
public class MyServiceImpl implements MyService {

    @Context
    protected RequestContext requestContext;

    public void someMethod(String arg) {
        // invoke the callback
        try {
            MyServiceCallback myServiceCallback = requestContext.getCallback();
            myServiceCallback.receiveResult(arg + " -> receiveResult");
        } catch (RuntimeException e) {
            System.out.println("RuntimeException invoking receiveResult: " + e.toString());
        }
    }
}
