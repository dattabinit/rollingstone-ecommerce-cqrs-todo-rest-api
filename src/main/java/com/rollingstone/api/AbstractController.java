package com.rollingstone.api;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.rollingstone.exception.HTTP400Exception;
import com.rollingstone.exception.HTTP404Exception;
import com.rollingstone.exception.RestAPIExceptionInfo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public abstract class AbstractController implements ApplicationEventPublisherAware {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected ApplicationEventPublisher eventPublisher;
	
	protected static final String DEFAULT_PAGE_SIZE = "20";
	
	protected static final String DEFAULT_PAGE_NUMBER = "0";
	
	Counter http400ExceptionCounter = Metrics.counter("com.rollingstone.AccountController.HTTP400");
	
	Counter http404ExceptionCounter = Metrics.counter("com.rollingstone.AccountController.HTTP404");
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HTTP400Exception.class)
	public @ResponseBody RestAPIExceptionInfo handleBadRequestException(HTTP400Exception ex, WebRequest request, HttpServletResponse response) {
		log.info("Received Bad Request Exception Info" + ex.getLocalizedMessage());
		http400ExceptionCounter.increment();
		return new RestAPIExceptionInfo(ex.getLocalizedMessage(), "This Request did not have the correct paramaters");
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(HTTP400Exception.class)
	public @ResponseBody RestAPIExceptionInfo handleResourceNotFoundException(HTTP404Exception ex, WebRequest request, HttpServletResponse response) {
		log.info("Received Resource Not FoundException Info" + ex.getLocalizedMessage());
		http404ExceptionCounter.increment();
		return new RestAPIExceptionInfo(ex.getLocalizedMessage(), "This Requested Resource Was Not found");
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
	
	public static <T> T checkResourceFound(final T resource){
		if (resource ==  null) {
			throw new HTTP404Exception("Resource Not Found");
		}
		return resource;
	}

}
