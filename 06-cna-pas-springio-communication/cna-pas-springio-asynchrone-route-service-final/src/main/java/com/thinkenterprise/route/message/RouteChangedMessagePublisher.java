package com.thinkenterprise.route.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;

@EnableBinding(Source.class)
public class RouteChangedMessagePublisher {
	
	protected final static Logger logger = LoggerFactory.getLogger(RouteChangedMessagePublisher.class);

	@Autowired
	private Source source;

	public void delete(Long id) {
		source.output().send(MessageBuilder.withPayload(new EntityChangedCommand(id, "Route", "Delete")).build());
		logger.info("Publish EntityChangedCommand Delete Route with ID " + id);
	}

	public void save(Long id) {
		source.output().send(MessageBuilder.withPayload(new EntityChangedCommand(id, "Route", "Create")).build());
		logger.info("Publish EntityChangedCommand Save Route with ID " + id);
	}

}
