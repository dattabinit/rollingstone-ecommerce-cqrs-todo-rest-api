package com.rollingstone.dispatcher;

import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.rollingstone.command.GenericCommandResult;
import com.rollingstone.command.interfaces.GenericCommand;
import com.rollingstone.command.interfaces.GenericCommandBus;
import com.rollingstone.command.interfaces.GenericCommandDispatcher;

@Component
public class DispatchingCommandBus implements GenericCommandBus {

	private final GenericCommandDispatcher commandDispatcher;
	
	/*
	 * Now lets see how the constructor of the class DispatchingCommandBus will be created by Spring Boot
	 * This class DispatchingCommandBus depends on GenericCommandDispatcher which is an interface
	 * public interface GenericCommandDispatcher {

			Future<GenericCommandResult> dispatch(GenericCommand command);
		}
	 * So Spring Boot will need find an implementation of that interface GenericCommandDispatcher
	 * Lets see which java class implements this GenericCommandDispatcher interface.
	 * The SQSCommandDispatcher class implements the GenericCommandDispatcher interface.
	 * @Component
		public class SQSCommandDispatcher implements GenericCommandDispatcher 
	 * It is annotated with the @Component annotation and Spring will find no problem finding it, making an instance and assinging it to the 
	 * DispatchingCommandBus class
	 */
	public DispatchingCommandBus(GenericCommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}

	@Override
	public <T extends GenericCommand> Future<GenericCommandResult> send(T command) {
	
		/*
		 * So the next class we need to understand is the SQSCommandDispatcher.java
		 */
		return this.commandDispatcher.dispatch(command);
	}
	
	
}
