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

	/*
	 * This is the final class that is actually sending the TodoCommand message to AWS SQS
	 */
	private final SQSQueueSender sqsQueueSender;
	
	/*
	 * This CommandQueueNameResolver interface is used to keep the QueueName loosely coupled with the java code
	 */
	private final CommandQueueNameResolver queueNameResolver;
	
	/*
	 * (non-Javadoc)
	 * @see com.rollingstone.command.interfaces.GenericCommandDispatcher#dispatch(com.rollingstone.command.interfaces.GenericCommand)
	 * 
	 * 1. The first class that impelments the CommandQueueNameResolver interface is the SQSCommandQueueNameResolver
	 * 2. The SQSCommandQueueNameResolver is annotated with @Component making it easy for Spring to find it
	 * 3. What this class will do is to receive the type of the message
	 * 4. The message is of type TodoCommand
	 * 5. TodoCommand has a GenericCommandHeader which contains a String named commandType.
	 * 6. This commandType within the GenericCommandHeader holds the name of Queue indirectly
	 * 7. The actual names of the Queues are held in a Map CommandQueues class's static member variable called commandQueueMap
	 * 8. The Map is a key valus pair and holds an AWS SQS Queue Name value against the key which is the Header Type coming out of GenericCommandHeader
	 * 9. So basically from the SQSCommandQueueNameResolver class we get the name of the AWS SQS Queue.
	 * 10. If at all we need to change the name of the AWS SQS Queue, the code change will only be at one place and that is how the message header destination / type is loosely coupled
	 * 11. Finally we use the SQSQueueSender class instance to actually send the message to AWS SQS.
	 * 12. Lets see how the SQSQueueSender works.
	 * 13. Before we go over there, however, we must notice that we are sending back an AsyncResult to the CommandBus which called us
	 */
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
