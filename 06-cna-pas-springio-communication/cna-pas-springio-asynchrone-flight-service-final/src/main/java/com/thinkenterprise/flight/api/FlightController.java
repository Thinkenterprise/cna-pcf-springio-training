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
import org.springframework.util.RouteMatcher.Route;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thinkenterprise.flight.Flight;
import com.thinkenterprise.flight.FlightRepository;
import com.thinkenterprise.health.RouteServiceMemoryHealthIndicator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("flights")
@Api("flights")
public class FlightController {
	
	private Log logger = LogFactory.getLog(RouteServiceMemoryHealthIndicator.class); 

	@Autowired
	FlightRepository flightRepository;
	
	@ApiOperation(value="Get all Flights", notes= "Read the list of flights from the repository", nickname="getAll", response=Route.class, responseContainer="List", code=200, tags={"Flight"}, produces="application/json")
	@ApiResponses(value = {@ApiResponse(code=204, message="empty list"), @ApiResponse(code=400, message="can't access flights ",response=Error.class)})
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Flight>> getAll() {
		return new ResponseEntity<Iterable<Flight>>(flightRepository.findAll(),HttpStatus.OK);
	}
	
	@ApiOperation(value="Get Flight Count", notes= "Get Count of Flights", nickname="getAll", response=Route.class, responseContainer="List", code=200, tags={"Flight"})
	@ApiResponses(value = {@ApiResponse(code=204, message="empty list"), @ApiResponse(code=400, message="can't access flights ",response=Error.class)})
	@RequestMapping(path = "count/{id}", method=RequestMethod.GET)
	public ResponseEntity<Long> getCount(@ApiParam(name="id", value="route identifier", required=true, type="Long") @PathVariable("id") Long id) {
		logger.info(id);
		return new ResponseEntity<Long>(new Long(flightRepository.findByRouteId(id).size()),HttpStatus.OK);
	}
		
}
