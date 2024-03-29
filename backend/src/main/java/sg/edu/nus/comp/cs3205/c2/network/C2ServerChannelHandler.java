package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.key.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

@ChannelHandler.Sharable
public class C2ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C2ServerChannelHandler.class);

    private C2NetworkManager c2NetworkManager;
    private C2NetworkForwarder c2NetworkForwarder;
    private ChannelHandlerContext channelHandlerContext;

    C2ServerChannelHandler(C2NetworkManager c2NetworkManager) {
        this.c2NetworkManager = c2NetworkManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection from C1: " + ctx.channel());
        c2NetworkForwarder = c2NetworkManager.getNetworkClient(this);
        channelHandlerContext = ctx;
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info("Received from C1: \"" + request + "\"");
        c2NetworkForwarder.handleInputFromC1(request);
    }

    public void forwardReplyToC1(BaseJsonFormat baseJsonFormat) throws JoseException {
        String reply = baseJsonFormat.getJsonString();
        logger.info("Sending to C1: \"" + reply + "\"");
        reply = JsonUtils.getSignedBaseJsonFormat(C2KeyManager.c2RsaPrivateKey, baseJsonFormat);
        logger.info("Sending to C1: \"" + reply + "\"");
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
        c2NetworkForwarder.stopClient();
    }

    public void close() {
        channelHandlerContext.close();
    }

}
