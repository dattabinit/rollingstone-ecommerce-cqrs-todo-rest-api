package com.rollingstone.cloud.messaging.aws;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.rollingstone.command.GenericCommandResult;

@Component
public class SQSQueueSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
	
	public SQSQueueSender(AmazonSQSAsync amazonSqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSqs);
	}
	
	
	
	public GenericCommandResult send(String queue, Object message) {
		this.queueMessagingTemplate.convertAndSend(queue, message);
		GenericCommandResult<String> cr = new GenericCommandResult<String>();
		cr.setAsSuccessful((String) message, "Success");
		return cr;
		
	}

}
