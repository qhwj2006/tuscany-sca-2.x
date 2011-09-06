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

package calculator;


import java.util.logging.Logger;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class CalculatorActivator implements BundleActivator {
    private Logger logger = Logger.getLogger(CalculatorActivator.class.getName());

    public void start(BundleContext context) throws Exception {
        logger.info("Starting " + context.getBundle());
        Node node = TuscanyRuntime.runComposite("Calculator.composite", context.getBundle().getLocation());
        node.stop();
    }

    public void stop(BundleContext context) throws Exception {
        logger.info("Stopping " + context.getBundle());
        // Registered services will be automatically unregistered
    }

}
