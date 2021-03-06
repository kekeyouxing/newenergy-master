package newenergy.admin.background.communicate;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import newenergy.admin.background.communicate.executor.MsgParsing;
import newenergy.admin.background.communicate.executor.MsgSolve;
import newenergy.admin.background.communicate.executor.ParsingResult;
import newenergy.admin.background.communicate.executor.SolveResult;
import newenergy.admin.background.service.StorageService;
import newenergy.admin.background.service.WaterService;
import newenergy.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class ServerHandle extends SimpleChannelInboundHandler<Object> {
    MsgParsing msgParsing;
    MsgSolve msgSolve;
    StorageService storageService;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    ServerHandle(){
        msgParsing = SpringUtil.getBean(MsgParsing.class);
        msgSolve = SpringUtil.getBean(MsgSolve.class);
        storageService = SpringUtil.getBean(StorageService.class);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        logger.info("服务器收到消息：" + o);

        ParsingResult parsingResult = msgParsing.parse((String)o);
        SolveResult solveResult = msgSolve.solve(parsingResult);
        if(solveResult != null)
            ctx.channel().writeAndFlush(solveResult.replyMsg());
        logger.info("服务器回复消息：" + solveResult.replyMsg());
        storageService.refundPostSolve(parsingResult.deviceNum());
        storageService.notifyPostSolve(parsingResult.deviceNum(),parsingResult.remainWater());

//        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有客户端连接");
    }
}
