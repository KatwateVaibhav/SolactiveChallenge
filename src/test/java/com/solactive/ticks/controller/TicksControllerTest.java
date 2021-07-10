package com.solactive.ticks.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.solactive.ticks.entity.Statistic;
import com.solactive.ticks.entity.Tick;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
class TicksControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	private String url;
	
	@Before
    public void setUp() {
        url = "http://localhost:" + port;
    }

	@Test
	void gettGlobalStats() {
		ResponseEntity<Statistic> response = restTemplate.getForEntity("/statistics", Statistic.class);
		assertThat(response.getStatusCodeValue(), equalTo(200));
	}

	@Test
	void getWrongInstrumentStats() {
		ResponseEntity<Statistic> response = restTemplate.getForEntity("/statistics/!!@#!!", Statistic.class);
		assertThat(response.getStatusCodeValue(), equalTo(404));
	}

	@Test
	void addTicks() {
		Tick tick = new Tick();
		tick.setInstrument("IBM.N");
		tick.setPrice(123.2);
		tick.setTimestamp(System.currentTimeMillis());

		ResponseEntity<Void> response = restTemplate.postForEntity("/ticks", tick, Void.class);
		assertThat(response.getStatusCodeValue(), equalTo(201));
	}

	@Test
	void addExpiredTick() {
		Tick tick = new Tick();
		tick.setInstrument("IBM.N");
		tick.setPrice(123.2);
		tick.setTimestamp(System.currentTimeMillis() - 60001);

		ResponseEntity<Void> response = restTemplate.postForEntity("/ticks", tick, Void.class);
		assertThat(response.getStatusCodeValue(), equalTo(500));
	}
	
	@Test
	void addNegativePrice() {
		Tick tick = new Tick();
		tick.setInstrument("TCS");
		tick.setPrice(-105);
		tick.setTimestamp(System.currentTimeMillis());

		ResponseEntity<Void> response = restTemplate.postForEntity("/ticks", tick, Void.class);
		assertThat(response.getStatusCodeValue(), equalTo(204));
	}


	@Test
	void addTickAndGetInstrument() {
		Tick tick = new Tick();
		tick.setInstrument("IBM.N");
		tick.setPrice(123.2);
		tick.setTimestamp(System.currentTimeMillis());

		restTemplate.postForEntity("/ticks", tick, Void.class);
		ResponseEntity<Statistic> response = restTemplate.getForEntity("/statistics/IBM.N", Statistic.class);

		assertThat(response.getStatusCodeValue(), equalTo(200));
	}
}
