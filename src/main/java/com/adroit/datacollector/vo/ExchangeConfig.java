package com.adroit.datacollector.vo;

import java.io.Serializable;

public class ExchangeConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2890913838852991236L;
	private String exchnageName;

	public String getExchnageName() {
		return exchnageName;
	}

	public void setExchnageName(String exchnageName) {
		this.exchnageName = exchnageName;
	}

	@Override
	public String toString() {
		return "ExchangeConfig [exchnageName=" + exchnageName + "]";
	}
	
	
	
	
}
