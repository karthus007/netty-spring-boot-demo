package com.andin.netty.handler;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.EXPECTATION_FAILED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andin.netty.dispatcher.NettyDispatcher;
import com.andin.netty.factory.BuilderNettyRequestFactory;
import com.andin.netty.servlet.NettyRequest;
import com.andin.netty.servlet.NettyResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    //Netty所需的线程池，分别用于接收/监听请求以及处理请求读写
    private static DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(10);
    //POST请求工厂
    private final DefaultHttpDataFactory factory = new DefaultHttpDataFactory(false);
    //POST请求解码器
    private HttpPostRequestDecoder decoder = null;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest)msg;
        boolean isKeepAlive = HttpUtil.isKeepAlive(req);
		if (!prepare(ctx, req, isKeepAlive)) {
			return;
		}
		//获取客户端访问的IP地址
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		String ip = addr.getAddress().getHostAddress();
		logger.debug("request address is: " + ip);
		//产生一个HttpPostRequestDecoder
		decoder = new HttpPostRequestDecoder(factory, req);
		//打印URI的路径
		logger.debug("request uri is: " + req.uri());
		NettyRequest request = BuilderNettyRequestFactory.builderNettyRequest(req, decoder);
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					long start = Calendar.getInstance().getTimeInMillis();

					NettyResponse response = new NettyResponse();
					//打印请求参数
					logger.debug("request param info is: " + request.getRequestParamInfo());
					response = NettyDispatcher.dealWithRequest(request, response);
					//打印响应参数
					logger.debug("response param info is: " + response.getResponseParamInfo());
					
					// 写响应
					doWriteAndFlushByChunk(ctx, true, response);
					
					long end = Calendar.getInstance().getTimeInMillis();
					logger.debug("channelRead0 menthod executed is successfully, spends {} milliseconds.", end - start);
				} catch (Exception e) {
					logger.error("channelRead0 menthod executed is error: ", e);
				}
			}
		});
		
		if(decoder != null) {
			decoder.destroy();
		}
		
	}
	
    /**
     *  将流转成多个 Chunk，并发送到请求者
     * @param ctx
     * @param isKeepAlive
     * @param content
     */
    private void doWriteAndFlushByChunk(ChannelHandlerContext ctx, boolean async, NettyResponse response) {
        HttpResponse resp = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
        //添加响应头信息
        HttpHeaders respHeaders = resp.headers();
        for(String name : response.getHeaders().keySet()){
        	respHeaders.add(name, response.getHeaders().get(name));
        }
        // 写头.
        ctx.write(resp);
        // 写内容.
        InputStream contentStream = new ByteArrayInputStream(response.getByteResult());
        ChannelFuture writeFuture = ctx.write(new ChunkedStream(contentStream),ctx.newProgressivePromise ());
        writeFuture.addListener (new ChannelProgressiveFutureListener () {
            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
            	contentStream.close();
            }
        });

        //写最后一块，并且写I/O
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (async) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
	
	
    private boolean prepare(ChannelHandlerContext ctx, FullHttpRequest req, boolean isKeepAlive){
        if (!req.decoderResult().isSuccess()) {
            sendBadHttpResponse(ctx, isKeepAlive, new DefaultFullHttpResponse (HTTP_1_1, BAD_REQUEST));
            return false;
        }
        return true;
    }
    
    private void sendBadHttpResponse(ChannelHandlerContext ctx, boolean isKeepAlive, FullHttpResponse res) {
        ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
        res.content().writeBytes(buf);
        buf.release();
        HttpUtil.setContentLength(res, res.content().readableBytes());
        doWriteAndFlush(ctx, isKeepAlive, res);
    }

    private void doWriteAndFlush(ChannelHandlerContext ctx, boolean isKeepAlive, FullHttpResponse res){
        if(null != res){
            ChannelFuture f = ctx.channel().writeAndFlush(res);
            if (!isKeepAlive) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }else{
            sendBadHttpResponse(ctx, isKeepAlive, new DefaultFullHttpResponse (HTTP_1_1, EXPECTATION_FAILED));
        }
    }

}
