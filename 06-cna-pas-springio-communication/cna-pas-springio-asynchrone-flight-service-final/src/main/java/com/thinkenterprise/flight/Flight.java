package com.thinkenterprise.flight;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Flight {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private double price;
	private LocalDate start;
	private Long routeId;
	

	public Flight() {
		super();
	}
	
	public Flight(double price, LocalDate start, Long routeId) {
		super();
		this.price = price;
		this.start = start;
		this.routeId = routeId;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public LocalDate getDate() {
		return start;
	}
	
	public void setDate(LocalDate start) {
		this.start = start;
	}
	

}
