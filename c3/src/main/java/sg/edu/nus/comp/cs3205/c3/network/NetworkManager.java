package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.HashMap;

public class NetworkManager {

    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class.getSimpleName());

    private static final int PORT = 8080;

    static HashMap<String, Key> keys;

    public NetworkManager() {
        logger.info("Initializing network manager.");

        keys = new HashMap<>();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelInitializer(keys));
            b.bind(PORT).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
