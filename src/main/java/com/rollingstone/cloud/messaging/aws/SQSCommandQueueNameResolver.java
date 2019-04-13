package com.rollingstone.cloud.messaging.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rollingstone.command.GenericCommandType;
import com.rollingstone.command.interfaces.CommandQueueNameResolver;

@Component
public class SQSCommandQueueNameResolver implements CommandQueueNameResolver {

	@Value("${queue.debug}")
	boolean debug;
	
	@Override
	public String resolve(String commandType) {
		if (this.debug) {
			return CommandQueues.commandQueueMap.get(GenericCommandType.DEBUG_QUEUE.toString());
		}
		return CommandQueues.commandQueueMap.get(commandType);
	}

}
