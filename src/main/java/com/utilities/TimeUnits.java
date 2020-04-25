package com.utilities;

import java.util.TreeSet;

public class TimeUnits {
	
	private TreeSet<String> units;
	
	public TimeUnits() {
		units.add("dia");
		units.add("semana");
		units.add("mes");
		units.add("año");
	}
	
	public boolean isPresent(String timeUnit) {
		return units.contains(timeUnit);		
	}
}
