package com.rollingstone.api;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rollingstone.command.GenericCommandHeader;
import com.rollingstone.command.GenericCommandType;
import com.rollingstone.command.TodoCommand;
import com.rollingstone.command.interfaces.GenericCommandBus;
import com.rollingstone.domain.RSResponse;
import com.rollingstone.domain.Todo;
import com.rollingstone.service.TodoService;
import com.rollingstone.service.event.TodoServiceEvent;

@RestController
public class TodoController extends AbstractController {

	private final static Logger log = LoggerFactory.getLogger("TodoController");
	private final static String SCHEMA_VERSION = "1.0";
	
	private final GenericCommandBus commandBus;
	private TodoService todoService;
	private static Validator validator;
	
	/* 
	 * The TodoController constructor takes two paramaters namely the GenericCommandBus and the TodoService.
	 * The TodoService is a Java Class already annotated with
	 * @Service
		public class TodoService 
	 * So Spring Boot will have no problem finding the TodoService during application start, 
	 * create an install and assign it to the TodoController.
	 * However, the first parameter which is an interface is a little tricky.
	 * This class com.rollingstone.dispatcher.DispatchingCommandBus actually implements the GenericCommandBus
	 * As per the Java Rules of the language, then DispatchingCommandBus is a GenericCommandBus.
	 * The class is also annotated with
	 * @Component
	 *	public class DispatchingCommandBus implements GenericCommandBus
	 *  So again following java rules, Spring Will have no problem to find an implementation of the interface GenericCommandBus
	 *  create an instance of DispatchingCommandBus and assign that to the TodoController Constructor as the first Parameter
	 */
	
	public TodoController(GenericCommandBus commandBus, TodoService todoService) {
		this.commandBus = commandBus;
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		this.todoService = todoService;
	}
	
	@GetMapping("rsecommerce/cqrs/todo/api")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Todo> getTodos(){
		return todoService.getAllTodos();
	}
	
	@GetMapping("rsecommerce/cqrs/todo/api/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Todo getTodo(@PathVariable("id") long id) {
		return todoService.getTodo(id);
	}
	
	@PostMapping("rsecommerce/cqrs/todo/api")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public RSResponse createTodo(@RequestBody Todo todo) {
		
		RSResponse<Todo> rsResponse = new RSResponse<Todo>();
		
		log.info("Received Request to create Todo");
		
		Set<ConstraintViolation<Todo>> constraintViolations = validator.validate(todo);
		
		String errorMessage = buildErrorMessage(constraintViolations);
		
		if (!errorMessage.isEmpty()) {
			log.error("Error When Creating Todo :"+ errorMessage);
			rsResponse.setErrorMEssage("Error When Creating Todo :"+ errorMessage);
			return rsResponse;
		}
		else {
			TodoCommand todoCommand = new TodoCommand();
			todoCommand.setTodo(todo);
			todoCommand.setId(UUID.randomUUID());
			
			GenericCommandHeader header = new GenericCommandHeader(GenericCommandType.CREATE_TODO.toString(), SCHEMA_VERSION, new Timestamp(System.currentTimeMillis()));
			
			todoCommand.setHeader(header);
			
			commandBus.send(todoCommand);
			eventPublisher.publishEvent(new TodoServiceEvent(this, todo, "TodoCreated"));
			rsResponse.setMessage("Todo Sent to AWS for creation");
			rsResponse.setPayload(todo);
			return rsResponse;
			
		}
		
		
	}
	
	@PutMapping("rsecommerce/cqrs/todo/api/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public RSResponse updateTodo(@RequestBody Todo todo) {
		
		RSResponse<Todo> rsResponse = new RSResponse<Todo>();
		
		log.info("Received Request to update Todo");
		
		Set<ConstraintViolation<Todo>> constraintViolations = validator.validate(todo);
		
		String errorMessage = buildErrorMessage(constraintViolations);
		
		if (!errorMessage.isEmpty()) {
			log.error("Error When Updating Todo :"+ errorMessage);
			rsResponse.setErrorMEssage("Error When Updating Todo :"+ errorMessage);
			return rsResponse;
		}
		else {
			TodoCommand todoCommand = new TodoCommand();
			todoCommand.setTodo(todo);
			todoCommand.setId(UUID.randomUUID());
			
			GenericCommandHeader header = new GenericCommandHeader(GenericCommandType.UPDATE_TODO.toString(), SCHEMA_VERSION, new Timestamp(System.currentTimeMillis()));
			
			todoCommand.setHeader(header);
			
			commandBus.send(todoCommand);
		
			rsResponse.setMessage("Todo Sent to AWS for update");
			rsResponse.setPayload(todo);
			return rsResponse;
			
		}
		
		
	}
	
	@DeleteMapping("rsecommerce/cqrs/todo/api/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	public RSResponse deleteTodo(@PathVariable("id") Long id) {
		
		RSResponse<Todo> rsResponse = new RSResponse<Todo>();
		
		log.info("Received Request to delete Todo");
		
		Todo todo = todoService.getTodo(id);
		
		Set<ConstraintViolation<Todo>> constraintViolations = validator.validate(todo);
		
		String errorMessage = buildErrorMessage(constraintViolations);
		
		if (todo == null) {
			log.error("Error When Deleting Todo :"+ errorMessage);
			rsResponse.setErrorMEssage("Error When Deleting Todo :"+ errorMessage);
			return rsResponse;
		}
		else {
			TodoCommand todoCommand = new TodoCommand();
			todoCommand.setTodo(todo);
			todoCommand.setId(UUID.randomUUID());
			
			GenericCommandHeader header = new GenericCommandHeader(GenericCommandType.UPDATE_TODO.toString(), SCHEMA_VERSION, new Timestamp(System.currentTimeMillis()));
			
			todoCommand.setHeader(header);
			
			commandBus.send(todoCommand);
		
			rsResponse.setMessage("Todo Sent to AWS for Deletion");
			rsResponse.setPayload(todo);
			return rsResponse;
			
		}
		
		
	}
	
	private String buildErrorMessage(Set<ConstraintViolation<Todo>> constraintViolations) {
		String message  = "";
		
		if (constraintViolations == null || constraintViolations.size() == 0) {
			return message;
		}else {
			for (ConstraintViolation constraintViolation : constraintViolations) {
				message += constraintViolation.getMessage() + " ";
			}
		}
		
		return message;
	}
	
}
