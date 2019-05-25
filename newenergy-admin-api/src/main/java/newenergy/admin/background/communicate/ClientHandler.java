package newenergy.admin.background.communicate;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class ClientHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        channelHandlerContext.channel().attr(AttributeKey.valueOf("Attribute_key")).set(o);
        channelHandlerContext.close();
    }
}
