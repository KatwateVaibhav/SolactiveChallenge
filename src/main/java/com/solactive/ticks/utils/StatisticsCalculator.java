package com.solactive.ticks.utils;

import java.util.PriorityQueue;

import org.springframework.stereotype.Component;

import com.solactive.ticks.entity.InstrumentTicks;
import com.solactive.ticks.entity.Statistic;

@Component
public class StatisticsCalculator {
	
	private Statistic computedStats = new Statistic();
	PriorityQueue<InstrumentTicks> priorityQueue = new PriorityQueue<>();
	
	
	public synchronized void addTick(InstrumentTicks instrumentTicks) {
		final long minTimeSlot = System.currentTimeMillis() -  TicksConstant.DEFAULT_SLIDING_WINDOW_MS;
				
		 if ((instrumentTicks.getTimestamp() > minTimeSlot)) {
			// OutDated ticks will be removed
			 while (!priorityQueue.isEmpty()  && priorityQueue.peek().getTimestamp() < System.currentTimeMillis() - TicksConstant.DEFAULT_SLIDING_WINDOW_MS)
				 priorityQueue.poll(); //returns elements in sorted order
			 
			// Ticks with valid Timeframe will be added
			 priorityQueue.offer(new InstrumentTicks(instrumentTicks.getTimestamp(), instrumentTicks.getPrice()));
			 generateStatsWithInLastMinute(priorityQueue);
		 }
	}
	//Generate statistics with last 60 minutes		
	private void generateStatsWithInLastMinute(PriorityQueue<InstrumentTicks> pq) {

		final int count = pq.size();
        double avg = 0.0;
        double min = Double.MAX_VALUE;
        double max = 0.0;

       
        for (final InstrumentTicks ticks: pq) {
            final double prc = ticks.getPrice();
            avg += prc;
            min = Math.min(min, prc);
            max = Math.max(max, prc);
        }

        avg /= count;

        // Add values to Statistics Object the values.
		 computedStats.setAvg(avg); 
		 computedStats.setCount(count);
		 computedStats.setMin(min); 
		 computedStats.setMax(max);
	}

	public synchronized Statistic getCachedStats() {
		return computedStats;
	}
	//All expired ticks will be cleaned from the object and recalculation is performed.
	public synchronized void cleanExpiredTicks() {
		final long minTime = System.currentTimeMillis() - TicksConstant.DEFAULT_SLIDING_WINDOW_MS;
		InstrumentTicks expiredTickObj = priorityQueue.stream().filter(node -> node.getTimestamp() < minTime)
				.findFirst().orElse(null);
		if (expiredTickObj != null) {
			priorityQueue.remove(expiredTickObj);
		}

		if (priorityQueue.isEmpty()) {
			computedStats = new Statistic();
		}

		updateStats();
	}
	
	private void updateStats() {
		PriorityQueue<InstrumentTicks> pq = new PriorityQueue<>(priorityQueue);
		double total = 0;
		while (!pq.isEmpty()) {
			InstrumentTicks tick = pq.poll();
			total += tick.getPrice();
			if (computedStats.getCount() == 0 || tick.getPrice() < computedStats.getMin()) {
				computedStats.setMin(tick.getPrice());
			}
			if (computedStats.getCount() == 0 || tick.getPrice() > computedStats.getMax()) {
				computedStats.setMax(tick.getPrice());
			}
			computedStats.setCount(priorityQueue.size());
		}
			computedStats.setAvg(total / computedStats.getCount());
		}
	
}
