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
	
	public DispatchingCommandBus(GenericCommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
	}

	@Override
	public <T extends GenericCommand> Future<GenericCommandResult> send(T command) {
	
		return this.commandDispatcher.dispatch(command);
	}
	
	
}
