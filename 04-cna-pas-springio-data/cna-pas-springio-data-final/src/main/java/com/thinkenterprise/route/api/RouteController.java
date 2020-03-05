package com.thinkenterprise.route.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thinkenterprise.route.Route;
import com.thinkenterprise.route.RouteRepository;

@RestController
public class RouteController {
	
	@Autowired
	private RouteRepository routeRepository;
	
	@GetMapping("routes")
	public Iterable<Route> routes() {
		return routeRepository.findAll();
	} 

}
