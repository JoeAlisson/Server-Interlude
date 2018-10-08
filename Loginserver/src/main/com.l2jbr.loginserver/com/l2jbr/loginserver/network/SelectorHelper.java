package com.l2jbr.loginserver.network;

import com.l2jbr.loginserver.AuthController;
import org.l2j.mmocore.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author KenM
 */
public class SelectorHelper implements PacketExecutor<AuthClient>, ClientFactory<AuthClient>, ConnectionFilter {
    private final ThreadPoolExecutor _generalPacketsThreadPool;

    public SelectorHelper() {
        _generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    public void execute(ReadablePacket<AuthClient> packet) {
        _generalPacketsThreadPool.execute(packet);
    }

    @Override
    public AuthClient create(Connection<AuthClient> connection) {
        return new AuthClient(connection);
    }

    @Override
    public boolean accept(AsynchronousSocketChannel channel) {
        try {
            var socketAddress = (InetSocketAddress) channel.getRemoteAddress();
            return !AuthController.getInstance().isBannedAddress(socketAddress.getAddress().getHostAddress());
        } catch (IOException e) {
            return false;
        }
    }
}
