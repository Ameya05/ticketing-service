

# Ticketing Service
A Ticketing Service implementation for a high-demand performance venue.
[![Build Status](https://travis-ci.org/Ameya05/ticketing-service.svg?branch=master)](https://travis-ci.org/Ameya05/ticketing-service)
## Requirements: 

 - [ ] **jdk1.8**
 - [ ] **maven**

## Test and Build: 
**mvn clean install**  
This will build and run the test cases included in the application.

## Assumptions:

Detailed information about the logic has been accounted for in the JavaDocs written in the source code.

### Venue
 1. A Venue can have multiple Events created in it. 
 2. Every Venue has a unique `venueId`
 3. A Venue can have N types of seats e.g. Platinum, Gold, Silver, etc. each having separate count.
 4. Venue name cannot be blank and it must have positive number of seats in it;
 5. The priority of a specific Venue seat type is determined by where it is present in the `ArrayList<VenueSeats> seatsPerClass `object.  
 e.g.  
  ` VenueSeats platSeats = new VenueSeats("Platinum", 40);`  
  ` VenueSeats goldSeats = new VenueSeats("Gold", 40);`    
  ` allSeats.add(platSeats);`  
  ` allSeats.add(goldSeats);`  

   Since Platinum seats are added first to the Venue, it has a higher value than Gold seats
### Event
1. Every Event has a unique `eventId`.
2. Each Event can be created with a separate `seatHoldExpiry` period in seconds, which if set to <= 0, will default to 300 seconds i.e. 5 minutes.
3. Creating a new Event requires knowledge of the Venue.
4. When the first Event is created, a background thread is started for processing SeatHold expiry.
5. Each Event created in a Venue will utilize all the Venue seats.
6. Calling the `numSeatsAvailable` method on a particular Event object will return the number of seats neither held or reserved for that Event.
7. Calling the `findAndHoldSeats` method on a particular Event object will initiate the process to search and Hold the specified seat count for the customer. A `SeatHold` object is returned from the `findAndHoldSeats` function. This `SeatHold` object will be null in case there was an error creating the SeatHold or if the requested number of Seats could not be found.
8. The `reserveSeats` function for a particular Event object can be used to reserve the seats indicated by the specified `SeatHoldId`. If the Reservation is successful, it will return the Confirmation code (`EventId`-`SeatHoldId` ) . Else if the `SeatHoldId` is expired or if the Customer specified does not match the one who created the hold, this returns a String error message.


### SeatHold
 1. SeatHolds / reservations are for a particular type of seats and not for specific seat positions. e.g. When John requests 8 seats, he would get 8 tickets for the best tier which has that many vacant seats.
 2. Seat hold / reservations cannot span across different seat types if requested in the same request. e.g. if 8 seats are requested, he/she cannot receive 4 Platinum and 4 Gold.
 3. Two seats are equally desirable if they are of same type. e.g. two Platinum seats are considered equal.
 4. If the reserveSeats function is called with a different email id than the one used while creating the hold, the function does not reserve seat but rather returns an error message.
 5. A SeatHold is for a particular Seat Type for an Event in a Venue and expires after the set Duration.
 6. A SeatHold expires after set number of seconds if not reserved. 
 
### HoldExpiryProcessor
1. This is responsible for picking up expired SeatHolds from the `seatHoldQueue` and processing them.
2. If the SeatHold expires and it is not already reserved, the seats are released back to the Event.
3. This processor runs till the application is stopped or terminated.
4. The processor runs in a separate thread and may get preempted on certain remote machines due to threading policies. This might cause SeatHolds to be not processed although they have expired.
