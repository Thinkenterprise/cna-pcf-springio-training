package com.thinkenterprise.route;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RouteService {
private RestTemplate restTemplate;
	
	
	
	public String flightCount(Long id) {
		return restTemplate.getForObject("http://flightService/flights/count/{id}", String.class, id);
	}
	

	public Supplier<String> flightCountSuppplier(Long id) {
		return () -> this.flightCount(id);
	}
	
	
	 public String flightCountResilence(Long id) {
		return "No Flights";
		 
	 }
}
