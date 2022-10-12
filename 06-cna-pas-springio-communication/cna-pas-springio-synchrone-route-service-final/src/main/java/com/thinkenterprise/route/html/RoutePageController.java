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

package com.thinkenterprise.route.html;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.thinkenterprise.route.Route;
import com.thinkenterprise.route.RouteRepository;
import com.thinkenterprise.route.RouteService;

@Controller
public class RoutePageController {
	
	private Log logger = LogFactory.getLog(RoutePageController.class); 
	
	@Autowired
	private RouteRepository routeRepository;
	
	@Autowired
	private RouteService routeService;
	
	@GetMapping("/")
	public String routes(Model model) {
		
		Iterable<Route> routes = routeRepository.findAll();
		
		routes.forEach((route) -> logger.info(route.getId()));
			
		for (Route route : routes) {
			route.setFlights(routeService.flightCountResilence(route.getId()));
		}
		
		model.addAttribute("routes", routes);
		return "index";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") long id, Model model) {
		Route student = routeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid Route Id:" + id));
		routeRepository.delete(student);
		model.addAttribute("routes", routeRepository.findAll());
		return "index";
	}
	
	@PostMapping("/add")
	public String addStudent(@Valid Route route, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "add-route";
		}
		routeRepository.save(route);
		return "redirect:/";
	}
	
	@GetMapping("/add")
	public String showSignUpForm(Route student) {
		return "add-route";
	}
	
}
