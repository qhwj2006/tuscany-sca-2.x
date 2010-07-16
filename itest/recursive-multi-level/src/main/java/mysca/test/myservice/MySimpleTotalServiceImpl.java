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

package mysca.test.myservice;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

@Service(MySimpleTotalService.class)
public class MySimpleTotalServiceImpl implements MySimpleTotalService {
    // default required==true so it is 1:1
    @Reference
    public MyService myService;
    
    @Reference
    private MyService myService2;

    public String getLocation() {
        return myService.getLocation();
    }

    public String getYear() {
        return myService.getYear();
    }
    
    public String getYear2() {
        return myService2.getYear();
    }

}
