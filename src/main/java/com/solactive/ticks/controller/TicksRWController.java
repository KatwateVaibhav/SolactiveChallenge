package com.solactive.ticks.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.solactive.ticks.entity.Statistic;
import com.solactive.ticks.entity.Tick;
import com.solactive.ticks.exception.InvalidInstrumentException;
import com.solactive.ticks.exception.InvalidTickException;
import com.solactive.ticks.exception.TransactionTimestampException;
import com.solactive.ticks.service.TicksStatisticsService;
import com.solactive.ticks.utils.TicksConstant;

/**
 * Endpoints:
 * POST /ticks – called every time a ticks is made. It is also the sole input of this rest API. 
 * 
 * GET /statistics This endpoint returns the statistics computed on the transactions within the last 60 seconds.
 *
 * @return {
 * 
 *         "avg": "100.53",
 *         "max": "200000.49",
 *         "min": "50.23",
 *         "count": 10 
 *         }
 *         
 * GET /statistics/{instrument_identifier} This endpoint returns the statistics computed on the transactions within the last 60 seconds for particular instrument.
 */
@RestController
public class TicksRWController {
	
	@Autowired
	private TicksStatisticsService ticksStatisticsService;
	
	
	@PostMapping("/ticks")
	public ResponseEntity addTick(@Valid @NotNull @RequestBody Tick tick) throws TransactionTimestampException {
		
       // If the transaction is older than 60 seconds then return 204
        if (tick.getTimestamp() < (System.currentTimeMillis()-TicksConstant.DEFAULT_SLIDING_WINDOW_MS)) {
            throw new TransactionTimestampException(HttpStatus.INTERNAL_SERVER_ERROR,TicksConstant.OLDER_TXN_EXCEPTION);
        } 
        if (tick.getPrice() < 0.0) {
            throw new InvalidTickException(HttpStatus.NO_CONTENT,TicksConstant.PRICE_LESS_THAN_ZERO_MESSAGE);
        } 

		ticksStatisticsService.saveTicks(tick);
		return new ResponseEntity("Transaction created successfully", HttpStatus.CREATED);
	}

	@GetMapping("/statistics")
	public Statistic getGlobalStats() {
		return ticksStatisticsService.getTotalStats();
	}

	@GetMapping("/statistics/{instrument}")
	public Statistic getInstrumentStats(@PathVariable String instrument) throws InvalidInstrumentException  {
		return ticksStatisticsService.getStatsForInstrument(instrument);
	}
	

}
