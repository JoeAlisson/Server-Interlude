package com.l2jbr.mmocore.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

public class ConnectionHandler<T extends AsyncMMOClient<AsyncMMOConnection<T>>> extends Thread {

    private final AsynchronousChannelGroup group;
    private final AsynchronousServerSocketChannel listener;
    private final WriteHandler<T> writeHandler;
    private boolean shutdown;
    private ClientFactory<T> clientFactory;
    private ReadHandler<T> readHandler;
    private PacketHandler<T>  packetHandler;


    public ConnectionHandler(InetSocketAddress address, int threadPoolSize, ClientFactory<T> clientFactory, PacketHandler<T> packetHandler)
            throws IOException {
        this.clientFactory = clientFactory;
        this.packetHandler = packetHandler;

        this.readHandler = new ReadHandler<>(packetHandler);
        this.writeHandler = new WriteHandler<>();
        group = createChannelGroup(threadPoolSize);
        listener = group.provider().openAsynchronousServerSocketChannel(group);
        listener.bind(address);
    }

    private AsynchronousChannelGroup createChannelGroup(int threadPoolSize) throws IOException {
        if(threadPoolSize <= 0 || threadPoolSize >= Integer.MAX_VALUE) {
            return AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 5);
        }
        return AsynchronousChannelGroup.withFixedThreadPool(threadPoolSize, Executors.defaultThreadFactory());
    }

    @Override
    public void run() {
        listener.accept(null, new AcceptConnectionHandler());

        while (!shutdown) {
            try  {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        closeConnection();
    }

    private void closeConnection() {
        try {
            listener.close();
            group.shutdownNow();
            group.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (Exception e) {  }
    }

    private void acceptConnection(AsynchronousSocketChannel channel) {
        if(nonNull(channel) && channel.isOpen()) {
            try {
                channel.setOption(StandardSocketOptions.TCP_NODELAY, false);
            } catch (Exception e) {

            }

            AsyncMMOConnection<T> connection = new AsyncMMOConnection<>(channel, readHandler, writeHandler);
            clientFactory.create(connection);
            connection.read();

        } else {
            System.out.println("Channel Closed");
        }
    }

    private void handleDisconnection(T client) {
        client.onDisconnection();
    }

    private void onFinishedIO(T client) {
        /*ByteBufferPool.recycleBuffer(client.getReadingBuffer());*/
    }

    private class AcceptConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
            if(!shutdown && listener.isOpen()) {
                listener.accept(null, this);
            }
            acceptConnection(clientChannel);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            System.out.println(exc.getLocalizedMessage());
        }
    }



}
