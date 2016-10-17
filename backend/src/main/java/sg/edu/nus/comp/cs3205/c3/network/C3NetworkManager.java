package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;
import sg.edu.nus.comp.cs3205.common.data.config.C3Config;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.util.HashMap;
import java.util.Optional;

public class C3NetworkManager extends AbstractManager {

    private static final Logger logger = LoggerFactory.getLogger(C3NetworkManager.class.getSimpleName());

    public C3NetworkManager() {
        logger.info("Initializing network manager.");
        Optional<C3Config> c3Config = JsonUtils.readJsonFile("config/c3.json", C3Config.class);
        if (c3Config.isPresent()) {
            HashMap<Channel, String> ids = new HashMap<>();
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new C3ServerChannelInitializer(ids));
                b.bind(c3Config.get().getC3ServerPort()).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("InterruptedException: ", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } else {
            logger.error("Unable to get C3 config.");
        }
    }

}
