package com.solactive.ticks.service;

import com.solactive.ticks.entity.Statistic;
import com.solactive.ticks.entity.Tick;
import com.solactive.ticks.exception.InvalidInstrumentException;

public interface TicksStatisticsService {
	
	void saveTicks(Tick tick);
	Statistic getTotalStats();
	Statistic getStatsForInstrument(String instrument) throws InvalidInstrumentException;
	void recalculateGlobalStats();
}
