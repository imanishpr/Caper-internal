package com.khelacademy.www.utils;

import com.khelacademy.www.pojos.BookingRequestObject;
import com.khelacademy.www.pojos.PriceDetails;

public class NoOfTickerCalculator {
	public static int totalTicket(BookingRequestObject bookingRequestObject) {
		int totalTicket = 0;
		for (PriceDetails p : bookingRequestObject.getPriceDetail()) {
			totalTicket += p.getQuantity();
		}
		return totalTicket;
		
	}
}
