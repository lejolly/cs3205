package sg.edu.nus.comp.cs3205.c2.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TcpServer(int port) {
        try {
            LOGGER.info("TCP server starting up");
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new TcpServerChannelInitializer());
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException when binding server to port " + port, e);
        } finally {
            LOGGER.info("TCP server shutting down");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
