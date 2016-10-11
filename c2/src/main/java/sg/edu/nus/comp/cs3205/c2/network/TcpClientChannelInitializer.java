package sg.edu.nus.comp.cs3205.c2.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TcpClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    protected static final StringEncoder ENCODER = new StringEncoder();
    protected static final StringDecoder DECODER = new StringDecoder();
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        // decoders
        pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("stringDecoder", DECODER);
        
        // encoders
        pipeline.addLast("stringEncoder", ENCODER);
        
        // handlers
        pipeline.addLast("inboundHandler", new TcpClientInboundHandler());
    }

}
