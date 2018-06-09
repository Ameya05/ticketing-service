package com.walmart.ticketing.service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.walmart.ticketing.cache.Cache;
import com.walmart.ticketing.models.Event;
import com.walmart.ticketing.models.SeatHold;
import com.walmart.ticketing.models.VenueSeats;

public class HoldExpiryProcessor implements Runnable {

	private static HoldExpiryProcessor instance;
	private static final Logger LOGGER = Logger.getLogger(HoldExpiryProcessor.class.getName());
	
	private ConcurrentMap<Integer, SeatHold> seatHoldCache = Cache.getseatHoldCache();
	private Queue<SeatHold> seatHoldQueue = Cache.getSeatHoldQueue();
	private ConcurrentMap<Integer, Event> eventCache = Cache.getEventCache();
	
	private HoldExpiryProcessor() {
		
	}
	
	public static HoldExpiryProcessor getInstance() {
		
		if(instance == null) 
			instance = new HoldExpiryProcessor();
		
		return instance;
		
	}
	
	public void run() {
		
		LOGGER.info("Started Hold Expiry Processor ");
		
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
				
				if(nextHold != null && seatHoldCache.containsKey(nextHold.getSeatHoldId()))
				{
					// Remove it from cache since it has expired
					seatHoldCache.remove(nextHold.getSeatHoldId());
					Event eventForThisHold = eventCache.get(nextHold.getEventId());
				
					// If invalid data, skip further processing
					if(eventForThisHold == null || eventForThisHold.getSeatsPerClass() == null
							|| eventForThisHold.getSeatsPerClass().size() <= nextHold.getSeatClassIndex())
						continue;
					
					VenueSeats eventSeats = eventForThisHold.getSeatsPerClass().get(nextHold.getSeatClassIndex());
					
					int reclaimedSeatCount = nextHold.getSeats().getSeatCount();
					
					eventSeats.setSeatCount(eventSeats.getSeatCount() + reclaimedSeatCount);
					eventForThisHold.setSeatsAvailable(eventForThisHold.numSeatsAvailable() + reclaimedSeatCount);
					
					LOGGER.info("\nSeat Hold "+ nextHold.getSeatHoldId() + 
							" expired due to timeout " + nextHold.toString() + 
							"\nNew "+ eventSeats.getSeatClass() + 
							" tier seat count for this event: " + eventSeats.getSeatCount() +
							"\nTotal seats available: " + eventForThisHold.numSeatsAvailable());
				}
			}
		}
	}

}
