package org.apache.tuscany.container.spring;

import org.apache.tuscany.container.spring.mock.TestBean;
import org.apache.tuscany.container.spring.mock.TestBeanImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Verifies a simple invocation on a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvocationTestCase extends MockObjectTestCase {

    public void testSpringInvocation() throws Exception {
        Mock mock = mock(GenericApplicationContext.class);
        mock.expects(atLeastOnce()).method("getBean").will(returnValue(new TestBeanImpl()));
        GenericApplicationContext ctx = (GenericApplicationContext) mock.proxy();
        SpringInvoker invoker = new SpringInvoker("foo", TestBean.class.getMethod("echo", String.class), ctx);
        assertEquals("foo", invoker.invokeTarget(new String[]{"foo"}));
    }

}
