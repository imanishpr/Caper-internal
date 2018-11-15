package com.khelacademy.www.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.khelacademy.www.pojos.BookingRequestObject;
import com.khelacademy.www.pojos.PriceDetails;

public class PaymentRequestValidator {
	DBArrow SQLArrow = DBArrow.getArrow();
	public boolean validate(BookingRequestObject bookingRequestObject) throws SQLException {
		Integer amount = 0;
		amount = calculateAmount(bookingRequestObject.getPriceDetail(), bookingRequestObject.getEventId());
		return (amount.intValue() == bookingRequestObject.getTotalAmount().intValue());
	}
	
	private Integer calculateAmount(List<PriceDetails> priceDetails, Integer eventId) throws SQLException {
		Integer amount = 0;
		List<Integer> priceIds = new ArrayList<Integer>();
		Map<Integer, Integer> prices = new HashMap<Integer,Integer>();
		for(PriceDetails p : priceDetails) {
			priceIds.add(p.getPriceId());
		}
		String idsWithQuote = priceIds.toString().replace("[", "").replace("]", "");
        PreparedStatement statement = SQLArrow.getPreparedStatement("SELECT * FROM price where event_id = ? AND price_id IN ("+idsWithQuote+")");
        statement.setInt(1, eventId);

        try (ResultSet rs = SQLArrow.fire(statement)) {
            while (rs.next()) {
            	prices.put(rs.getInt("price_id"), rs.getInt("price_amount"));
            }
            SQLArrow.relax(rs);
        }
		
		for(PriceDetails p : priceDetails) {
			amount += (prices.get(p.getPriceId()) * p.getQuantity());
		}
		return amount;
		
	}

}
