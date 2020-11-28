package com.manager.helper;

public class MessageManage {
	
	private String content;
	private String type;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public MessageManage(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public MessageManage() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "MessageManage [content=" + content + ", type=" + type + "]";
	}

	
	
}
