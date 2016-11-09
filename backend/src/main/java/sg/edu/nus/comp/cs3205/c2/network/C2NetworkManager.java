package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.data.config.C2Config;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.Key;
import java.util.Optional;

public class C2NetworkManager {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkManager.class);

    public Key key = null;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private int c2ClientPort;
    private String c2ClientHost;

    public C2NetworkManager() {
        logger.info("Initializing network manager.");
        Optional<C2Config> c2Config = JsonUtils.readJsonFile("config/c2.json", C2Config.class);
        if (c2Config.isPresent()) {
            c2ClientPort = c2Config.get().getC2ClientPort();
            c2ClientHost = c2Config.get().getC2ClientHost();
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new C2ServerChannelInitializer(this));
                b.bind(c2Config.get().getC2ServerPort()).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("InterruptedException: ", e);
            }
        } else {
            logger.error("Unable to get C2 config.");
        }
    }

    C2NetworkForwarder getNetworkClient(C2ServerChannelHandler c2ServerChannelHandler) {
        return new C2NetworkForwarder(workerGroup, c2ServerChannelHandler, c2ClientPort, c2ClientHost);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
