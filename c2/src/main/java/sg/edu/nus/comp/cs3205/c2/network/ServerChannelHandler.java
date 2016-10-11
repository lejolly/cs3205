package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class.getSimpleName());

    private NetworkManager networkManager;
    private NetworkClient networkClient;
    private ChannelHandlerContext channelHandlerContext;

    ServerChannelHandler(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection: " + ctx.channel());
        networkClient = networkManager.getNetworkClient(this);
        channelHandlerContext = ctx;
        ctx.write("Welcome!\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info(ctx.channel() + " received: \"" + request + "\"");
        // Generate and write a response.
        String response = "error\r\n";
        boolean close = false;
        if (request.isEmpty()) {
            response = "Please type something.\r\n";
        } else if ("bye".equals(request.toLowerCase())) {
            response = "Have a good day!\r\n";
            close = true;
        } else {
            try {
                response = "Got input: \"" + request + "\"\r\n";
                networkClient.sendInput(request);
            } catch (IllegalArgumentException e) {
                logger.error("IllegalArgumentException: ", e);
            }
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
        ChannelFuture future = ctx.write(response);

        // Close the connection after sending 'Have a good day!'
        // if the client has sent 'bye'.
        if (close) {
            logger.info("Closing connection " + ctx.channel());
            networkClient.stopClient();
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void forwardReply(String reply) {
        channelHandlerContext.write(reply + "\r\n");
        channelHandlerContext.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        logger.info("Closing connection " + ctx.channel());
        ctx.close();
    }

}
