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
package org.apache.tuscany.core.databinding.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

public final class StAXHelper {
    private static final XMLInputFactory INPUT_FACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    private StAXHelper() {
    }

    /**
     * This class is derived from Apache Axis2 class org.apache.axis2.util.StreamWrapper</a>. It's used wrap a
     * XMLStreamReader to create a XMLStreamReader representing a document and it will produce START_DOCUMENT,
     * END_DOCUMENT events.
     */
    public static class XMLDocumentStreamReader implements XMLStreamReader {
        private static final int STATE_COMPLETE_AT_NEXT = 2; // The wrapper
        // will produce
        // END_DOCUMENT

        private static final int STATE_COMPLETED = 3; // Done

        private static final int STATE_INIT = 0; // The wrapper will produce
        // START_DOCUMENT

        private static final int STATE_SWITCHED = 1; // The real reader will
        // produce events

        private XMLStreamReader realReader;

        private int state = STATE_INIT;

        public XMLDocumentStreamReader(XMLStreamReader realReader) {
            if (realReader == null) {
                throw new UnsupportedOperationException("Reader cannot be null");
            }

            this.realReader = realReader;

            // If the real reader is positioned at START_DOCUMENT, always use
            // the real reader
            if (realReader.getEventType() == START_DOCUMENT) {
                state = STATE_SWITCHED;
            }
        }

        public void close() throws XMLStreamException {
            realReader.close();
        }

        public int getAttributeCount() {
            if (isDelegating()) {
                return realReader.getAttributeCount();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeLocalName(int i) {
            if (isDelegating()) {
                return realReader.getAttributeLocalName(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public QName getAttributeName(int i) {
            if (isDelegating()) {
                return realReader.getAttributeName(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeNamespace(int i) {
            if (isDelegating()) {
                return realReader.getAttributeNamespace(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributePrefix(int i) {
            if (isDelegating()) {
                return realReader.getAttributePrefix(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeType(int i) {
            if (isDelegating()) {
                return realReader.getAttributeType(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeValue(int i) {
            if (isDelegating()) {
                return realReader.getAttributeValue(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getAttributeValue(String s, String s1) {
            if (isDelegating()) {
                return realReader.getAttributeValue(s, s1);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getCharacterEncodingScheme() {
            return realReader.getCharacterEncodingScheme();
        }

        public String getElementText() throws XMLStreamException {
            if (isDelegating()) {
                return realReader.getElementText();
            } else {
                throw new XMLStreamException();
            }
        }

        public String getEncoding() {
            return realReader.getEncoding();
        }

        public int getEventType() {
            int event = -1;
            switch (state) {
                case STATE_SWITCHED:
                case STATE_COMPLETE_AT_NEXT:
                    event = realReader.getEventType();
                    break;
                case STATE_INIT:
                    event = START_DOCUMENT;
                    break;
                case STATE_COMPLETED:
                    event = END_DOCUMENT;
                    break;
            }
            return event;
        }

        public String getLocalName() {
            if (isDelegating()) {
                return realReader.getLocalName();
            } else {
                throw new IllegalStateException();
            }
        }

        public Location getLocation() {
            if (isDelegating()) {
                return realReader.getLocation();
            } else {
                return null;
            }
        }

        public QName getName() {
            if (isDelegating()) {
                return realReader.getName();
            } else {
                throw new IllegalStateException();
            }
        }

        public NamespaceContext getNamespaceContext() {
            return realReader.getNamespaceContext();
        }

        public int getNamespaceCount() {
            if (isDelegating()) {
                return realReader.getNamespaceCount();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getNamespacePrefix(int i) {
            if (isDelegating()) {
                return realReader.getNamespacePrefix(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getNamespaceURI() {
            if (isDelegating()) {
                return realReader.getNamespaceURI();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getNamespaceURI(int i) {
            if (isDelegating()) {
                return realReader.getNamespaceURI(i);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getNamespaceURI(String s) {
            if (isDelegating()) {
                return realReader.getNamespaceURI(s);
            } else {
                throw new IllegalStateException();
            }
        }

        public String getPIData() {
            if (isDelegating()) {
                return realReader.getPIData();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getPITarget() {
            if (isDelegating()) {
                return realReader.getPITarget();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getPrefix() {
            if (isDelegating()) {
                return realReader.getPrefix();
            } else {
                throw new IllegalStateException();
            }
        }

        public Object getProperty(String s) throws IllegalArgumentException {
            if (isDelegating()) {
                return realReader.getProperty(s);
            } else {
                throw new IllegalArgumentException();
            }
        }

        public String getText() {
            if (isDelegating()) {
                return realReader.getText();
            } else {
                throw new IllegalStateException();
            }
        }

        public char[] getTextCharacters() {
            if (isDelegating()) {
                return realReader.getTextCharacters();
            } else {
                throw new IllegalStateException();
            }
        }

        public int getTextCharacters(int i, char[] chars, int i1, int i2) throws XMLStreamException {
            if (isDelegating()) {
                return realReader.getTextCharacters(i, chars, i1, i2);
            } else {
                throw new IllegalStateException();
            }
        }

        public int getTextLength() {
            if (isDelegating()) {
                return realReader.getTextLength();
            } else {
                throw new IllegalStateException();
            }
        }

        public int getTextStart() {
            if (isDelegating()) {
                return realReader.getTextStart();
            } else {
                throw new IllegalStateException();
            }
        }

        public String getVersion() {
            if (isDelegating()) {
                return realReader.getVersion();
            } else {
                return null;
            }
        }

        public boolean hasName() {
            if (isDelegating()) {
                return realReader.hasName();
            } else {
                return false;
            }
        }

        public boolean hasNext() throws XMLStreamException {
            if (state == STATE_COMPLETE_AT_NEXT) {
                return true;
            } else if (state == STATE_COMPLETED) {
                return false;
            } else if (state == STATE_SWITCHED) {
                return realReader.hasNext();
            } else {
                return true;
            }
        }

        public boolean hasText() {
            if (isDelegating()) {
                return realReader.hasText();
            } else {
                return false;
            }
        }

        public boolean isAttributeSpecified(int i) {
            if (isDelegating()) {
                return realReader.isAttributeSpecified(i);
            } else {
                return false;
            }
        }

        public boolean isCharacters() {
            if (isDelegating()) {
                return realReader.isCharacters();
            } else {
                return false;
            }
        }

        private boolean isDelegating() {
            return state == STATE_SWITCHED || state == STATE_COMPLETE_AT_NEXT;
        }

        public boolean isEndElement() {
            if (isDelegating()) {
                return realReader.isEndElement();
            } else {
                return false;
            }
        }

        public boolean isStandalone() {
            if (isDelegating()) {
                return realReader.isStandalone();
            } else {
                return false;
            }
        }

        public boolean isStartElement() {
            if (isDelegating()) {
                return realReader.isStartElement();
            } else {
                return false;
            }
        }

        public boolean isWhiteSpace() {
            if (isDelegating()) {
                return realReader.isWhiteSpace();
            } else {
                return false;
            }
        }

        public int next() throws XMLStreamException {
            int returnEvent;

            switch (state) {
                case STATE_SWITCHED:
                    returnEvent = realReader.next();
                    if (returnEvent == END_DOCUMENT) {
                        state = STATE_COMPLETED;
                    } else if (!realReader.hasNext()) {
                        state = STATE_COMPLETE_AT_NEXT;
                    }
                    break;
                case STATE_INIT:
                    state = STATE_SWITCHED;
                    returnEvent = realReader.getEventType();
                    break;
                case STATE_COMPLETE_AT_NEXT:
                    state = STATE_COMPLETED;
                    returnEvent = END_DOCUMENT;
                    break;
                case STATE_COMPLETED:
                    // oops - no way we can go beyond this
                    throw new NoSuchElementException("End of stream has reached.");
                default:
                    throw new UnsupportedOperationException();
            }

            return returnEvent;
        }

        public int nextTag() throws XMLStreamException {
            if (isDelegating()) {
                return realReader.nextTag();
            } else {
                throw new XMLStreamException();
            }
        }

        public void require(int i, String s, String s1) throws XMLStreamException {
            if (isDelegating()) {
                realReader.require(i, s, s1);
            }
        }

        public boolean standaloneSet() {
            if (isDelegating()) {
                return realReader.standaloneSet();
            } else {
                return false;
            }
        }
    }

    public static interface XMLFragmentStreamReader extends XMLStreamReader {

        // this will help to handle Text within the current element.
        // user should pass the element text to the property list as this
        // ELEMENT_TEXT as the key. This key deliberately has a space in it
        // so that it is not a valid XML name
        String ELEMENT_TEXT = "Element Text";

        /**
         * Initiate the parser - this will do whatever the needed tasks to initiate the parser and must be called before
         * attempting any specific parsing using this parser
         */
        void init();

        /**
         * Extra method to query the state of the pullparser
         */
        boolean isEndOfFragment();

        /**
         * add the parent namespace context to this parser
         */
        void setParentNamespaceContext(NamespaceContext nsContext);
    }

    /**
     * The XMLStreamSerializer pulls events from the XMLStreamReader and dumps into the XMLStreamWriter
     */
    public static class XMLStreamSerializer implements XMLStreamConstants {
        public static final String NAMESPACE_PREFIX = "ns";
        private static int namespaceSuffix;

        /*
         * The behavior of the serializer is such that it returns when it
         * encounters the starting element for the second time. The depth
         * variable tracks the depth of the serilizer and tells it when to
         * return. Note that it is assumed that this serialization starts on an
         * Element.
         */

        /**
         * Field depth
         */
        private int depth;

        /**
         * Generates a unique namespace prefix that is not in the scope of the NamespaceContext
         *
         * @param nsCtxt
         * @return string
         */
        private String generateUniquePrefix(NamespaceContext nsCtxt) {
            String prefix = NAMESPACE_PREFIX + namespaceSuffix++;
            // null should be returned if the prefix is not bound!
            while (nsCtxt.getNamespaceURI(prefix) != null) {
                prefix = NAMESPACE_PREFIX + namespaceSuffix++;
            }

            return prefix;
        }

        /**
         * Method serialize.
         *
         * @param node
         * @param writer
         * @throws XMLStreamException
         */
        public void serialize(XMLStreamReader node, XMLStreamWriter writer) throws XMLStreamException {
            serializeNode(node, writer);
        }

        /**
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeAttributes(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            int count = reader.getAttributeCount();
            String prefix;
            String namespaceName;
            String writerPrefix;
            for (int i = 0; i < count; i++) {
                prefix = reader.getAttributePrefix(i);
                namespaceName = reader.getAttributeNamespace(i);
                /*
                 * Due to parser implementations returning null as the namespace
                 * URI (for the empty namespace) we need to make sure that we
                 * deal with a namespace name that is not null. The best way to
                 * work around this issue is to set the namespace uri to "" if
                 * it is null
                 */
                if (namespaceName == null) {
                    namespaceName = "";
                }

                writerPrefix = writer.getNamespaceContext().getPrefix(namespaceName);

                if (!"".equals(namespaceName)) {
                    // prefix has already being declared but this particular
                    // attrib has a
                    // no prefix attached. So use the prefix provided by the
                    // writer
                    if (writerPrefix != null && (prefix == null || prefix.equals(""))) {
                        writer.writeAttribute(writerPrefix,
                            namespaceName,
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));

                        // writer prefix is available but different from the
                        // current
                        // prefix of the attrib. We should be decalring the new
                        // prefix
                        // as a namespace declaration
                    } else if (prefix != null && !"".equals(prefix) && !prefix.equals(writerPrefix)) {
                        writer.writeNamespace(prefix, namespaceName);
                        writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                            .getAttributeValue(i));

                        // prefix is null (or empty), but the namespace name is
                        // valid! it has not
                        // being written previously also. So we need to generate
                        // a prefix
                        // here
                    } else if (prefix == null || prefix.equals("")) {
                        prefix = generateUniquePrefix(writer.getNamespaceContext());
                        writer.writeNamespace(prefix, namespaceName);
                        writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                            .getAttributeValue(i));
                    } else {
                        writer.writeAttribute(prefix, namespaceName, reader.getAttributeLocalName(i), reader
                            .getAttributeValue(i));
                    }
                } else {
                    // empty namespace is equal to no namespace!
                    writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                }

            }
        }

        /**
         * Method serializeCData.
         *
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeCData(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            writer.writeCData(reader.getText());
        }

        /**
         * Method serializeComment.
         *
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeComment(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            writer.writeComment(reader.getText());
        }

        /**
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeElement(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            String prefix = reader.getPrefix();
            String nameSpaceName = reader.getNamespaceURI();
            if (nameSpaceName != null) {
                String writerPrefix = writer.getPrefix(nameSpaceName);
                if (writerPrefix != null) {
                    writer.writeStartElement(nameSpaceName, reader.getLocalName());
                } else {
                    if (prefix != null) {
                        writer.writeStartElement(prefix, reader.getLocalName(), nameSpaceName);
                        writer.writeNamespace(prefix, nameSpaceName);
                        writer.setPrefix(prefix, nameSpaceName);
                    } else {
                        writer.writeStartElement(nameSpaceName, reader.getLocalName());
                        writer.writeDefaultNamespace(nameSpaceName);
                        writer.setDefaultNamespace(nameSpaceName);
                    }
                }
            } else {
                writer.writeStartElement(reader.getLocalName());
            }

            // add the namespaces
            int count = reader.getNamespaceCount();
            String namespacePrefix;
            for (int i = 0; i < count; i++) {
                namespacePrefix = reader.getNamespacePrefix(i);
                if (namespacePrefix != null && namespacePrefix.length() == 0) {
                    continue;
                }

                serializeNamespace(namespacePrefix, reader.getNamespaceURI(i), writer);
            }

            // add attributes
            serializeAttributes(reader, writer);

        }

        /**
         * Method serializeEndElement.
         *
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeEndElement(XMLStreamWriter writer) throws XMLStreamException {
            writer.writeEndElement();
        }

        /**
         * Method serializeNamespace.
         *
         * @param prefix
         * @param uri
         * @param writer
         * @throws XMLStreamException
         */
        private void serializeNamespace(String prefix, String uri, XMLStreamWriter writer)
            throws XMLStreamException {
            String prefix1 = writer.getPrefix(uri);
            if (prefix1 == null) {
                writer.writeNamespace(prefix, uri);
                writer.setPrefix(prefix, uri);
            }
        }

        /**
         * Method serializeNode.
         *
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            // TODO We get the StAXWriter at this point and uses it hereafter
            // assuming that this is the only entry point
            // to this class.
            // If there can be other classes calling methodes of this we might
            // need to change methode signatures to
            // OMOutputer
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == START_ELEMENT) {
                    serializeElement(reader, writer);
                    depth++;
                } else if (event == ATTRIBUTE) {
                    serializeAttributes(reader, writer);
                } else if (event == CHARACTERS) {
                    serializeText(reader, writer);
                } else if (event == COMMENT) {
                    serializeComment(reader, writer);
                } else if (event == CDATA) {
                    serializeCData(reader, writer);
                } else if (event == END_ELEMENT) {
                    serializeEndElement(writer);
                    depth--;
                } else if (event == START_DOCUMENT) {
                    depth++; // if a start document is found then increment
                    // the depth
                } else if (event == END_DOCUMENT) {
                    if (depth != 0) {
                        depth--; // for the end document - reduce the depth
                    }
                    try {
                        serializeEndElement(writer);
                    } catch (Exception e) {
                        // TODO: log exceptions
                    }
                }
                if (depth == 0) {
                    break;
                }
            }
        }

        /**
         * @param reader
         * @param writer
         * @throws XMLStreamException
         */
        protected void serializeText(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
            writer.writeCharacters(reader.getText());
        }
    }

    public static XMLStreamReader createXMLStreamReader(InputStream inputStream) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(inputStream);
    }

    public static XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(reader);
    }

    public static XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return INPUT_FACTORY.createXMLStreamReader(source);
    }

    public static XMLStreamReader createXMLStreamReader(String string) throws XMLStreamException {
        StringReader reader = new StringReader(string);
        return createXMLStreamReader(reader);
    }

    public static String save(XMLStreamReader reader) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        save(reader, writer);
        return writer.toString();
    }

    public static void save(XMLStreamReader reader, OutputStream outputStream) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(outputStream);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public static void save(XMLStreamReader reader, Writer writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        XMLStreamWriter streamWriter = OUTPUT_FACTORY.createXMLStreamWriter(writer);
        serializer.serialize(reader, streamWriter);
        streamWriter.flush();
    }

    public static void save(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        XMLStreamSerializer serializer = new XMLStreamSerializer();
        serializer.serialize(reader, writer);
        writer.flush();
    }

}
