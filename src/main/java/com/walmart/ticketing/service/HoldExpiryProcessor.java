package com.walmart.ticketing.service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import com.walmart.ticketing.models.Event;
import com.walmart.ticketing.models.SeatHold;
import com.walmart.ticketing.models.VenueSeats;

public class HoldExpiryProcessor implements Runnable {

	private static HoldExpiryProcessor instance;
	
	private HoldExpiryProcessor() {
		
	}
	
	public static HoldExpiryProcessor getInstance() {
		
		if(instance == null) 
			instance = new HoldExpiryProcessor();
		
		return instance;
		
	}
	
	public void run() {
		
		System.out.println("Started Hold Expiry Processor ");
		Queue<SeatHold> seatHoldQueue = Event.seatHoldQueue;
		ConcurrentMap<Integer, SeatHold> seatHoldCache = Event.seatHoldCache;
		ConcurrentMap<Integer, Event> eventCache = Event.eventCache;
		
		while(true) 
			
		{
			/*
			 * Poll seatHoldQueue every 0.5 seconds to check for expired SeatHolds
			 * If 
			 * 	SeatHold is found in the Cache, it means the SeatHold wasn't reserved.
			 * 	Add the number of seats to the Event seat list so that the seats can be reused.
			 * 	Fetch the respective Event object from cache and update its Seat count.
			 * Else
			 * 	Do nothing, just remove the SeatHold from queue.
			 */
			if(!seatHoldQueue.isEmpty() && seatHoldQueue.peek().getSeatHoldExpiry().isBefore(Instant.now())) 
			{
				SeatHold nextHold = seatHoldQueue.poll();
				
				if(seatHoldCache.containsKey(nextHold.getSeatHoldId()))
				{
					// Remove it from cache since it has expired
					seatHoldCache.remove(nextHold.getSeatHoldId());
					Event eventForThisHold = eventCache.get(nextHold.getEventId());
				
					VenueSeats eventSeats = eventForThisHold.getSeatsPerClass().get(nextHold.getSeatClassIndex());
					
					int reclaimedSeatCount = nextHold.getSeats().getSeatCount();
					
					eventSeats.setSeatCount(eventSeats.getSeatCount() + reclaimedSeatCount);
					eventForThisHold.setSeatsAvailable(eventForThisHold.numSeatsAvailable() + reclaimedSeatCount);
					
					System.out.println("\nSeat Hold "+ nextHold.getSeatHoldId() + " expired due to timeout ====>" +
							nextHold.toString() +
							"\nNew "+ eventSeats.getSeatClass() + 
							" seat count for this event " + eventSeats.getSeatCount() +
							"\nTotal seats available: " + eventForThisHold.numSeatsAvailable());
				}
			}
		}
	}

}
