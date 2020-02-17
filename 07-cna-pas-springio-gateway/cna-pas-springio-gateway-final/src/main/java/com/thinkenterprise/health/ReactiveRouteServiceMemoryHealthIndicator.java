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

package com.thinkenterprise.health;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ReactiveRouteServiceMemoryHealthIndicator implements ReactiveHealthIndicator  {
	
	private Log logger = LogFactory.getLog(ReactiveRouteServiceMemoryHealthIndicator.class); 
		
	@Value("${vcap.application.limits.mem}")
	private Long limitsMem;

	public Mono<Health> health() {
		
		logger.info(limitsMem);

		if (limitsMem >= 512)
			return Mono.just(Health.up().build());
		else
			return Mono.just(Health.down().build());

	}

}
