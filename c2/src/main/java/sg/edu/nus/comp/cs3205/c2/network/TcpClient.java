package sg.edu.nus.comp.cs3205.c2.network;

import java.util.Scanner;

import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import sg.edu.nus.comp.cs3205.c2.data.JwtWrapper;
import sg.edu.nus.comp.cs3205.c2.data.Payload;

public class TcpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);
    private static final String HOST = "localhost";
    private static final String JWS = "jws";
    private static final String JWE = "jwe";
    private static final String JWT = "jwt";
    private static final String USAGE = "Usage: <" + JWS + "/" + JWE + "/" + JWT + "> <payload>";

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture = null;
    private Channel channel;

    public TcpClient(int port) {
        try {
            LOGGER.info("TCP client starting up");
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new TcpClientChannelInitializer());
            channel = b.connect(HOST, port).sync().channel();

            Scanner stdin = new Scanner(System.in);
            while (stdin.hasNextLine()) {
                String parts[] = stdin.nextLine().split("\\s", 2);
                String output = "NUL";
                
                if (parts[0].toLowerCase().equals("bye")) {
                    channel.writeAndFlush("bye").sync();
                    channel.closeFuture().sync();
                    break;
                } else if(parts.length != 2 || (!parts[0].equals(JWS) && !parts[0].equals(JWE) && !parts[0].equals("jwt"))) {
                    LOGGER.error(USAGE);
                    continue;
                } else {
                    switch(parts[0]) {
                    case JWS:
                        LOGGER.info("generating JWS token with fixed key pair");
                        output = JwtWrapper.getJws(new Payload().setAction(parts[1]));
                        break;
                    case JWE:
                        break;
                    case JWT:
                        LOGGER.info("generating JWT");
                        output = JwtWrapper.getJwt(new Payload().setAction(parts[1]));
                        break;
                    }
                }

                channelFuture = channel.writeAndFlush(parts[0] + " " + output + "\r\n");

                if (channelFuture != null) {
                    channelFuture.sync();
                }
            }
            stdin.close();

        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException when connecting client to " + HOST + ":" + port, e);
        } catch (JoseException e) {
            LOGGER.error("JoseException when creating JWT", e);
        } finally {
            LOGGER.info("TCP client shutting down");
            workerGroup.shutdownGracefully();
        }
    }
}
