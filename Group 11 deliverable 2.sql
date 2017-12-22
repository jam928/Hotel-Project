/*
SQLyog Enterprise
MySQL - 5.7.17 : Database - project
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`project` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `project`;

/*Table structure for table `breakfast` */

CREATE TABLE `breakfast` (
  `bType` varchar(32) NOT NULL,
  `HotelID` int(11) NOT NULL,
  `Description` varchar(128) DEFAULT NULL,
  `bPrice` decimal(5,2) NOT NULL,
  PRIMARY KEY (`bType`,`HotelID`),
  KEY `HotelBreakfast` (`HotelID`),
    CONSTRAINT `HotelBreakfast` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `creditcard` */

CREATE TABLE `creditcard` (
  `Cnumber` varchar(16) NOT NULL,
  `ExpDate` date NOT NULL,
  `SecCode` int(11) NOT NULL,
  `Type` enum('VISA','AMEX','DISC','MSTR') NOT NULL,
  `Name` varchar(32) NOT NULL,
  `BillingAddr` varchar(50) NOT NULL,
  PRIMARY KEY (`Cnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `customer` */

CREATE TABLE `customer` (
  `CID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(32) NOT NULL,
  `Address` varchar(32) NOT NULL,
  `Phone_no` char(16) NOT NULL,
  `Email` varchar(32) NOT NULL,
  `Password` varchar(32) NOT NULL,
  PRIMARY KEY (`CID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

/*Table structure for table `hotel` */

CREATE TABLE `hotel` (
  `HotelID` int(11) NOT NULL AUTO_INCREMENT,
  `Street` varchar(50) NOT NULL,
  `City` varchar(50) NOT NULL,
  `State` char(2) NOT NULL,
  `ZIP` char(10) NOT NULL,
  `Country` varchar(20) NOT NULL,
  PRIMARY KEY (`HotelID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `hotelphones` */

CREATE TABLE `hotelphones` (
  `Phone_no` char(20) NOT NULL,
  `HotelID` int(11) NOT NULL,
  PRIMARY KEY (`Phone_no`),
  KEY `HotelID2` (`HotelID`),
  CONSTRAINT `HotelID2` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `offer-room` */

CREATE TABLE `offer-room` (
  `Room_no` int(11) NOT NULL,
  `SDate` date NOT NULL,
  `EDate` date NOT NULL,
  `Discount` int(11) NOT NULL DEFAULT '0',
  `HotelID` int(11) NOT NULL,
  PRIMARY KEY (`Room_no`,`SDate`,`EDate`,`HotelID`),
  KEY `HotelID` (`HotelID`),
  CONSTRAINT `HotelID` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`),
  CONSTRAINT `Offer` FOREIGN KEY (`Room_no`) REFERENCES `room` (`Room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `reservation` */

CREATE TABLE `reservation` (
  `InvoiceNo` int(11) NOT NULL AUTO_INCREMENT,
  `ResDate` date NOT NULL,
  `TotalAmt` decimal(7,2) DEFAULT NULL,
  `CID` int(11) DEFAULT NULL,
  `Room_no` int(11) DEFAULT NULL,
  `HotelID` int(11) NOT NULL,
  `InDate` date NOT NULL,
  `OutDate` date DEFAULT NULL,
  `NoOfDays` int(11) DEFAULT NULL,
  `Cnumber` varchar(16) NOT NULL,
  `bType` varchar(32) DEFAULT NULL,
  `sType` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`InvoiceNo`),
  KEY `Room_no` (`Room_no`),
  KEY `HotelID2` (`HotelID`),
  KEY `CNumber` (`Cnumber`),
  KEY `ReservationBreakfast` (`bType`),
  KEY `ReservationService` (`sType`),
  KEY `CID` (`CID`),
  CONSTRAINT `CID` FOREIGN KEY (`CID`) REFERENCES `customer` (`CID`),
  CONSTRAINT `CNumber` FOREIGN KEY (`Cnumber`) REFERENCES `creditcard` (`Cnumber`),
  CONSTRAINT `HotelID2` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`),
  CONSTRAINT `ReservationBreakfast` FOREIGN KEY (`bType`) REFERENCES `breakfast` (`bType`),
  CONSTRAINT `ReservationService` FOREIGN KEY (`sType`) REFERENCES `service` (`sType`),
  CONSTRAINT `Room_no` FOREIGN KEY (`Room_no`) REFERENCES `room` (`Room_no`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Table structure for table `review` */

CREATE TABLE `review` (
  `ReviewID` int(11) NOT NULL AUTO_INCREMENT,
  `Rating` int(11) NOT NULL,
  `TextComment` text,
  `Room_no` int(11) DEFAULT NULL,
  `bType` varchar(32) DEFAULT NULL,
  `sType` varchar(32) DEFAULT NULL,
  `HotelID` int(11) DEFAULT NULL,
  `CID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ReviewID`),
  KEY `RoomReview` (`Room_no`),
  KEY `RoomReviewHotel` (`HotelID`),
  KEY `ServiceReview` (`sType`),
  KEY `BreakfastReview` (`bType`),
  KEY `CID2` (`CID`),
  CONSTRAINT `BreakfastReview` FOREIGN KEY (`bType`) REFERENCES `breakfast` (`bType`),
  CONSTRAINT `CID2` FOREIGN KEY (`CID`) REFERENCES `customer` (`CID`),
  CONSTRAINT `RoomReview` FOREIGN KEY (`Room_no`) REFERENCES `room` (`Room_no`),
  CONSTRAINT `RoomReviewHotel` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`),
  CONSTRAINT `ServiceReview` FOREIGN KEY (`sType`) REFERENCES `service` (`sType`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Table structure for table `room` */

CREATE TABLE `room` (
  `Room_no` int(11) NOT NULL,
  `HotelID` int(11) NOT NULL,
  `Price` decimal(5,2) NOT NULL,
  `Capacity` int(11) DEFAULT NULL,
  `Floor_no` int(11) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Roomtype` enum('standard','double','deluxe','suite') NOT NULL,
  `Image_HTML` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Room_no`,`HotelID`),
  KEY `RoomType` (`Roomtype`),
  KEY `Hotel` (`HotelID`),
  CONSTRAINT `Hotel` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `service` */

CREATE TABLE `service` (
  `sType` varchar(32) NOT NULL,
  `HotelID` int(11) NOT NULL,
  `sCost` decimal(5,2) NOT NULL,
  PRIMARY KEY (`sType`,`HotelID`),
  KEY `HotelService` (`HotelID`),
  CONSTRAINT `HotelService` FOREIGN KEY (`HotelID`) REFERENCES `hotel` (`HotelID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
