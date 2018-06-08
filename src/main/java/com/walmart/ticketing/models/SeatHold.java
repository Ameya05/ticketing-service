package com.walmart.ticketing.models;

import java.time.Instant;
import com.walmart.ticketing.exception.TicketingException;

/**
 * @author Ameya Advankar
 */
public class SeatHold {

	private static int seatHoldSerialID = 0;
	
	private int seatHoldId;
	
	private int eventId;
	
	private Instant seatHoldExpiry;
	
	private int seatClassIndex;
	
	private VenueSeats seats;

	private String holdCustomerEmail;

	/**
	 * Used to create a new SeatHold for a particular (venue seats, customer, event) combination
	 * 
	 * @param seats - A {@link com.walmart.ticketing.models.VenueSeats} object containing information 
	 * about the Class and Count of seats to be held by this SeatHold
	 * @param holdCustomerEmail - String email id of the Customer
	 * @param event - Event object to which this SeatHold is attached to
	 * @throws TicketingException - If <code>seats</code> is null or non-positive
	 */
	public SeatHold(VenueSeats seats, int seatClassIndex, String holdCustomerEmail, Event event) throws TicketingException {
		
		if(seats == null || seats.getSeatCount() <= 0)
			throw new TicketingException("Seat count has to be a positive number");
		
		setSeatHoldId(++seatHoldSerialID);
		setEventId(event.getEventId());
		setSeatClassIndex(seatClassIndex);
		
		setSeatHoldExpiry( Instant.now().plusSeconds((long)event.getEventHoldDuration()));
		setSeats(seats);
		setHoldCustomerEmail(holdCustomerEmail);
	}
	
	/**
	 * @return <b>seatHoldId</b> - Integer representing the unique identifier for this SeatHold
	 */
	public int getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(int seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	/**
	 * @return <b>eventId</b> - Integer representing the eventId for which this SeatHold was created
	 */
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	/**
	 * @return <b>seatHoldExpiry</b> - Instant object containing information about when this Seat Hold will expire
	 */
	public Instant getSeatHoldExpiry() {
		return seatHoldExpiry;
	}

	public void setSeatHoldExpiry(Instant seatHoldExpiry) {
		this.seatHoldExpiry = seatHoldExpiry;
	}

	public int getSeatClassIndex() {
		return seatClassIndex;
	}

	public void setSeatClassIndex(int seatClassIndex) {
		this.seatClassIndex = seatClassIndex;
	}

	/**
	 * @return <b>seats</b> - a {@link com.walmart.ticketing.models.VenueSeats} object representing seats being held by this SeatHold object
	 */
	public VenueSeats getSeats() {
		return seats;
	}

	public void setSeats(VenueSeats seats) {
		this.seats = seats;
	}

	/**
	 * @return <b>holdCustomerEmail</b> - Email id of the customer who requested this hold
	 */
	public String getHoldCustomerEmail() {
		return holdCustomerEmail;
	}

	public void setHoldCustomerEmail(String holdCustomerEmail) {
		this.holdCustomerEmail = holdCustomerEmail;
	}
	
	public String toString() {
		return 	"\n===================================" +
				"\nSeatHold Details" +
				"\n===================================" + 
				"\nSeatHold ID : " + getSeatHoldId() +
				"\nEvent ID: " + getEventId() + 
				"\nHold Expiry: " + getSeatHoldExpiry().toString() +
				"\nClass : " + seats.getSeatClass() +
				"\nSeats : " + seats.getSeatCount() +
				"\n===================================";
	}
	
}
