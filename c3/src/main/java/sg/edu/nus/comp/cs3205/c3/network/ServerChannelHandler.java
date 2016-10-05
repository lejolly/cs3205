package sg.edu.nus.comp.cs3205.c3.network;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;

@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class.getSimpleName());

    private HashMap<Channel, Key> keys;

    ServerChannelHandler(HashMap<Channel, Key> keys) {
        this.keys = keys;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection: " + ctx.channel());
        Key key = MacProvider.generateKey();
        keys.put(ctx.channel(), key);
        ctx.write("key: " + Arrays.toString(key.getEncoded()) + "\r\n");
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
                    Jwt jwt = Jwts.parser().setSigningKey(keys.get(ctx.channel())).parseClaimsJws(request);
                    String body = jwt.getBody().toString();
                    response = "Got signed value: \"" + body.substring(5, body.length() - 1) + "\"\r\n";
                } catch (ExpiredJwtException e) {
                    logger.error("ExpiredJwtException: ", e);
                } catch (MalformedJwtException e) {
                    logger.error("MalformedJwtException: ", e);
                } catch (SignatureException e) {
                    logger.error("SignatureException: ", e);
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
