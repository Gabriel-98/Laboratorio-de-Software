package com.utilities;

import java.math.BigInteger;
import java.util.Vector;

public class PasswordEncoder {
	
	private BigInteger clave;
	private Vector<Character> alp;
	
	public PasswordEncoder() {
		clave = new BigInteger("45349287593623648396713953798483034126903942967403364220287311540715120875348581");
		alp = new Vector<Character>();
		for(int i=0; i<10; i++) alp.add((char)('0' + i));
		for(int i=0; i<26; i++) alp.add((char)('a' + i));
		for(int i=0; i<26; i++) alp.add((char)('A' + i));
	}
	
	public String encode(String password) {
		BigInteger mod = new BigInteger(String.valueOf(alp.size()));
		BigInteger num = BigInteger.ONE;		
		for(int i=0; i<password.length(); i++) {
			int x = password.charAt(i) - 32;
			num = num.multiply(mod);
			num = num.add(new BigInteger(String.valueOf(x)));
		}
		num = num.multiply(clave);
		
		String encodedPassword = "";
		while(num.compareTo(BigInteger.ZERO) == 1) {
			int x = num.remainder(mod).intValue();
			num = num.divide(mod);
			encodedPassword += alp.get(x);
		}
		return encodedPassword;		
	}
	
	public boolean matches(String password, String encodedPassword) {
		return encode(password).equals(encodedPassword);
	}
}
