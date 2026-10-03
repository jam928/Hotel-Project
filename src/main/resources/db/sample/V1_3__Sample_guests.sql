-- Demo guests with past stays they have reviewed, a few upcoming stays, and room discounts.
-- Dates are relative to the day the migration runs, so past stays are always in the past.
-- Every guest signs in with the password "password".

INSERT INTO `customer` (`Name`, `Address`, `Phone_no`, `Email`, `Password`) VALUES
  ('Maria Garcia', '18 Elm St, Boston, MA', '617-555-0111', 'maria@example.com', 'password'),
  ('James Chen', '742 Pine Ave, Seattle, WA', '206-555-0122', 'james@example.com', 'password'),
  ('Priya Patel', '55 Oak Rd, Austin, TX', '512-555-0133', 'priya@example.com', 'password'),
  ('Daniel Okafor', '9 Birch Ln, Chicago, IL', '312-555-0144', 'daniel@example.com', 'password'),
  ('Sophie Martin', '310 Rue Cedar, Montreal, QC', '514-555-0155', 'sophie@example.com', 'password');

-- Test card numbers from the card networks; they cannot be charged.
INSERT INTO `creditcard` (`Cnumber`, `ExpDate`, `SecCode`, `Type`, `Name`, `BillingAddr`) VALUES
  ('4111111111111111', '2029-08-31', 123, 'VISA', 'Maria Garcia', '18 Elm St, Boston, MA'),
  ('5555555555554444', '2028-03-31', 456, 'MSTR', 'James Chen', '742 Pine Ave, Seattle, WA'),
  ('378282246310005', '2030-01-31', 7890, 'AMEX', 'Priya Patel', '55 Oak Rd, Austin, TX'),
  ('6011111111111117', '2028-11-30', 321, 'DISC', 'Daniel Okafor', '9 Birch Ln, Chicago, IL'),
  ('4012888888881881', '2029-05-31', 654, 'VISA', 'Sophie Martin', '310 Rue Cedar, Montreal, QC');

-- Stays by guest email. A negative checkin_days_ago is an upcoming stay.
CREATE TEMPORARY TABLE `sample_stay` (
  `email` varchar(32),
  `hotel_id` int,
  `room_no` int,
  `booked_days_ago` int,
  `checkin_days_ago` int,
  `nights` int,
  `card` varchar(16),
  `breakfast` varchar(32),
  `service` varchar(32)
);

INSERT INTO `sample_stay` VALUES
  ('maria@example.com', 1, 401, 90, 75, 3, '4111111111111111', 'American', 'Spa'),
  ('maria@example.com', 3, 104, 40, 30, 4, '4111111111111111', 'Continental', 'Cabana'),
  ('maria@example.com', 4, 301, 5, -20, 2, '4111111111111111', NULL, NULL),
  ('james@example.com', 2, 203, 120, 100, 2, '5555555555554444', 'Vegan', 'Bike rental'),
  ('james@example.com', 1, 102, 50, 45, 1, '5555555555554444', NULL, 'Airport shuttle'),
  ('priya@example.com', 4, 402, 200, 180, 5, '378282246310005', 'American', 'Laundry'),
  ('priya@example.com', 2, 305, 30, 21, 3, '378282246310005', 'Continental', NULL),
  ('priya@example.com', 3, 401, 10, -45, 4, '378282246310005', 'American', 'Spa'),
  ('daniel@example.com', 3, 206, 70, 60, 2, '6011111111111117', 'American', NULL),
  ('daniel@example.com', 4, 104, 25, 14, 3, '6011111111111117', NULL, 'Airport shuttle'),
  ('sophie@example.com', 1, 305, 160, 150, 2, '4012888888881881', 'Continental', 'Laundry'),
  ('sophie@example.com', 2, 402, 15, 10, 3, '4012888888881881', 'Vegan', 'Airport shuttle');

-- The total is the room and breakfast per night plus the service once.
INSERT INTO `reservation`
  (`ResDate`, `TotalAmt`, `CID`, `Room_no`, `HotelID`, `InDate`, `OutDate`, `NoOfDays`, `Cnumber`, `bType`, `sType`)
SELECT
  CURDATE() - INTERVAL s.booked_days_ago DAY,
  (room.Price + COALESCE(b.bPrice, 0)) * s.nights + COALESCE(svc.sCost, 0),
  c.CID,
  s.room_no,
  s.hotel_id,
  CURDATE() - INTERVAL s.checkin_days_ago DAY,
  CURDATE() - INTERVAL (s.checkin_days_ago - s.nights) DAY,
  s.nights,
  s.card,
  s.breakfast,
  s.service
FROM `sample_stay` s
JOIN `customer` c ON c.Email = s.email
JOIN `room` room ON room.HotelID = s.hotel_id AND room.Room_no = s.room_no
LEFT JOIN `breakfast` b ON b.HotelID = s.hotel_id AND b.bType = s.breakfast
LEFT JOIN `service` svc ON svc.HotelID = s.hotel_id AND svc.sType = s.service
ORDER BY s.booked_days_ago DESC;

DROP TEMPORARY TABLE `sample_stay`;

-- Reviews of past stays. Ratings run from 1 (poor) to 3 (great); each review is of
-- exactly one room, breakfast or service.
CREATE TEMPORARY TABLE `sample_review` (
  `email` varchar(32),
  `hotel_id` int,
  `room_no` int,
  `breakfast` varchar(32),
  `service` varchar(32),
  `rating` int,
  `comment` text
);

INSERT INTO `sample_review` VALUES
  ('maria@example.com', 1, 401, NULL, NULL, 3, 'The suite''s view of Park Avenue was worth every penny.'),
  ('maria@example.com', 1, NULL, 'American', NULL, 3, 'Best pancakes I have had at a hotel.'),
  ('maria@example.com', 1, NULL, NULL, 'Spa', 2, 'Relaxing massage, but we waited 20 minutes past our booking.'),
  ('maria@example.com', 3, 104, NULL, NULL, 2, 'Clean and comfortable, though the street was loud at night.'),
  ('maria@example.com', 3, NULL, NULL, 'Cabana', 3, 'Shady, quiet and the staff kept the drinks coming.'),
  ('james@example.com', 2, 203, NULL, NULL, 2, 'Good room for the price. The desk chair could be better.'),
  ('james@example.com', 2, NULL, 'Vegan', NULL, 3, 'Great to see a proper vegan breakfast. The tofu scramble was excellent.'),
  ('james@example.com', 2, NULL, NULL, 'Bike rental', 3, 'Bikes were in great shape. Perfect for riding along the Embarcadero.'),
  ('james@example.com', 1, 102, NULL, NULL, 1, 'Very small room and the air conditioner rattled all night.'),
  ('james@example.com', 1, NULL, NULL, 'Airport shuttle', 2, 'On time, but the van was cramped with luggage.'),
  ('priya@example.com', 4, 402, NULL, NULL, 3, 'Spacious suite with a lovely view of the lake.'),
  ('priya@example.com', 4, NULL, 'American', NULL, 3, 'The peameal bacon was a highlight of the trip.'),
  ('priya@example.com', 4, NULL, NULL, 'Laundry', 1, 'My shirts came back a day later than promised.'),
  ('priya@example.com', 2, 305, NULL, NULL, 2, 'Nice room, slow elevator.'),
  ('priya@example.com', 2, NULL, 'Continental', NULL, 2, 'Fresh sourdough, but the coffee ran out early.'),
  ('daniel@example.com', 3, 206, NULL, NULL, 3, 'Ocean breeze from the balcony every morning. Would stay again.'),
  ('daniel@example.com', 3, NULL, 'American', NULL, 2, 'Solid breakfast, a little pricey.'),
  ('daniel@example.com', 4, 104, NULL, NULL, 2, 'Comfortable bed, but the room faced the parking garage.'),
  ('daniel@example.com', 4, NULL, NULL, 'Airport shuttle', 3, 'Driver was friendly and helped with our bags.'),
  ('sophie@example.com', 1, 305, NULL, NULL, 2, 'Good location, the room was smaller than the photos suggested.'),
  ('sophie@example.com', 1, NULL, 'Continental', NULL, 1, 'The pastries tasted like they were from the day before.'),
  ('sophie@example.com', 1, NULL, NULL, 'Laundry', 2, 'Everything came back clean, if not quite folded.'),
  ('sophie@example.com', 2, 402, NULL, NULL, 3, 'Beautiful suite with a view of the Bay Bridge.'),
  ('sophie@example.com', 2, NULL, 'Vegan', NULL, 3, 'Loved the smoothies and avocado toast.');

INSERT INTO `review` (`Rating`, `TextComment`, `Room_no`, `bType`, `sType`, `HotelID`, `CID`)
SELECT r.rating, r.comment, r.room_no, r.breakfast, r.service, r.hotel_id, c.CID
FROM `sample_review` r
JOIN `customer` c ON c.Email = r.email;

DROP TEMPORARY TABLE `sample_review`;

-- Discounts (percent) on some rooms for the coming weeks.
INSERT INTO `offer-room` (`Room_no`, `SDate`, `EDate`, `Discount`, `HotelID`) VALUES
  (101, CURDATE(), CURDATE() + INTERVAL 60 DAY, 20, 3),
  (102, CURDATE(), CURDATE() + INTERVAL 60 DAY, 20, 3),
  (103, CURDATE(), CURDATE() + INTERVAL 60 DAY, 20, 3),
  (401, CURDATE() + INTERVAL 14 DAY, CURDATE() + INTERVAL 45 DAY, 15, 4),
  (402, CURDATE() + INTERVAL 14 DAY, CURDATE() + INTERVAL 45 DAY, 15, 4),
  (205, CURDATE(), CURDATE() + INTERVAL 30 DAY, 10, 1);
