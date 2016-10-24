package sg.edu.nus.comp.cs3205.c3.network;

import com.google.gson.JsonSyntaxException;
import io.netty.channel.*;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.auth.C3LoginManager;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.key.C3KeyManager;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;
import sg.edu.nus.comp.cs3205.common.data.json.BaseJsonFormat;
import sg.edu.nus.comp.cs3205.common.data.json.SaltRequest;
import sg.edu.nus.comp.cs3205.common.utils.JsonUtils;
import sg.edu.nus.comp.cs3205.common.utils.JwsUtils;

@ChannelHandler.Sharable
public class C3ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class.getSimpleName());

    private C3SessionManager c3SessionManager;
    private C3LoginManager c3LoginManager;

    C3ServerChannelHandler(C3SessionManager c3SessionManager, C3LoginManager c3LoginManager) {
        this.c3SessionManager = c3SessionManager;
        this.c3LoginManager = c3LoginManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().toString();
        logger.info("New connection: " + id);
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info("Received: " + request);
        String response = "error" + "\r\n";
        try {
            BaseJsonFormat baseJsonFormat = JsonUtils.fromJsonString(request);
            SaltRequest saltRequest = SaltRequest.fromBaseFormat(baseJsonFormat);
            if (saltRequest != null) {
                logger.info("Received salt_request");
                response = c3LoginManager.getUserSalt(saltRequest).getJsonString() + "\r\n";
            }
        } catch (JsonSyntaxException e) {
            logger.error("JsonSyntaxException: ", e);
        }
        logger.info("Sending response: " + response);
        ChannelFuture future = ctx.write(response);
    }

    public void oldChannelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        // Generate and write a response.
        boolean close = false;
        String id = "";
        String response = JwsUtils.getSimpleSignedMessageWithId(C3KeyManager.c3RsaPrivateKey, id, "error") + "\r\n";
        try {
            JwtClaims jwtClaims = JwsUtils.consumeSignedMessageWithId(C3KeyManager.c2RsaPublicKey, request);
            logger.info(ctx.channel() + " received: \"" + jwtClaims.toString() + "\"");
            if (request.isEmpty()) {
                response = JwsUtils.getSimpleSignedMessageWithId(
                        C3KeyManager.c3RsaPrivateKey, id, "Please type something") + "\r\n";
            } else if (jwtClaims.hasClaim("message") &&
                    "bye".equals(((String) jwtClaims.getClaimsMap().get("message")).toLowerCase())) {
//                response = JwsUtils.getSimpleSignedMessageWithId(
//                        C3KeyManager.c3RsaPrivateKey, id, "Have a good day!") + "\r\n";
                response = "";
                close = true;
            } else {
                if (jwtClaims.getClaimsMap().containsKey("actor_id")) {
                    int actor_id = Integer.parseInt(String.valueOf(jwtClaims.getClaimsMap().get("actor_id")));
                    response = JwsUtils.getSignedFieldWithId(C3KeyManager.c3RsaPrivateKey, id, "actor_info",
                            C3DatabaseManager.getActorInfo(actor_id)) + "\r\n";
                } else if (jwtClaims.getClaimsMap().containsKey("message")) {
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
        ctx.close();
    }

}
