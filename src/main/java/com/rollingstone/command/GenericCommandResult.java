package com.rollingstone.command;

import java.util.Map;

import org.springframework.util.StringUtils;

public class GenericCommandResult<T> {

	private ResultCode code;
	private Map<String, String> messages;
	private T result;
	
	public GenericCommandResult(ResultCode code, Map<String, String> messages, T result) {
		super();
		this.code = code;
		this.messages = messages;
		this.result = result;
	}

	public GenericCommandResult(ResultCode code) {
		super();
		this.code = code;
	}

	public GenericCommandResult(Map<String, String> messages) {
		this();
		this.messages = messages;
	}

	public GenericCommandResult(ResultCode code, Map<String, String> messages) {
		super();
		this.code = code;
		this.messages = messages;
	}

	public GenericCommandResult() {
		super();
	}

	public ResultCode getCode() {
		return code;
	}

	public void setCode(ResultCode code) {
		this.code = code;
	}

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
	public String getConsolidatedMessage() {
		return String.join(",", messages.values());
	}
	
	public void setAsFailed(Iterable<Map.Entry<String, String>> messages) {
		setFailure();
		for(Map.Entry<String, String> message : messages) {
			addMessage(message.getKey(),message.getValue());
		}
	}
	
	public void setAsSuccessful(String message, T result) {
		this.result = result;
		
		if (result == null && StringUtils.isEmpty(message)) {
			addMessage("","Item Not Found");
		}
		
		if (!StringUtils.isEmpty(message)) {
			addMessage("", message);
		}
	}
	
	public boolean isFailure() {
		return code == ResultCode.OPERATION_FAILURE;
	}
	protected void setFailure() {
		code = ResultCode.OPERATION_FAILURE;
	}
	
	protected void addMessage(String key, String message) {
		if (StringUtils.isEmpty(message)) {
			return;
		}
		if (!messages.containsKey(key)) {
			messages.put(key, message);
		}
	}
	
}
