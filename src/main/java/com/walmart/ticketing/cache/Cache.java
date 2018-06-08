package com.walmart.ticketing.cache;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.walmart.ticketing.models.Event;
import com.walmart.ticketing.models.SeatHold;

public class Cache {

	private static ConcurrentMap<Integer, Event> eventCache = new ConcurrentHashMap<Integer, Event>();
	private static ConcurrentMap<Integer, SeatHold> seatHoldCache = new ConcurrentHashMap<Integer, SeatHold>();
	private static final Queue<SeatHold> seatHoldQueue = new LinkedList<SeatHold>();
	
	private Cache() {
		
	}
	
	public static ConcurrentMap<Integer, Event> getEventCache() {
		return eventCache;
	}
	
	public static ConcurrentMap<Integer, SeatHold> getseatHoldCache() {
		return seatHoldCache;
	}
	
	public static Queue<SeatHold> getSeatHoldQueue() {
		return seatHoldQueue;
	}
	
}
