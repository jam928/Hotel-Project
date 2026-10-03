-- Links saved cards to the customers who saved them. creditcard.Cnumber stays the primary key
-- (reservation references it), so a card can belong to more than one customer through this table.
-- ID identifies a saved card on the account page without putting the card number in it.
CREATE TABLE `customer_card` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CID` int(11) NOT NULL,
  `Cnumber` varchar(16) NOT NULL,
  `SavedOn` date NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CustomerCardUnique` (`CID`, `Cnumber`),
  KEY `CustomerCardNumber` (`Cnumber`),
  CONSTRAINT `CustomerCardCustomer` FOREIGN KEY (`CID`) REFERENCES `customer` (`CID`),
  CONSTRAINT `CustomerCardNumber` FOREIGN KEY (`Cnumber`) REFERENCES `creditcard` (`Cnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Until now a card was only tied to a customer by the reservations they paid with it.
INSERT INTO `customer_card` (`CID`, `Cnumber`, `SavedOn`)
SELECT `CID`, `Cnumber`, MIN(`ResDate`)
FROM `reservation`
WHERE `CID` IS NOT NULL
GROUP BY `CID`, `Cnumber`;
