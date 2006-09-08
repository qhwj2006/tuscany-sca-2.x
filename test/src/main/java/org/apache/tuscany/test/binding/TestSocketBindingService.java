package org.apache.tuscany.test.binding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.WireService;

/**
 * Implements a very simple remote, socket-based binding for test purposes. This binding exposes services using a socket
 * on a given port. Service operations must take only one paramter that is <code>Serializable</code>.
 *
 * @version $Rev$ $Date$
 */
public class TestSocketBindingService<T> extends ServiceExtension<T> {
    private int port;
    private ServerSocket socket;
    private ExecutorService executor;
    private TestSocketBindingService<T>.ServerRunnable runnable;

    public TestSocketBindingService(String name,
                                    int port,
                                    Class<T> interfaze,
                                    CompositeComponent parent,
                                    WireService wireService) throws CoreRuntimeException {
        super(name, interfaze, parent, wireService);
        this.port = port;
    }

    public void start() {
        executor = Executors.newSingleThreadExecutor();
        // create a listener, note that a work scheduler should normally be used to spawn work in different threads
        runnable = new ServerRunnable();
        executor.execute(runnable);
    }

    public void stop() {
        try {
            runnable.setEnd(true);
            socket.close();
            executor.shutdownNow();
        } catch (IOException e) {
            throw new TestBindingRuntimeException(e);
        }
    }

    /**
     * Creates a socket listener in another thread which handles one client at a time. For a real binding, a work
     * scheduler should be used
     */
    private class ServerRunnable implements Runnable {

        private boolean end;

        public void setEnd(boolean end) {
            this.end = end;
        }

        public void run() {
            Socket clientSocket;
            ObjectInputStream is = null;
            ObjectOutputStream os = null;
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                throw new TestBindingRuntimeException(e);
            }
            while (!end) {
                try {
                    // only handle one client at a time
                    clientSocket = socket.accept();
                    is = new ObjectInputStream(clientSocket.getInputStream());
                    String operation = is.readUTF();
                    Object o = is.readObject();
                    Map<Operation<?>, InboundInvocationChain> chains = getInboundWire().getInvocationChains();
                    for (InboundInvocationChain chain : chains.values()) {
                        if (chain.getOperation().getName().equals(operation)) {
                            Message message = new MessageImpl();
                            message.setTargetInvoker(chain.getTargetInvoker());
                            message.setBody(new Object[]{o});
                            message = chain.getHeadInterceptor().invoke(message);
                            os = new ObjectOutputStream(clientSocket.getOutputStream());
                            os.writeObject(message.getBody());
                            os.flush();
                        }
                    }
                } catch (IOException e) {
                    throw new TestBindingRuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new TestBindingRuntimeException(e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            // ingore
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }
        }
    }
}
