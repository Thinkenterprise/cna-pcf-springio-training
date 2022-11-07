package com.thinkenterprise;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.thinkenterprise.flight.api.FlightController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseClass {
	
	@Autowired FlightController fligthController;
	
	@BeforeEach public void setup() {
		RestAssuredMockMvc.standaloneSetup(fligthController);

	}

}
