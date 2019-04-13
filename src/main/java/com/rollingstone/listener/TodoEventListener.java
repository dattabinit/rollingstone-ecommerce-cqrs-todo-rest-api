package com.rollingstone.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.rollingstone.service.event.TodoServiceEvent;

@Component
public class TodoEventListener {

	private static final Logger log  = LoggerFactory.getLogger(TodoEventListener.class);

	@EventListener
	public void onApplicationEvent(TodoServiceEvent totoServiceEvent) {
		log.info("Recieved Sales Order Event :"+ totoServiceEvent.getEventType());
		log.info("Received Todo from Todo Event :" + totoServiceEvent.getEventTodo().toString());
		
	}

}
