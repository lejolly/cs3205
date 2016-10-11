package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;

public class NetworkManager {

    private static Logger logger = LoggerFactory.getLogger(NetworkManager.class.getSimpleName());

    private static final int SERVER_PORT = 8081;

    public Key key = null;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NetworkManager() {
        logger.info("Initializing network manager.");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelInitializer(this));
            b.bind(SERVER_PORT).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    public NetworkClient getNetworkClient(ServerChannelHandler serverChannelHandler) {
        return new NetworkClient(workerGroup, serverChannelHandler);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
