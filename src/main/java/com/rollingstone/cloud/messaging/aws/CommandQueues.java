package com.rollingstone.cloud.messaging.aws;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rollingstone.command.GenericCommandType;

public class CommandQueues {

	private static final String categoryQueue = "Category_Command_Queue";
	
	private static final String todoQueue = "TODO_New_Queue";
	
	public static Map<String, String> commandQueueMap;
	
	static {
		commandQueueMap = new LinkedHashMap<String, String>();
		
		commandQueueMap.put(GenericCommandType.CREATE_CATEGORY.toString(), categoryQueue);
		commandQueueMap.put(GenericCommandType.UPDATE_CATEGORY.toString(), categoryQueue);
		
		commandQueueMap.put(GenericCommandType.CREATE_TODO.toString(), categoryQueue);
		commandQueueMap.put(GenericCommandType.UPDATE_TODO.toString(), categoryQueue);

	}

}
