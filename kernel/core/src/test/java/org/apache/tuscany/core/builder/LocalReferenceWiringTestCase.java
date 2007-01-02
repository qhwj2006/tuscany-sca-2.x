package org.apache.tuscany.core.builder;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;

import org.apache.tuscany.core.implementation.composite.ServiceImpl;
import org.apache.tuscany.core.mock.binding.MockServiceBinding;
import org.apache.tuscany.core.wire.InboundInvocationChainImpl;
import org.apache.tuscany.core.wire.InboundWireImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.SynchronousBridgingInterceptor;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Verifies various wiring "scenarios" or paths through the connector
 *
 * @version $Rev$ $Date$
 */
public class LocalReferenceWiringTestCase extends AbstractLocalWiringTestCase {
    private Service service;
    private Reference reference;
    private AtomicComponent atomicComponent;

    /**
     * Verifies the case where inbound and outbound reference wires are connected followed by the outbound reference
     * wire being connected to a target that is an atomic component and child of the reference's parent composite. This
     * wiring scenario occurs when a reference is configured with the local binding.
     */
    public void testConnectLocalReferenceBindingToAtomicComponentService() throws Exception {
        createLocalReferenceToSiblingAtomicConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    /**
     * Verifies the case where inbound and outbound reference wires are connected followed by the outbound reference
     * wire being connected to a target. This wiring scenario occurs when a reference is configured with the local
     * binding.
     */
    public void testConnectLocalReferenceBindingToCompositeService() throws Exception {
        createLocalReferenceToServiceConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectLocalReferenceBindingToCompositeServiceNoMatchingBinding() throws Exception {
        createLocalReferenceToInvalidServiceConfiguration();
        try {
            connector.connect(reference);
            fail();
        } catch (NoCompatibleBindingsException e) {
            // expected
        }
    }

    /**
     * Verifies a connection to a service offered by a sibling composite of the reference's parent
     *
     * @throws Exception
     */
    public void testConnectLocalReferenceBindingToSiblingCompositeService() throws Exception {
        createLocalReferenceToSiblingCompositeServiceConfiguration();
        connector.connect(reference);
        InboundInvocationChain chain = referenceBinding.getInboundWire().getInvocationChains().get(operation);
        Interceptor interceptor = chain.getHeadInterceptor();
        MessageImpl msg = new MessageImpl();
        msg.setTargetInvoker(new MockInvoker());
        Message resp = interceptor.invoke(msg);
        assertEquals(RESPONSE, resp.getBody());
    }

    public void testConnectLocalReferenceBindingToSiblingCompositeServiceNoMatchingBinding() throws Exception {
        createLocalReferenceToSiblingCompositeServiceConfigurationNoMatchingBinding();
        try {
            connector.connect(reference);
            fail();
        } catch (NoCompatibleBindingsException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void createLocalReferenceToServiceConfiguration() throws Exception {
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createLocalService(topComposite);
        reference = createLocalReference(parent, AbstractLocalWiringTestCase.TARGET_NAME);
    }

    private void createLocalReferenceToInvalidServiceConfiguration() throws Exception {
        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createService();
        reference = createLocalReference(parent, AbstractLocalWiringTestCase.TARGET_NAME);
    }

    private void createLocalReferenceToSiblingCompositeServiceConfiguration() throws Exception {
        final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
        sibling.getService(TARGET_SERVICE);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(sibling);

        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return sibling;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createLocalService(topComposite);
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private void createLocalReferenceToSiblingCompositeServiceConfigurationNoMatchingBinding() throws Exception {
        final CompositeComponent sibling = EasyMock.createMock(CompositeComponent.class);
        sibling.getService(TARGET_SERVICE);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return service;
            }
        });
        EasyMock.replay(sibling);

        CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return sibling;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite);
        EasyMock.replay(parent);

        service = createService();
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private void createLocalReferenceToSiblingAtomicConfiguration() throws Exception {
        final CompositeComponent topComposite = EasyMock.createMock(CompositeComponent.class);
        topComposite.getChild(TARGET);
        EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                return atomicComponent;
            }
        });
        EasyMock.replay(topComposite);

        CompositeComponent parent = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(parent.getParent()).andReturn(topComposite).atLeastOnce();
        EasyMock.replay(parent);
        atomicComponent = createAtomicTarget();
        reference = createLocalReference(parent, TARGET_SERVICE_NAME);
    }

    private Service createService() throws WireConnectException {
        QName qName = new QName("foo", "bar");
        ServiceBinding serviceBinding = new MockServiceBinding();
        InboundInvocationChain targetInboundChain = new InboundInvocationChainImpl(operation);
        targetInboundChain.addInterceptor(new SynchronousBridgingInterceptor());
        InboundWireImpl targetInboundWire = new InboundWireImpl();
        targetInboundWire.setBindingType(qName);
        targetInboundWire.setServiceContract(contract);
        targetInboundWire.addInvocationChain(operation, targetInboundChain);
        targetInboundWire.setContainer(serviceBinding);

        OutboundInvocationChain targetOutboundChain = new OutboundInvocationChainImpl(operation);
        // place an invoker interceptor on the end
        targetOutboundChain.addInterceptor(new InvokerInterceptor());
        OutboundWireImpl targetOutboundWire = new OutboundWireImpl();
        targetOutboundWire.setServiceContract(contract);
        targetOutboundWire.addInvocationChain(operation, targetOutboundChain);
        targetOutboundWire.setContainer(serviceBinding);
        targetOutboundWire.setBindingType(qName);

        serviceBinding.setInboundWire(targetInboundWire);
        serviceBinding.setOutboundWire(targetOutboundWire);
        // manually connect the service chains
        connector.connect(targetInboundChain, targetOutboundChain);
        Service service = new ServiceImpl(TARGET, null, contract);
        service.addServiceBinding(serviceBinding);
        return service;
    }


}
