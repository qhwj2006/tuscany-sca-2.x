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

package org.apache.tuscany.sca.binding.http.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.binding.http.HTTPBindingFactory;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

public class HTTPBindingProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<HTTPBinding> {
    private static final String NAME = "name";
    private static final String URI = "uri";

    private HTTPBindingFactory httpBindingFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;
    private StAXAttributeProcessor<Object> extensionAttributeProcessor;
    

    public HTTPBindingProcessor(ExtensionPointRegistry extensionPoints, 
                                StAXArtifactProcessor extensionProcessor,
                                StAXAttributeProcessor extensionAttributeProcessor) {
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.httpBindingFactory = modelFactories.getFactory(HTTPBindingFactory.class);
        this.extensionProcessor = (StAXArtifactProcessor<Object>)extensionProcessor;
        this.extensionAttributeProcessor = extensionAttributeProcessor;
    }

    public QName getArtifactType() {
        return HTTPBinding.TYPE;
    }

    public Class<HTTPBinding> getModelType() {
        return HTTPBinding.class;
    }

    public HTTPBinding read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException, XMLStreamException {
        HTTPBinding httpBinding = httpBindingFactory.createHTTPBinding();

        while(reader.hasNext()) {
            QName elementName = null;
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    elementName = reader.getName();

                    if (HTTPBinding.TYPE.equals(elementName)) {
                        String name = getString(reader, NAME);
                        if(name != null) {
                            httpBinding.setName(name);
                        }

                        String uri = getURIString(reader, URI);
                        if (uri != null) {
                            httpBinding.setURI(uri);
                        }
                    } else {
                        // Read an extension element
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof WireFormat) {
                                httpBinding.setRequestWireFormat((WireFormat)extension);
                            } else if(extension instanceof OperationSelector) {
                                httpBinding.setOperationSelector((OperationSelector)extension);
                            }
                        }
                    }
            }

            if (event == END_ELEMENT && HTTPBinding.TYPE.equals(reader.getName())) {
                break;
            }

            // Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }

        return httpBinding;
    }

    public void write(HTTPBinding httpBinding, XMLStreamWriter writer, ProcessorContext context) throws ContributionWriteException, XMLStreamException {
        //writer.writeStartElement(Constants.SCA10_NS, BINDING_HTTP);

        writeStart(writer, HTTPBinding.TYPE.getNamespaceURI(), HTTPBinding.TYPE.getLocalPart());

        // Write binding name
        if (httpBinding.getName() != null) {
            writer.writeAttribute(NAME, httpBinding.getName());
        }

        // Write binding URI
        if (httpBinding.getURI() != null) {
            writer.writeAttribute(URI, httpBinding.getURI());
        }

        writeEnd(writer);
        //writer.writeEndElement();
    }


    public void resolve(HTTPBinding model, ModelResolver resolver, ProcessorContext context) throws ContributionResolveException {
        // Should not need to do anything here for now... 

    }

}
