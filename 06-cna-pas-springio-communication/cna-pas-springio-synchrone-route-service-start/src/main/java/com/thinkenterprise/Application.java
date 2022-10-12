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

package com.thinkenterprise;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
public class Application implements ApplicationRunner {
	
	private Log logger = LogFactory.getLog(Application.class); 
	
	@Value("${route.service.version}")
	private String version;
	
	@Value("${cf.instance.index}")
	private String index;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	public void run(ApplicationArguments args) throws Exception {
		logger.info("Start: Route Service with version " + version + "Instance: " + index);
	}
	
		
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components())
				.info(new Info().title("Aero API").version("1.0")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
	  
}
