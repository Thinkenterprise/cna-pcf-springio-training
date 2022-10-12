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

package com.thinkenterprise.route.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thinkenterprise.route.Route;
import com.thinkenterprise.route.RouteRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("routes")
@Tag(name = "routes")
public class RouteController {


	@Autowired
	RouteRepository routeRepository;
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Routes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
			@ApiResponse(responseCode = "203", description = "No Routes"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Route>> getAll() {
		return new ResponseEntity<Iterable<Route>>(routeRepository.findAll(),HttpStatus.OK);
	}
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Routes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
			@ApiResponse(responseCode = "203", description = "No Routes"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "route itentifier", required = true)  @PathVariable(value = "id") Long id) {
		routeRepository.deleteById(id);
	}
			
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Routes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
			@ApiResponse(responseCode = "203", description = "No Routes"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Route> get(@Parameter(description = "route itentifier", required = true) @PathVariable(value = "id") Long id) {
		 return new ResponseEntity<Route>(routeRepository.findById(id).get(),HttpStatus.OK);
		 
	 }
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Routes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
			@ApiResponse(responseCode = "203", description = "No Routes"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(method = RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@Parameter(description = "route itentifier", required = true) @Validated @RequestBody Route entity) {	
	    routeRepository.save(entity);
	}	
	
	@Operation(summary = "Get all Routes", responses = {
			@ApiResponse(responseCode = "200", description = "Routes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
			@ApiResponse(responseCode = "203", description = "No Routes"),
			@ApiResponse(responseCode = "400", description = "Error")})
	@RequestMapping(method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Route> post(@Parameter(description = "route itentifier", required = true) @Validated @RequestBody Route entity) {	
	    return new ResponseEntity<Route>(routeRepository. save(entity),HttpStatus.CREATED);
	}	
		
}
