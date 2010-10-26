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

import static org.junit.Assert.assertEquals;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.Test;

/**
 * A unit test of the basic Java implementation classes in this contribution 
 * without using SCA
 */
public class CalculatorTestCase {

    @Test
    public void testCalculator() throws Exception {
        
        Node node = NodeFactory.newInstance().createNode(new Contribution("c1", "./target/classes"));
        node.start();
        
        CalculatorService calculator = node.getService(CalculatorService.class, "CalculatorServiceComponent");
        
        assertEquals(calculator.add(3, 2), 5.0, 0);
        assertEquals(calculator.subtract(3, 2), 1.0, 0);
        assertEquals(calculator.multiply(3, 2), 6.0, 0);
        assertEquals(calculator.divide(3, 2), 1.5, 0);
        
        node.stop();        
    }
}
