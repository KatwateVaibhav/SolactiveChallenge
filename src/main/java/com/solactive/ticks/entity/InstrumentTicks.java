package com.solactive.ticks.entity;

import javax.validation.constraints.NotNull;

public class InstrumentTicks implements Comparable<InstrumentTicks> {
	
	@NotNull
    private long timestamp;
	private double price;
	public InstrumentTicks(long timestamp, double price) {
		super();
		this.timestamp = timestamp;
		this.price = price;
	}
	public InstrumentTicks() {
		
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public int compareTo(InstrumentTicks that) {
		return Long.compare(this.getTimestamp(),that.getTimestamp());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstrumentTicks other = (InstrumentTicks) obj;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "InstrumentTicks [timestamp=" + timestamp + ", price=" + price + "]";
	}
	
}
