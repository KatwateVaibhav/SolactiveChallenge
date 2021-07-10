package com.solactive.ticks.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.solactive.ticks.entity.InstrumentTicks;
import com.solactive.ticks.entity.Statistic;
import com.solactive.ticks.entity.Tick;
import com.solactive.ticks.exception.InvalidInstrumentException;
import com.solactive.ticks.service.TicksStatisticsService;
import com.solactive.ticks.utils.StatisticsCalculator;
import com.solactive.ticks.utils.TicksConstant;

@Component
public class TicksStatisticsServiceImpl extends Thread implements TicksStatisticsService {
	
	@Autowired
	private StatisticsCalculator instrumentStatsCalculator;
	
	private final Map<String, StatisticsCalculator> instrumentStats = new ConcurrentHashMap<>();
	private final ExecutorService taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*4);
	private AtomicReference<Statistic> globalStats = new AtomicReference<>();
	
	@PostConstruct
	public void init() {
		this.start();
	}
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			List<Runnable> runnables = new ArrayList<>();
			Iterator<StatisticsCalculator> it = instrumentStats.values().iterator();
			while (it.hasNext()) {
				StatisticsCalculator calc = it.next();
				runnables.add(new Runnable() {
					
					@Override
					public void run() {
						calc.cleanExpiredTicks();						
					}
				});
			}

			// wait for cleanup of expired ticks on all instruments

			CompletableFuture<?>[] futures = runnables.stream().map(task -> CompletableFuture.runAsync(task, taskExecutor))
					.toArray(CompletableFuture[]::new);
			CompletableFuture.allOf(futures).join();
			 

			recalculateGlobalStats();
		}
	}
	@Override
	public void saveTicks(Tick tick) {
		
		instrumentStats.putIfAbsent(tick.getInstrument(), instrumentStatsCalculator);
		instrumentStats.get(tick.getInstrument()).addTick(new InstrumentTicks(tick.getTimestamp(), tick.getPrice()));
		recalculateGlobalStats();

	}


	public synchronized Statistic getTotalStats() {
		Statistic stats = globalStats.get();
		stats = (stats == null) ? new Statistic() : stats;
		return stats;
	}

	public Statistic getStatsForInstrument(String instrument)  throws InvalidInstrumentException{
		StatisticsCalculator calc = instrumentStats.get(instrument);
		if (calc == null) {
			throw new InvalidInstrumentException(TicksConstant.INSTRUMENT_NOT_FOUND,HttpStatus.NOT_FOUND.value());
		}
		return calc.getCachedStats();
	}

	@Override
	public synchronized void recalculateGlobalStats() {
		
		Statistic newGlobalStats = new Statistic();
		double total = 0;
		for (StatisticsCalculator calc : instrumentStats.values()) {
			
			Statistic stats = calc.getCachedStats();
			if (newGlobalStats.getCount() == 0 || (stats.getCount() > 0 && stats.getMax() > newGlobalStats.getMax())) {
				newGlobalStats.setMax(stats.getMax());
			}
			if (newGlobalStats.getCount() == 0 || (stats.getCount() > 0 && stats.getMin() < newGlobalStats.getMin())) {
				newGlobalStats.setMin(stats.getMin());
			}
			newGlobalStats.setCount(newGlobalStats.getCount() + stats.getCount());
			total += stats.getAvg() * stats.getCount();
		}
		if (newGlobalStats.getCount() > 0) {
			newGlobalStats.setAvg(total / newGlobalStats.getCount());
		}
		
		globalStats.set(newGlobalStats);
	}

}
