package com.andin.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.andin.netty.app.NettyServer;

@SpringBootApplication
@ComponentScan("com.*")
@MapperScan("com.andin.server.dao")
public class SpringBootNettyDemoApplication implements CommandLineRunner{
	
    @Value("${server.port}")
    private Integer port;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootNettyDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//启动Netty
		NettyServer.start(port);
	}

}
