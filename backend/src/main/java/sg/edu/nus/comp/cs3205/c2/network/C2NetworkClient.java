package sg.edu.nus.comp.cs3205.c2.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.c2.keys.C2KeyManager;
import sg.edu.nus.comp.cs3205.common.data.json.LoginRequest;
import sg.edu.nus.comp.cs3205.common.data.json.LoginResponse;
import sg.edu.nus.comp.cs3205.common.utils.JwsUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class C2NetworkClient {

    private static Logger logger = LoggerFactory.getLogger(C2NetworkClient.class.getSimpleName());

    private C2ServerChannelHandler c2ServerChannelHandler;
    private ChannelFuture lastWriteFuture = null;
    private Channel ch;
    private String id = null;

    public C2NetworkClient(EventLoopGroup workerGroup, C2ServerChannelHandler c2ServerChannelHandler,
                           int c2ClientPort, String c2ClientHost) {
        this.c2ServerChannelHandler = c2ServerChannelHandler;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new C2ClientChannelInitializer(this));

            // Start the connection attempt.
            ch = b.connect(c2ClientHost, c2ClientPort).sync().channel();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

    public void handleInput(String line) throws Exception {
        try {
            // Sends the received line to the server.
            if (!line.isEmpty()) {
                Gson gson = new Gson();
                LoginRequest loginRequest = gson.fromJson(line, LoginRequest.class);
                String username = loginRequest.getData().get("username");
                String password = loginRequest.getData().get("password");
                System.out.println("username: " + username);
                System.out.println("password: " + password);
                if (username.equals("user") && password.equals("pass")) {
                    LoginResponse loginResponse = new LoginResponse();
                    Map<String, String> map = new LinkedHashMap<>();
                    String auth_token = String.valueOf(Math.abs(ThreadLocalRandom.current().nextLong()));
                    map.put("auth_token", auth_token);
                    map.put("csrf_token", "");
                    loginResponse.setData(map);
                    System.out.println("auth_token: " + auth_token);
                    c2ServerChannelHandler.forwardReply(gson.toJson(loginResponse, LoginResponse.class));
                    lastWriteFuture = ch.writeAndFlush(JwsUtils.getSignedFieldWithId(C2KeyManager.c2RsaPrivateKey, id,
                            "actor_id", String.valueOf(ThreadLocalRandom.current().nextInt(1, 200 + 1))) + "\r\n");
                } else {
                    throw new Exception();
                }

//                int actor_id = 0;
//                try {
//                    actor_id = Integer.parseInt(line);
//                } catch (NumberFormatException e) {
//                    logger.info("Not a number, NumberFormatException.");
//                }
//                lastWriteFuture = ch.writeAndFlush(JwsUtils.getSignedFieldWithId(
//                        C2KeyManager.c2RsaPrivateKey, id, "actor_id", String.valueOf(actor_id)) + "\r\n");
            } else {
//                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
            }
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        } catch (JsonSyntaxException e) {
            logger.error("Invalid input.");
        } catch (JoseException e) {
            logger.error("JoseException: ", e);
        }
    }

    public void handleMessageFromC3(JwtClaims jwtClaims) {
        if (id == null && jwtClaims.hasClaim("id")) {
            id = (String) jwtClaims.getClaimsMap().get("id");
        }
        if (jwtClaims.hasClaim("message")) {
            logger.info("Received from C3: \"" + jwtClaims.getClaimsMap().get("message") + "\"");
            c2ServerChannelHandler.forwardReply((String) jwtClaims.getClaimsMap().get("message"));
        } else if (jwtClaims.hasClaim("actor_info")) {
            c2ServerChannelHandler.forwardReply((String) jwtClaims.getClaimsMap().get("actor_info"));
        }
    }

    public void stopClient() throws JoseException {
        lastWriteFuture = ch.writeAndFlush(JwsUtils.getSimpleSignedMessageWithId(C2KeyManager.c2RsaPrivateKey, id, "bye") + "\r\n");
        try {
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException: ", e);
        }
    }

}
