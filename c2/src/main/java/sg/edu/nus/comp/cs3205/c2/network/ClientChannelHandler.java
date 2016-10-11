package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;

@Sharable
public class ClientChannelHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class.getSimpleName());

    private NetworkManager networkManager;

    ClientChannelHandler(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.startsWith("key: ")) {
            String stringArray = msg.substring(5);
            byte[] bytes = fromString(stringArray);
            networkManager.key = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA256");
            logger.info("Got key from server");
        } else {
            logger.info("Message: \"" + msg + "\"");
        }
    }

    private static byte[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        byte result[] = new byte[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(strings[i]);
        }
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception: ", cause);
        ctx.close();
    }

}
