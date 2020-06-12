package com.utilities;

import java.util.TreeMap;

public class CurrencyConverter {

	// 1.00 USD = x currency
	private TreeMap<String,Double> prices;
	
	public CurrencyConverter() {
		prices = new TreeMap<String,Double>();
		prices.put("USD", 1.00);
		prices.put("EUR", 0.89);
		prices.put("COP", 3610.00);
	}
	
	public boolean isEnabled(String currency) {
		if(prices.get(currency) == null)
		return false;
		return true;
	}
	
	public Double convert(String source, String target, Double amount){
		if(isEnabled(source) && isEnabled(target))
		return (amount * prices.get(target)) / prices.get(source);
		else
		return null;
	}
}
