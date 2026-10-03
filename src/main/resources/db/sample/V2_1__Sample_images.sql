-- Photos for the sample hotels and rooms. The files are in src/main/resources/sample-images
-- and are uploaded to the image bucket on startup (see SampleImageLoader).
UPDATE `hotel` SET `ImageKey` = 'hotels/new-york.jpg' WHERE `HotelID` = 1;
UPDATE `hotel` SET `ImageKey` = 'hotels/san-francisco.jpg' WHERE `HotelID` = 2;
UPDATE `hotel` SET `ImageKey` = 'hotels/miami-beach.jpg' WHERE `HotelID` = 3;
UPDATE `hotel` SET `ImageKey` = 'hotels/toronto.jpg' WHERE `HotelID` = 4;

-- One photo per room type, shared by every sample room of that type.
UPDATE `room` SET `ImageKey` = CONCAT('rooms/', `Roomtype`, '.jpg') WHERE `HotelID` IN (1, 2, 3, 4);
