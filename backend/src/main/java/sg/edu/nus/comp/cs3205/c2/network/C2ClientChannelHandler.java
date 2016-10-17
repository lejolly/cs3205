package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.keys.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.utils.JwsUtils;

@Sharable
public class C2ClientChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C2ClientChannelHandler.class.getSimpleName());

    private C2NetworkClient c2NetworkClient;

    C2ClientChannelHandler(C2NetworkClient c2NetworkClient) {
        this.c2NetworkClient = c2NetworkClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            JwtClaims jwtClaims = JwsUtils.consumeSignedMessageWithId(C2KeyManager.c3RsaPublicKey, msg);
            logger.info("Message from C3: \"" + jwtClaims.toString() + "\"");
            c2NetworkClient.handleMessageFromC3(jwtClaims);
        } catch (InvalidJwtException e) {
            logger.error("InvalidJwtException: ", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        ctx.close();
    }

}
