package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.key.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;

import java.security.NoSuchAlgorithmException;

@Sharable
public class C2ClientChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C2ClientChannelHandler.class);

    private C2NetworkForwarder c2NetworkForwarder;

    C2ClientChannelHandler(C2NetworkForwarder c2NetworkForwarder) {
        this.c2NetworkForwarder = c2NetworkForwarder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.debug("Received from C3: \"" + msg + "\"");
        if (!msg.equals("error")) {
            try {
                BaseJsonFormat baseJsonFormat = JsonUtils.consumeSignedBaseJsonFormat(C2KeyManager.c3RsaPublicKey, msg);
                if (baseJsonFormat != null) {
                    logger.info("Received from C3: \"" + baseJsonFormat.getJsonString() + "\"");
                    c2NetworkForwarder.handleMessageFromC3(baseJsonFormat);
                    return;
                }
            } catch (InvalidJwtException e) {
                logger.error("InvalidJwtException: ", e);
            } catch (JoseException e) {
                logger.error("JoseException: ", e);
            } catch (NoSuchAlgorithmException e) {
                logger.error("NoSuchAlgorithmException: ", e);
            }
        }
        logger.info("Invalid reply from C3 received");
        logger.info("Closing connection " + ctx.channel());
        ctx.close();
        c2NetworkForwarder.stopClient();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        ctx.close();
    }

}
