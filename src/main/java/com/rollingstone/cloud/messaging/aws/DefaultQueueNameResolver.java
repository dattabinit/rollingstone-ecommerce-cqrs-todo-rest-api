package com.rollingstone.cloud.messaging.aws;

import com.rollingstone.command.interfaces.CommandQueueNameResolver;

public class DefaultQueueNameResolver implements CommandQueueNameResolver {

	@Override
	public String resolve(String commandType) {
		return commandType;
	}

}
