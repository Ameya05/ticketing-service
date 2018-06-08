package com.walmart.ticketing.models;


/**
 * VenueSeats contains the following information for the venue - </br>
 * <b>seatClass</b> e.g. Gold , Platinum, Standard</br>
 * <b>seatCount</b> i.e. count of seats for this particular class in the Venue
 * 
 * @author Ameya Advankar
 */
public class VenueSeats {

	/* 
	 * The String denoting the Class of the Seat. 
	 * This isn't hard coded, but rather can be passed dynamically when initialized.
	 */
	private String seatClass;
	
	/*
	 * Count of seats available for this class within the Venue / Event
	 */
	private int seatCount;

	public String getSeatClass() {
		return seatClass;
	}

	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
	}

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}
	
	public VenueSeats(String seatClass, int seatCount) {
		setSeatClass(seatClass);
		setSeatCount(seatCount);
	}
	
	public String toString() {
		
		return "\nClass : " + this.getSeatClass() + " - Seat Count : " + this.getSeatCount();
	}
	
}
