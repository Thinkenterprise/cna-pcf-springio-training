/*
 * Copyright (C) 2019 Thinkenterprise
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

package com.thinkenterprise;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class Application implements ApplicationRunner {

	private Log logger = LogFactory.getLog(Application.class);

	@Value("${route.service.version}")
	private String version;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public void run(ApplicationArguments args) throws Exception {
		logger.info("Start: AeroGateway with version " + version);

	}
	
	
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/aero/**")
						.filters(f -> f.rewritePath("/aero/(?<segment>.*)", "/${segment}")
								.circuitBreaker(c -> c.setName("resilience4j").setFallbackUri("forward:/fallback")))
						.uri("lb://routeService").id("routeService"))
				.build();
	}

	@RequestMapping("fallback")
	public String fallback() {
		return "The application is temporarily unavailable";

	}

}
