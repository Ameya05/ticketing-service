package com.walmart.ticketing.service;

import java.util.ArrayList;
import java.util.List;

import com.walmart.ticketing.models.Event;
import com.walmart.ticketing.models.SeatHold;
import com.walmart.ticketing.models.Venue;
import com.walmart.ticketing.models.VenueSeats;

import junit.framework.TestCase;

public class TestEvent extends TestCase{

	protected Event walmartConference;
	protected Venue venue;
	protected int totalSeats = 0;
	
	public void setUp() {
		
		List<VenueSeats> allSeats = new ArrayList<VenueSeats>();
		
		//Setup Venue with 380 total seats
		VenueSeats platSeats = new VenueSeats("Platinum", 40);
		VenueSeats goldSeats = new VenueSeats("Gold", 40);
		VenueSeats stdSeats = new VenueSeats("Standard", 100);

		allSeats.add(platSeats);
		allSeats.add(goldSeats);
		allSeats.add(stdSeats);
		
		for(VenueSeats seats : allSeats)
			totalSeats += seats.getSeatCount();
		
		try {
			venue = new Venue("Madison Square Garden", allSeats);
		} 
		catch (Exception e) {
		} 
	}

	public void testNumSeatsAvailable() 
	{
		//Create event with hold expiry set to 2 seconds
		walmartConference = new Event(2, venue);
		assertEquals(totalSeats, walmartConference.numSeatsAvailable());
	}
	
//	public void testHoldExpire() 
//	{
//		walmartConference = new Event(4, venue);
//		
//		walmartConference.findAndHoldSeats(40, "luke@gmail.com");
//		
//		try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e) {
//		}
//		
//		assertEquals(totalSeats, walmartConference.numSeatsAvailable());
//		
//	}
	
	public void testReserveAfterHoldExpire() 
	{
		walmartConference = new Event(2, venue);
		SeatHold hold = walmartConference.findAndHoldSeats(40, "leia@gmail.com");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		walmartConference.reserveSeats(hold.getSeatHoldId(), hold.getHoldCustomerEmail());
		
		assertEquals(totalSeats, walmartConference.numSeatsAvailable());
	}
	
	public void testHoldAndReserve() 
	{
		//Create event with hold expiry set to 2 seconds
		walmartConference = new Event(2, venue);
		
		SeatHold holdToReserve = walmartConference.findAndHoldSeats(40, "Anakin@gmail.com");
		walmartConference.findAndHoldSeats(40, "jarjar@yahoo.com");
		walmartConference.findAndHoldSeats(40, "bensolo@gmail.com");
		walmartConference.findAndHoldSeats(100, "hansolo@rocketmail.com");

		walmartConference.reserveSeats(holdToReserve.getSeatHoldId(), holdToReserve.getHoldCustomerEmail());
		walmartConference.findAndHoldSeats(40, "maul@msn.com");

		//Let all holds expire by waiting 5 second
		try {
		Thread.sleep(5000);
		}
		catch(Exception e) {
		}
		
		assertEquals(totalSeats - 40, walmartConference.numSeatsAvailable());
		
	}
}
