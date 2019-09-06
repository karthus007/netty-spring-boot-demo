package com.andin.netty.app;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andin.netty.handler.NettyHttpServerHandler;
import com.andin.netty.utils.ConstantUtil;
import com.andin.netty.utils.PropertiesUtils;
import com.andin.netty.utils.ToolUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {
	
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
    private static final int TIMEOUT = 600;
    
    private static EventLoopGroup bossGroup;
    
    private static EventLoopGroup workerGroup;
	
    private static final String IS_USE_SSL = PropertiesUtils.getProperties(ConstantUtil.SSL_NETTY_STATUS, ConstantUtil.CONFIG_PROPERTIES);
    
    private static final String KEY_FILE_NAME = PropertiesUtils.getProperties(ConstantUtil.SSL_NETTY_KEY_NAME, ConstantUtil.CONFIG_PROPERTIES);
    
    private static final String KEY_FILE_PASSWORD = PropertiesUtils.getProperties(ConstantUtil.SSL_NETTY_KEY_PASSWORD, ConstantUtil.CONFIG_PROPERTIES);
    
    /**
     * netty启动入口
     * @param port
     */
	public static void start(Integer port){
		try {
			
			ServerBootstrap sb = new ServerBootstrap();
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup(4);
			
			sb.channel(NioServerSocketChannel.class)
			  .group(bossGroup, workerGroup);
			
			sb.childOption(ChannelOption.TCP_NODELAY, true)
			  .childOption(ChannelOption.SO_KEEPALIVE, true)	//是否保持连接
			  .option(ChannelOption.SO_REUSEADDR, true)
			  .option(ChannelOption.SO_BACKLOG, 100);
			
	        //证书生成命令：keytool -genkey -alias smcc -keysize 2048 -validity 365 -keyalg RSA -dname "CN=cn" -keypass 123456 -storepass 123456 -keystore server.jks
	        //方法一：
			final SslContext sslCtx;
			
			if(ConstantUtil.TRUE.equals(IS_USE_SSL)) {
				String filePath = ToolUtil.getJarRootPath() + File.separator + KEY_FILE_NAME;
				KeyStore keyStore = KeyStore.getInstance(ConstantUtil.SSL_NETTY_JKS); 
				keyStore.load(new FileInputStream(filePath), KEY_FILE_PASSWORD.toCharArray());
				KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(ConstantUtil.SSL_NETTY_SunX509);
				keyManagerFactory.init(keyStore,KEY_FILE_PASSWORD.toCharArray());
				sslCtx = SslContextBuilder.forServer(keyManagerFactory).clientAuth(ClientAuth.NONE).build();				
			} else {
				sslCtx = null;
			}
			
			//方法二：
	        //File certChainFile= new File("D:\\mykey\\key.crt");
	        //File keyFile= new File("D:\\mykey\\key.pem");
	        //SslContext sslCtx = SslContextBuilder.forServer(certChainFile, keyFile).clientAuth(ClientAuth.NONE).build();
	        

			sb.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
			        ChannelPipeline pipeline = ch.pipeline();
			        if(sslCtx != null) {
			        	pipeline.addLast("ssl", sslCtx.newHandler(ch.alloc()));
					}
			        pipeline.addLast("timeout", new ReadTimeoutHandler(TIMEOUT));
			        pipeline.addLast("codec", new HttpServerCodec());
			        pipeline.addLast("aggegator", new HttpObjectAggregator(20 * 1024 * 1024));
			        pipeline.addLast(new ChunkedWriteHandler());
			        pipeline.addLast("ServerInbound", new NettyHttpServerHandler());
			    }
			});
			
			Channel ch = sb.bind(port).sync().channel();    //自定义端口
			logger.info("**********netty start is successful on port: " + port + "**********");
			ch.closeFuture().sync();
		} catch (Exception e) {
			logger.error("netty start is error: ", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}
	
}
