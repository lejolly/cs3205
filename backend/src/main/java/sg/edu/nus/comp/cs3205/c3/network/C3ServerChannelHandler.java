package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.channel.*;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.keys.C3KeyManager;
import sg.edu.nus.comp.cs3205.common.utils.JwsUtils;

import java.util.HashMap;

@ChannelHandler.Sharable
public class C3ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class.getSimpleName());

    private HashMap<Channel, String> ids;

    C3ServerChannelHandler(HashMap<Channel, String> ids) {
        this.ids = ids;
        System.out.println(C3DatabaseManager.getActorCount());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().toString();
        logger.info("New connection: " + id);
        ids.put(ctx.channel(), id);
        ctx.write(JwsUtils.getSimpleSignedMessageWithId(C3KeyManager.c3RsaPrivateKey, id, "Welcome") + "\r\n");
        ctx.write(JwsUtils.getSignedFieldWithId(C3KeyManager.c3RsaPrivateKey, id, "num_actors",
                String.valueOf(C3DatabaseManager.getActorCount())) + "\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info(ctx.channel() + " received: \"" + request + "\"");
        // Generate and write a response.
        boolean close = false;
        String id = ids.get(ctx.channel());
        String response = JwsUtils.getSimpleSignedMessageWithId(C3KeyManager.c3RsaPrivateKey, id, "error") + "\r\n";
        try {
            JwtClaims jwtClaims = JwsUtils.consumeSignedMessageWithId(C3KeyManager.c2RsaPublicKey, request);
            if (request.isEmpty() || !jwtClaims.hasClaim("message") ||
                    ((String) jwtClaims.getClaimsMap().get("message")).isEmpty()) {
                response = JwsUtils.getSimpleSignedMessageWithId(
                        C3KeyManager.c3RsaPrivateKey, id, "Please type something") + "\r\n";
            } else if ("bye".equals(((String) jwtClaims.getClaimsMap().get("message")).toLowerCase())) {
                response = JwsUtils.getSimpleSignedMessageWithId(
                        C3KeyManager.c3RsaPrivateKey, id, "Have a good day!") + "\r\n";
                close = true;
            } else {
                if (jwtClaims.getClaimsMap().containsKey("actor_id")) {
                    int actor_id = Integer.parseInt(String.valueOf(jwtClaims.getClaimsMap().get("actor_id")));
                    response = JwsUtils.getSignedFieldWithId(C3KeyManager.c3RsaPrivateKey, id, "actor_info",
                            C3DatabaseManager.getActorInfo(actor_id)) + "\r\n";
                } else {
                    response = JwsUtils.getSimpleSignedMessageWithId(C3KeyManager.c3RsaPrivateKey, id,
                            "Got signed value: \"" + jwtClaims.getClaimsMap().get("message")) + "\"\r\n";
                }
            }
        } catch (InvalidJwtException e) {
            logger.error("InvalidJwtException: ", e);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException: ", e);
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: ", e);
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
        ChannelFuture future = ctx.write(response);

        // Close the connection after sending 'Have a good day!'
        // if the client has sent 'bye'.
        if (close) {
            logger.info("Closing connection " + ctx.channel());
            ids.remove(ctx.channel());
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        logger.info("Closing connection " + ctx.channel());
        ids.remove(ctx.channel());
        ctx.close();
    }

}
