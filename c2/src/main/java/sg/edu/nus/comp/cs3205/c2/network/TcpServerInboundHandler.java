package sg.edu.nus.comp.cs3205.c2.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import sg.edu.nus.comp.cs3205.c2.data.JwtWrapper;
import sg.edu.nus.comp.cs3205.c2.data.Payload;

public class TcpServerInboundHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerInboundHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LOGGER.info("Recieved: " + msg);
        String parts[] = msg.split("\\s");
        Payload payload;

        LOGGER.info("" + parts.length);
        if (parts.length == 2) {
            switch (parts[0]) {
            case "jws":
                LOGGER.info("extracting payload from JWS token");
                payload = JwtWrapper.parseJws(parts[1]);
                LOGGER.info("payload action: " + payload.action);
                break;
            case "jwt":
                LOGGER.info("extracting payload from JWT");
                payload = JwtWrapper.parseJwt(parts[1]);
                LOGGER.info("payload action: " + payload.action);
                break;
            }
        }

        if (msg.toLowerCase().equals("bye")) {
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("exception occurred", cause);
        ctx.close();
    }
}
