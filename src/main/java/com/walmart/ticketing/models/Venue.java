package com.walmart.ticketing.models;

import java.util.ArrayList;
import java.util.List;

import com.walmart.ticketing.exception.TicketingException;

/**
 * @author Ameya Advankar
 *
 */
public class Venue {

	private static int venueSerialID = 0;
	
	private int venueID;
	
	private String venueName;
	
	private List<VenueSeats> seatsPerClass;

	public int getVenueID() {
		return venueID;
	}

	public void setVenueID(int venueID) {
		this.venueID = venueID;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public List<VenueSeats> getSeatsPerClass() {
		return seatsPerClass;
	}

	public void setSeatsPerClass(List<VenueSeats> seatsPerClass) {
		this.seatsPerClass = seatsPerClass;
	}	
	
	/**
	 * Create a <code>Venue</code> object
	 * 
	 * @param venueName - Name of the Venue
	 * @param seatsPerClass - List of {@link com.walmart.ticketing.models.VenueSeats} available in the Venue
	 * @throws TicketingException - When venueName or SeatsPerClass is empty
	 */
	public Venue(String venueName, List<VenueSeats> seatsPerClass) throws TicketingException {
		
		if(venueName == null || venueName.isEmpty() || seatsPerClass == null || seatsPerClass.isEmpty())
			throw new TicketingException("Name and Seats can't be empty");
		
		setVenueID(++venueSerialID);
		setSeatsPerClass(new ArrayList<VenueSeats>(seatsPerClass));
	}
	
}
