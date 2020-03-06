package com.thinkenterprise.route;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RouteService {
	
	
	private RestTemplate restTemplate;
	
	private CircuitBreaker circuitBreaker;
	
	@Autowired
	public RouteService(RestTemplate restTemplate, CircuitBreakerFactory  circuitBreakerFactory) {
		this.restTemplate=restTemplate;
		this.circuitBreaker=circuitBreakerFactory.create("routeService");   
	
	}
	
	public Long flightCount(Long id) {
		return restTemplate.getForObject("", Long.class, id);
	}
	

	public Supplier<Long> flightCountSuppplier(Long id) {
		return () -> this.flightCount(id);
	}
	
	
	 public Long flightCountResilence(Long id) {
		 return 0L;
		 
	 }
	 
}


