package com.solactive.ticks.utils;

/**
 * Constant class to manage Constants at single place
 */
public class TicksConstant {
	public static final int DEFAULT_SLIDING_WINDOW_MS = 60 * 1000;
	public static final String OLDER_TXN_EXCEPTION = "Transaction is older then 60 seconds";
	public static final String INSTRUMENT_NOT_FOUND = "Instrument not found in System";
	public static final String PRICE_LESS_THAN_ZERO_MESSAGE = "Price should not be less than zero";
        
}
