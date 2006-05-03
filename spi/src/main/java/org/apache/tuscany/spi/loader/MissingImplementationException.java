/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.spi.loader;

/**
 * Exception that indicates that an implementation was not provided.
 *
 * @version $Rev: 392764 $ $Date: 2006-04-09 09:13:55 -0700 (Sun, 09 Apr 2006) $
 */
public class MissingImplementationException extends LoaderException {
    private static final long serialVersionUID = -2917278473974880124L;

    /**
     * Default constructor.
     *
     */
    public MissingImplementationException() {
        super();
    }
}
