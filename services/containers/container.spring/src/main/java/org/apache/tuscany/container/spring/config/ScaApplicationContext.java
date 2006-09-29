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
package org.apache.tuscany.container.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.sca.ScaAdapterAware;
import org.springframework.sca.ScaAdapterPostProcessor;

import org.apache.tuscany.container.spring.impl.SpringScaAdapter;
import org.apache.tuscany.container.spring.model.SpringComponentType;

/**
 * @author Andy Piper
 * @since 2.1
 */
public class ScaApplicationContext extends AbstractXmlApplicationContext {
    public static final String APP_CONTEXT_PROP = "org.springframework.sca.application.context";
    private Resource appXml;
    private SpringComponentType componentType;

    public ScaApplicationContext(Resource appXml, SpringComponentType componentType) {
        this(null, appXml, componentType);
    }

    public ScaApplicationContext(ApplicationContext parent, Resource appXml, SpringComponentType componentType) {
        super(parent);
        this.appXml = appXml;
        this.componentType = componentType;
        refresh();
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        // beanDefinitionReader.setEntityResolver(null);
        beanDefinitionReader
                .setNamespaceHandlerResolver(new SCANamespaceHandlerResolver(getClassLoader(), componentType));
    }

    protected Resource[] getConfigResources() {
        return new Resource[]{appXml};
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new ScaAdapterPostProcessor(new SpringScaAdapter(componentType)));
        beanFactory.ignoreDependencyInterface(ScaAdapterAware.class);
    }
}
