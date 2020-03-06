/*
 * Copyright (C) 2020 Thinkenterprise
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author Michael Schaefer
 */
package com.thinkenterprise.flight.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thinkenterprise.flight.FlightRepository;



public class RouteChangedMessageConsumer {
	
	protected static Logger logger = LoggerFactory.getLogger(RouteChangedMessageConsumer.class);
	
	@Autowired
	private FlightRepository flightRepository;
	
	
	public void input(EntityChangedCommand entityChangedCommand) {
		
		if(entityChangedCommand.getType().equals("Route")&&entityChangedCommand.getCommand().equals("Save")) {
			// Handle Save ...
			logger.info("Recieved Save Command EntityCangedCommand for Route Entity with ID " + entityChangedCommand.getId() );
			
			
		}
		
		if(entityChangedCommand.getType().equals("Route")&&entityChangedCommand.getCommand().equals("Delete")) {
			// Handle Delete ...
			logger.info("Recieved Delete Command EntityCangedCommand for Route Entity with ID " + entityChangedCommand.getId() );
		}
		
	}
	
	

}
