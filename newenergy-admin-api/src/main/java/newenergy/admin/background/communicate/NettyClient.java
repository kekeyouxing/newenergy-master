package newenergy.admin.background.communicate;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class NettyClient {
    private static Logger log = LoggerFactory.getLogger(NettyClient.class);
    private static Bootstrap b;
    private static ChannelFuture f;
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static void init () {
        try {
//            log.info("init...");
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    // 解码编码
                    socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object startAndWrite(InetSocketAddress address, Object send) throws InterruptedException {
        init();
        f = b.connect(address).sync();
        // 传数据给服务端
        f.channel().writeAndFlush(send);
        f.channel().closeFuture().sync();
        return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress("101.37.67.23", 7000);
        String message = "HGDR123400000ZB300000ZF8C000FINL";
        for(int i = 0; i < 10000; i++) {
            try {
                Object result = NettyClient.startAndWrite(address, message);
//                log.info("....result:" + result);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
//                f.channel().close();
            }
        }

        workerGroup.shutdownGracefully();
        log.info("Closed client!");

        /**
         * 6w连接测试
         */
//        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
//        Bootstrap b = new Bootstrap();
//        b.group(workerGroup);
//        b.channel(NioSocketChannel.class);
//        b.handler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel socketChannel) throws Exception {
//                ChannelPipeline pipeline = socketChannel.pipeline();
//            }
//        });
//
//        for(int k = 0; k < 30000; k++){
//            ChannelFuture f = b.connect("101.37.67.23",7000);
////            f.channel().writeAndFlush("HGDR123400000ZB300000ZF8C000FINL");
//        }

    }
}
