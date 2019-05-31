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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class ServerHandle extends SimpleChannelInboundHandler<Object> {
    MsgParsing msgParsing;
    MsgSolve msgSolve;
    StorageService storageService;
    ServerHandle(){
        msgParsing = SpringUtil.getBean(MsgParsing.class);
        msgSolve = SpringUtil.getBean(MsgSolve.class);
        storageService = SpringUtil.getBean(StorageService.class);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        System.out.println("server receive message :" + o);

        ParsingResult parsingResult = msgParsing.parse((String)o);
        SolveResult solveResult = msgSolve.solve(parsingResult);
        if(solveResult != null)
            ctx.channel().writeAndFlush("server send message " + solveResult.replyMsg());
        storageService.refundPostSolve(parsingResult.deviceNum());
        storageService.notifyPostSolve(parsingResult.deviceNum(),parsingResult.remainWater());

//        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive>>>>>>>>");
    }
}
