package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.*;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class C2ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C2ServerChannelHandler.class.getSimpleName());

    private C2NetworkManager c2NetworkManager;
    private C2NetworkClient c2NetworkClient;
    private ChannelHandlerContext channelHandlerContext;

    C2ServerChannelHandler(C2NetworkManager c2NetworkManager) {
        this.c2NetworkManager = c2NetworkManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection: " + ctx.channel());
        c2NetworkClient = c2NetworkManager.getNetworkClient(this);
        channelHandlerContext = ctx;
//        ctx.write("C2 says welcome!\r\n");
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
//            response = "Have a good day!\r\n";
            response = "";
            close = true;
        } else {
            try {
//                response = "Got input: \"" + request + "\"\r\n";
                response = "";
                c2NetworkClient.handleInput(request);
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
            c2NetworkClient.stopClient();
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws JoseException {
        logger.error("Exception: ", cause);
        logger.info("Closing connection " + ctx.channel());
        c2NetworkClient.stopClient();
        ctx.close();
    }

}
