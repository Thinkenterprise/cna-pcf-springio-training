package com.thinkenterprise.flight.message;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.thinkenterprise.flight.Flight;
import com.thinkenterprise.flight.FlightRepository;


@EnableBinding(Sink.class)
public class RouteChangedMessageConsumer {
	
	protected static Logger logger = LoggerFactory.getLogger(RouteChangedMessageConsumer.class);
	
	@Autowired
	private FlightRepository flightRepository;
	
	
	@StreamListener(target = Sink.INPUT)
	public void input(EntityChangedCommand entityChangedCommand) {
		
		if(entityChangedCommand.getType().equals("Route")&&entityChangedCommand.getCommand().equals("Save")) {
			// Handle Save ...
			logger.info("Recieved Comman EntityCangedCommand for Route Entity with ID " + entityChangedCommand.getId() );
			flightRepository.save(new Flight(0L,LocalDate.now(),entityChangedCommand.getId()));
			
		}
		
		if(entityChangedCommand.getType().equals("Route")&&entityChangedCommand.getCommand().equals("Delete")) {
			// Handle Delete ...
			logger.info("Recieved Comman EntityCangedCommand for Route Entity with ID " + entityChangedCommand.getId() );
		}
		
	}
	
	

}
