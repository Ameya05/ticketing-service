package com.walmart.ticketing.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.walmart.ticketing.exception.TicketingException;
import com.walmart.ticketing.service.HoldExpiryProcessor;
import com.walmart.ticketing.service.TicketService;

/**
 * @author Ameya Advankar
 * 
 */
public class Event implements TicketService{

	//TODO - Move defaultHoldDuration to properties file
	private static int defaultHoldDuration = 300;
	private static int eventSerialID = 0;

	public static final ConcurrentMap<Integer, SeatHold> seatHoldCache = new ConcurrentHashMap<Integer, SeatHold>();
	public static final Queue<SeatHold> seatHoldQueue = new LinkedList<SeatHold>();
	public static final ConcurrentMap<Integer, Event> eventCache = new ConcurrentHashMap<Integer, Event>();
	
	private int eventId;
	
	private int eventHoldDuration = defaultHoldDuration;
	
	private int seatsAvailable;
	
	private List<VenueSeats> seatsPerClass;
	
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getEventHoldDuration() {
		return eventHoldDuration;
	}

	public void setEventHoldDuration(int eventHoldDuration) {		
		this.eventHoldDuration = eventHoldDuration;
	}

	public void setSeatsAvailable(int seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	}

	public List<VenueSeats> getSeatsPerClass() {
		return seatsPerClass;
	}

	public void setSeatsPerClass(List<VenueSeats> seatsPerClass){		
		this.seatsPerClass = seatsPerClass;
	}

	/**
	 * Create a new <code>Event</code> for a particular <code>venue</code>
	 * @param eventHoldDuration - Duration in seconds. A SeatHold created for this Event will expire after <code> eventHoldDuration </code> seconds have elapsed. 
	 * @param venue - Venue in which this Event is created
	 * @throws TicketingException - If holdDuration or Venue parameters are invalid
	 */
	public Event(int eventHoldDuration, Venue venue) 
	{	
		/*
		 * When the first event is created, 
		 * perform lazy initialization of the HoldExpiryProcessor daemon
		 */
		if(eventSerialID == 0)
		{
			HoldExpiryProcessor holdProcObj = HoldExpiryProcessor.getInstance();
			
			Thread t = new Thread(holdProcObj);
			t.start();
		}
		
		setEventId(++eventSerialID);
		
		if(eventHoldDuration > 0)
			setEventHoldDuration(eventHoldDuration);
		
		setSeatsPerClass(new ArrayList<VenueSeats>(venue.getSeatsPerClass()));
		
		// Set total seats available for this event
		for(VenueSeats venSeats : getSeatsPerClass())
			seatsAvailable += venSeats.getSeatCount();
		
		eventCache.put(getEventId(), this);
	}

	/**
	* The number of seats in the venue that are neither held nor reserved
	*
	* @return the number of tickets available in the venue
	*/
	public int numSeatsAvailable() 
	{
		return seatsAvailable;
	}

	/**
	* Find and hold the best available seats for a customer
	*
	* @param numSeats the number of seats to find and hold
	* @param customerEmail unique identifier for the customer
	* @return a <code>SeatHold</code> object identifying the specific seats and related
	* information OR <b>null</b> when 
	* 1. numSeats is non positive 
	* 2. CustomerEmail is not valid
	* 3. Seats couldn't be found
	*/
	public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) 
	{
		SeatHold seatHold = null;
		
		if(numSeats <= 0 || numSeats > this.numSeatsAvailable() 
				|| customerEmail == null || customerEmail.isEmpty())
			return seatHold;
		/*
		 * Iterate the seats by class, and book the seat Class which has seats available.
		 */
		for(int i = 0; i < getSeatsPerClass().size(); i++) 
		{
			VenueSeats seatClass = getSeatsPerClass().get(i);
			
			if(numSeats <= seatClass.getSeatCount())
			{
				seatClass.setSeatCount(seatClass.getSeatCount() - numSeats);
				seatsAvailable = seatsAvailable - numSeats;
				
				VenueSeats heldSeats = new VenueSeats(seatClass.getSeatClass(), numSeats);
				try {
					seatHold = new SeatHold(heldSeats, i, customerEmail, this);
					seatHoldCache.put(seatHold.getSeatHoldId(), seatHold);
					seatHoldQueue.add(seatHold);
					
					System.out.println(seatHold.toString());
					
					System.out.println("Seats available : " + numSeatsAvailable());
					
				} catch (TicketingException e) {
					System.out.println("Error while creating Seat Hold." + e.getMessage());
				}
				
				break;
			}
		}
		
		if(seatHold == null)
			System.out.println("Couldn't find and hold "+ numSeats +" seats for " + customerEmail);
		
		return seatHold;
		
	}

	/**
	* Commit seats held for a specific customer
	*
	* @param seatHoldId the seat hold identifier
	* @param customerEmail the email address of the customer to which the
	seat hold is assigned
	* @return a reservation confirmation code
	 * @throws TicketingException - If the reserveSeats is called with invalid seatHoldId or customerEmail
	*/
	public synchronized String reserveSeats(int seatHoldId, String customerEmail) 
	{
		/*
		 * If the seatHold Cache does not contain the seatHoldId, it means the holdId is not valid. </br>
		 * Return Error message in these cases
		 */
		if(!seatHoldCache.containsKey(seatHoldId))
			return "Reservation failed. Specified seatHoldId does not exist";
		
		SeatHold seatHoldToReserve = seatHoldCache.get(seatHoldId);
		 
		if(seatHoldToReserve.getHoldCustomerEmail().equals(customerEmail)) 
		{
			
			seatHoldCache.remove(seatHoldId);
			System.out.println("Reserved " + seatHoldToReserve.getSeats().getSeatCount() + 
						" " + seatHoldToReserve.getSeats().getSeatClass() + " seats for " +  customerEmail);
			
			return seatHoldToReserve.getEventId() + "-" + seatHoldToReserve.getSeatHoldId();
		}
		else
			return "Reservation failed. Specified customerEmail does not match the Hold details";
	}
}
