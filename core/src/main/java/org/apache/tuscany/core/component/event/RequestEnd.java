/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.component.event;

/**
 * Propagated when a request completes or is ended
 *
 * @version $$Rev$$ $$Date$$
 */
public class RequestEnd extends AbstractRequestEvent {

    /**
     * Creates a new event
     *
     * @param source the source of the event
     */
    public RequestEnd(Object source) {
        super(source);
    }


}
