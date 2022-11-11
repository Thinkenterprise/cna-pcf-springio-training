package com.thinkenterprise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import com.thinkenterprise.route.RouteService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

@AutoConfigureStubRunner(
		ids = "com.thinkenterprise:cna-pas-springio-synchron-flight-service-final:0.0.1-SNAPSHOT:stubs:8100",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class TestRouteService {
		
	@Test
	public void testflightCountResilence(@Autowired RouteService routeService) {	
		String count = routeService.flightCount(101L);
		Assertions.assertTrue(count.equals("1"));
	}
	
}