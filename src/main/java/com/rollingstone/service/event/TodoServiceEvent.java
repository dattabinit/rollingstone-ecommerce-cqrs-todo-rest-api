package com.rollingstone.service.event;

import org.springframework.context.ApplicationEvent;

import com.rollingstone.domain.Todo;

public class TodoServiceEvent extends ApplicationEvent {

	Todo eventTodo;
	String eventType;
	
	public Todo getEventTodo() {
		return eventTodo;
	}
	public void setEventTodo(Todo eventTodo) {
		this.eventTodo = eventTodo;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public TodoServiceEvent(Object source, Todo eventTodo, String eventType) {
		super(source);
		this.eventTodo = eventTodo;
		this.eventType = eventType;
	}
	public TodoServiceEvent(Object source) {
		super(source);
	}
	@Override
	public String toString() {
		return "TodoServiceEvent [eventTodo=" + eventTodo + ", eventType=" + eventType + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventTodo == null) ? 0 : eventTodo.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodoServiceEvent other = (TodoServiceEvent) obj;
		if (eventTodo == null) {
			if (other.eventTodo != null)
				return false;
		} else if (!eventTodo.equals(other.eventTodo))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		return true;
	}
	
	
	

}
