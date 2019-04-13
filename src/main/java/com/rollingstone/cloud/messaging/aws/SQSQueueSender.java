package com.rollingstone.cloud.messaging.aws;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.rollingstone.command.GenericCommandResult;
import com.rollingstone.command.ResultCode;

@Component
public class SQSQueueSender {

	private final static Logger log = LoggerFactory.getLogger("SQSQueueSender");
	
	private final QueueMessagingTemplate queueMessagingTemplate;
	
	public SQSQueueSender(AmazonSQSAsync amazonSqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
	}
	
	
	
	public GenericCommandResult send(String queue, Object message) {
		log.info("The Queue name is : "+ queue);
		this.queueMessagingTemplate.convertAndSend(queue, message);
		Map<String, String> messages = new HashMap<String, String>();
		GenericCommandResult<String> cr = new GenericCommandResult<String>(ResultCode.OPERATION_SUCCESS, messages, "Success");
		cr.setAsSuccessful("Success", "The message was sent successfully" );
		return cr;
		
	}

}
