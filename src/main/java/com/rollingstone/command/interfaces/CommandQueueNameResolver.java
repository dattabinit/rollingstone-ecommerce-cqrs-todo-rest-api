package com.rollingstone.command.interfaces;

public interface CommandQueueNameResolver {

	String resolve(String commandType);
}
