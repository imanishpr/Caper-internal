package com.khelacademy.www.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.khelacademy.www.pojos.EventPrice;

public class PriceUtili {
	public static Integer startPrice(List<EventPrice> prices) {
		return prices.stream().collect(Collectors.minBy((x, y) -> x.getPriceAmount() - y.getPriceAmount()))
				.get().getPriceAmount();
	}
}
