package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.channel.*;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;

@ChannelHandler.Sharable
public class C3ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(C3ServerChannelHandler.class.getSimpleName());

    private HashMap<Channel, Key> keys;

    C3ServerChannelHandler(HashMap<Channel, Key> keys) {
        this.keys = keys;
        System.out.println(C3DatabaseManager.getActorCount());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection: " + ctx.channel());
        Key key = KeyGenerator.getInstance("HmacSHA256").generateKey();
        keys.put(ctx.channel(), key);
        ctx.write("key: " + Arrays.toString(key.getEncoded()) + "\r\n");
        ctx.write("Number of actors: " + C3DatabaseManager.getActorCount() + "\r\n");
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info(ctx.channel() + " received: \"" + request + "\"");
        // Generate and write a response.
        String response = "error\r\n";
        boolean close = false;
        if (!request.equals("key")) {
            if (request.isEmpty()) {
                response = "Please type something.\r\n";
            } else if ("bye".equals(request.toLowerCase())) {
                response = "Have a good day!\r\n";
                close = true;
            } else {
                try {
                    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                            .setVerificationKey(keys.get(ctx.channel()))
                            .build();
                    JwtClaims jwtClaims = jwtConsumer.processToClaims(request);
                    if (jwtClaims.getClaimsMap().containsKey("actor_id")) {
                        int actor_id = Integer.parseInt(String.valueOf(jwtClaims.getClaimsMap().get("actor_id")));
                        response = C3DatabaseManager.getActorInfo(actor_id) + "\r\n";
                    } else {
                        response = "Got signed value: \"" + jwtClaims.getClaimsMap().get("line") + "\"\r\n";
                    }
                } catch (InvalidJwtException e) {
                    logger.error("InvalidJwtException: ", e);
                } catch (NumberFormatException e) {
                    logger.error("NumberFormatException: ", e);
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
                keys.remove(ctx.channel());
                future.addListener(ChannelFutureListener.CLOSE);
            }
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
        keys.remove(ctx.channel());
        ctx.close();
    }

}
