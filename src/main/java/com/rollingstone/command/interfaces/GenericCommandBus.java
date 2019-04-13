package com.rollingstone.command.interfaces;

import java.util.concurrent.Future;

import com.rollingstone.command.GenericCommandResult;

public interface GenericCommandBus {

	<T extends GenericCommand> Future<GenericCommandResult> send(T command);

}
