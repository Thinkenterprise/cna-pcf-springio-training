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

package com.thinkenterprise.flight.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thinkenterprise.flight.Flight;
import com.thinkenterprise.flight.FlightRepository;
import com.thinkenterprise.health.RouteServiceMemoryHealthIndicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("flights")
@Tag(name = "flights")
public class FlightController {
	
	private Log logger = LogFactory.getLog(FlightController.class); 

	@Autowired
	FlightRepository flightRepository;
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Flights", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))),
			@ApiResponse(responseCode = "203", description = "No Flights"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Flight>> getAll() {
		return new ResponseEntity<Iterable<Flight>>(flightRepository.findAll(),HttpStatus.OK);
	}
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Flights", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Flight.class))),
			@ApiResponse(responseCode = "203", description = "No Flights"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(path = "count/{id}", method=RequestMethod.GET)
	public ResponseEntity<String> getCount(@Parameter(description = "route itentifier", required = true)  @PathVariable("id") Long id) {
		logger.info(id);
		return new ResponseEntity<String>( Long.toString(flightRepository.findByRouteId(id).size()),HttpStatus.OK);
	}

		
}
