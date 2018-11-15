package com.khelacademy.www.pojos;

import java.util.Map;

public class PriceDetails {
	private Integer priceId;
	private Integer quantity;
	private Map<Integer, String> playerNames;
	
	public Integer getPriceId() {
		return priceId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public Map<Integer, String> getPlayerNames() {
		return playerNames;
	}
	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public void setPlayerNames(Map<Integer, String> playerNames) {
		this.playerNames = playerNames;
	}
	@Override
	public String toString() {
		return "PriceDetails [priceId=" + priceId + ", quantity=" + quantity
				+ ", playerNames=" + playerNames + "]";
	}
	
}
