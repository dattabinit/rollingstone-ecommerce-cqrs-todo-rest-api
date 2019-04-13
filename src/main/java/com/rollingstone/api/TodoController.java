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
	 * There are two Microservices for this application, working together.
	 * 
	 * 1. The first Microservice is called rollingstone-ecommerce-cqrs-todo-rest-api
	 * 2. The Java Class that represents the public facing API is called TodoControll.java which is the current class
	 * 3. There are many APIs in this class but we will focus on the createTodo Method for the POST HTTP Method for now
	 * 4. The First thing to understand is how the TodoController Constructor works
	 * 5. The Constructor takes two parameters
	 * 		A. An Instance of the GenericCommandBus Interface
	 * 		B. An Instance of the TodoService class
	 * 6. We cannot make instances form Java Interfaces
	 * 7. When we specify an interface in a constructor / method what we are telling java is that we will send an instance of a class that implements this interfaces
	 * 8. So instead  of GenericCommandBus, what will come to the TodoController class's Constructor will be an instance of a class that implements the GenericCommandBus interface
	 * 9. Lets us find out which class in which package implements the GenericCommandBus
	 * 10. Looks like com.rollingstone.dispatcher.DispatchingCommandBus class implements GenericCommandBus
	 * 11. Here is the definition
	 * 12. @Component
			public class DispatchingCommandBus implements GenericCommandBus
	 * 13. At runtime, Spring will be able to locate this DispatchingCommandBus as it implements GenericCommandBus and as it is annotated with @Component annotation
	 * Spring will be able to create an instance and feed the Constructor easily.
	 * 14. The second parameter which is the TodoService is easy. It is annotated with the @Service annotation making it easy for Spring to load it.
	
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
	
	/*
	 * 1. Now let us understand how the createTodo API method works
	 * 2. It receives the HTTP POST request which includes a HTTP RequestBody and converts that json object into a Todo Java pojo.
	 * 3. The Todo java pojo is annotated with Validation annotations
	 * 4. So we can easily use Java Validator API to determine if the request object is valid i.e. has all the mandetory data in it or not
	 * 5. If the validation fails, we will send a 200 response with error message
	 * 6. However, if the validation succeeds , we will create a Command message using the TodoCommand java class
	 * 7. The TodoCommand is a message carrier and has
	 * 		A. An id like our FedEx shipment has an unique tracking ID
	 * 		B. The TodoCommand also has a Header like our FedEx package has an envelope containing the to and from address etc
	 * 		C. Finally the TodoCommand has a payload like FedEx package has the content / letter of the package inside the envelope
	 * 8. Once we create the TodoCommand and set an unique ID as well as the Payload i.e. Todo Object we received as the request payload, we create the GenericCommandHeader
	 * 9. The GenericCommandHeader contains our destination address i.e. AWS SQS Queue name like our FedEx package has the receiver's address
	 * 10. The GenericCommandHeader also has the timestamp like FedEx shipment has the timestamp to tell when we shipped it.
	 * 11. Finally the GenericCommandHeader has a schema version to help determine what version of the object we are sending. This is for advanced usage and we are not using it now.
	 * 12. After we create the GenericCommandHeader, we simply set the GenericCommandHeader instance to our TodoCommand
	 * 
	 * 13. Immediately after, we use the commandBus to send the message which is the TodoCommand instance
	 * 14. The commandBus is an instance of class DispatchingCommandBus
	 * 15. So immediately after we have understood the TodoController.createTodo method, we need to go look at the DispatchingCommandBus class
	 * 16. So the flow is like this
	 * 
	 * 
	 * 
	 * 
	 * 17. TodoController calls DispatchingCommandBus
	 * 18. The DispatchingCommandBus uses SQSCommandDispatcher
	 * 19. The SQSCommandDispatcher uses the SQSCommandQueueNameResolver to determine the actual AWS SQS Queue Name from the Command Header Type
	 * 20. The SQSCommandDispatcher uses the SQSQueueSender class to send message to AWs SQS
	 * 21. The SQSQueueSender uses Springframework's AWS Librari's QueueMessagingTemplate class to finally send the message to AWS SQS Queue
	 */
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
