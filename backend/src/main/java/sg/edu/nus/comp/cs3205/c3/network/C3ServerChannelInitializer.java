package sg.edu.nus.comp.cs3205.c3.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import sg.edu.nus.comp.cs3205.c3.auth.C3LoginManager;
import sg.edu.nus.comp.cs3205.c3.database.C3DatabaseManager;
import sg.edu.nus.comp.cs3205.c3.session.C3SessionManager;

public class C3ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private C3SessionManager c3SessionManager;
    private C3LoginManager c3LoginManager;
    private C3DatabaseManager c3DatabaseManager;

    C3ServerChannelInitializer(C3SessionManager c3SessionManager, C3LoginManager c3LoginManager,
                               C3DatabaseManager c3DatabaseManager) {
        this.c3SessionManager = c3SessionManager;
        this.c3LoginManager = c3LoginManager;
        this.c3DatabaseManager = c3DatabaseManager;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(new C3ServerChannelHandler(c3SessionManager, c3LoginManager, c3DatabaseManager));
    }

}
