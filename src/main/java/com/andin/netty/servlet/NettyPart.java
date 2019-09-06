package com.andin.netty.servlet;

import java.io.IOException;

public class NettyPart{

	private String name;
	
	private String filename;
	
    private byte[] content;
	
    public NettyPart(String name,  String filename,  byte[] content) {
        this.name = name;
        this.filename = filename;
        this.content = content;

    }
    
	public byte[] getByteFile() throws IOException {
		return this.content;
	}

	public String getName() {
		return this.name;
	}

	public String getSubmittedFileName() {
		return this.filename;
	}
	
	public long getSize() {
		return this.content.length;
	}

}
