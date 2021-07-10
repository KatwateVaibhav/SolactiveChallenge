package com.solactive.ticks.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.solactive.ticks.entity.InstrumentTicks;
import com.solactive.ticks.entity.Statistic;


@SpringBootTest
@ContextConfiguration
@RunWith(MockitoJUnitRunner.class)
class StatisticsCalculatorTests {
	
	@InjectMocks
	private StatisticsCalculator calc;

	@Test
	void cachedStatsOnInitAreEmpty() {
		Statistic stats = calc.getCachedStats();

		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(0L));
		assertThat(stats.getAvg(), equalTo(0.0));
		assertThat(stats.getMax(), equalTo(0.0));
		assertThat(stats.getMin(), equalTo(0.0));
	}

	@Test
	void calculationOfSingleElementIsCorrect() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 12.3));

		Statistic stats = calc.getCachedStats();

		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(1L));
		assertThat(stats.getAvg(), equalTo(12.3));
		assertThat(stats.getMax(), equalTo(12.3));
		assertThat(stats.getMin(), equalTo(12.3));
	}

	@Test
	void cachedStatsAreCorrectAfterCalculation() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 12.3));

		Statistic stats = calc.getCachedStats();

		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(1L));
		assertThat(stats.getAvg(), equalTo(12.3));
		assertThat(stats.getMax(), equalTo(12.3));
		assertThat(stats.getMin(), equalTo(12.3));
	}

	@Test
	void calculationOfMultipleTicksAreCorrect() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 1.0));
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 2.0));
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 6.0));

		Statistic stats = calc.getCachedStats();

		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(3L));
		assertThat(stats.getAvg(), equalTo(3.0));
		assertThat(stats.getMax(), equalTo(6.0));
		assertThat(stats.getMin(), equalTo(1.0));
	}

	@Test
	void parallelTicksAreSynchronized() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(100);

		for (int i = 0; i < 100; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 100; i++) {
						calc.addTick(new InstrumentTicks(System.currentTimeMillis(), i));
					}
				}
			});
		}

		double expectedAvg = 0;
		for (int i = 0; i < 100; i++) {
			expectedAvg += i;
		}
		expectedAvg /= 100;

		executor.shutdown();
		boolean timeout = !executor.awaitTermination(59, TimeUnit.SECONDS);
		assertThat(timeout, is(false));

		Statistic stats = calc.getCachedStats();
		assertThat(stats.getCount(), equalTo(10000L));
		
		// losing some precision is expected because we are doing calculations on doubles
		// this doesn't have to do with parallel addition of ticks
		assertThat(stats.getAvg(), closeTo(expectedAvg, 0.0001));
		assertThat(stats.getMin(), equalTo(0.0));
		assertThat(stats.getMax(), equalTo(99.0));

	}

	@Test
	void willNotAddTickOlderThanOneMinute() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis() - 600001, 12.3));
		Statistic stats = calc.getCachedStats();

		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(0L));
		assertThat(stats.getAvg(), equalTo(0.0));
		assertThat(stats.getMax(), equalTo(0.0));
		assertThat(stats.getMin(), equalTo(0.0));
	}

	@Test
	void addNewTickThatIsOldest() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 1.0));
		calc.addTick(new InstrumentTicks(System.currentTimeMillis() - 1000, 5.0));

		Statistic stats = calc.getCachedStats();
		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(2L));
		assertThat(stats.getAvg(), equalTo(3.0));
	}

	@Test
	void addNewTickInMiddle() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 1.0));
		calc.addTick(new InstrumentTicks(System.currentTimeMillis() - 2000, 5.0));
		calc.addTick(new InstrumentTicks(System.currentTimeMillis() - 1000, 6.0));

		Statistic stats = calc.getCachedStats();
		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(3L));
		assertThat(stats.getAvg(), equalTo(4.0));
	}

	@Test
	void willCleanOldTicks() throws InterruptedException {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 1.0));
		Thread.sleep(60 * 1000 + 1);
		calc.cleanExpiredTicks();
		Statistic stats = calc.getCachedStats();
		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(0L));
	}

	@Test
	void willNotCleanNonExpiredTicks() {
		calc.addTick(new InstrumentTicks(System.currentTimeMillis(), 1.0));
		calc.cleanExpiredTicks();
		Statistic stats = calc.getCachedStats();
		assertThat(stats, is(not(nullValue())));
		assertThat(stats.getCount(), equalTo(1L));
	} 


}
