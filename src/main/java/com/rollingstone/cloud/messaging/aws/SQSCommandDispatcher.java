package com.rollingstone.cloud.messaging.aws;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.rollingstone.command.GenericCommandResult;
import com.rollingstone.command.interfaces.CommandQueueNameResolver;
import com.rollingstone.command.interfaces.GenericCommand;
import com.rollingstone.command.interfaces.GenericCommandDispatcher;

@Component
public class SQSCommandDispatcher implements GenericCommandDispatcher {

	private final SQSQueueSender sqsQueueSender;
	
	
	private final CommandQueueNameResolver queueNameResolver;
	
	@Override
	public Future<GenericCommandResult> dispatch(GenericCommand command) {
		
		String type = command.getHeader().getCommandType();
		
		String queue;
		
		if (queueNameResolver == null || queueNameResolver.resolve(type) == null) {
			queue = new DefaultQueueNameResolver().resolve(type);
		}
		else {
			queue = queueNameResolver.resolve(type);
		}
		
		return new AsyncResult<>(this.sqsQueueSender.send(queue, command));
	}
	
	public SQSCommandDispatcher(SQSQueueSender sqsQueueSender, CommandQueueNameResolver queueNameResolver) {
		this.queueNameResolver = queueNameResolver;
		this.sqsQueueSender = sqsQueueSender;
	}
	
	

}
