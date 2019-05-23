package com.test.socket.util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class NioConnect {

    /**
     * 根据IP:port获取session连接
     *
     * @param ip
     * @param port
     * @return
     */
    public static IoSession getSession(String ip, String port) {

        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(3000L);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
        SocketSessionConfig cfg = connector.getSessionConfig();
        cfg.setUseReadOperation(true);
        IoSession session = connector.connect(new InetSocketAddress(ip, Integer.valueOf(port))).awaitUninterruptibly()
                .getSession();
        return session;
    }


}
