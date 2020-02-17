package com.thinkenterprise.flight.message;


public class EntityChangedCommand {
	
	private Long id;
	private String type;
	private String command;
	
	public EntityChangedCommand(Long id, String type, String command) {
		super();
		this.id = id;
		this.type = type;
		this.command = command;
	}
	public EntityChangedCommand() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

}
