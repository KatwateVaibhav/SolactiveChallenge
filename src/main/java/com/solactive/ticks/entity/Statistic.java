package com.solactive.ticks.entity;

public class Statistic {
	public Statistic() {
		super();
	}

	private double avg;
	private double max;
	private double min;
	private long count;

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Statistic [avg=" + avg + ", max=" + max + ", min=" + min + ", count=" + count + "]";
	}

}
