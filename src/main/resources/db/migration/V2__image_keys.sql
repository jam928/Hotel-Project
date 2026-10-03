-- Object keys of the hotel's and room's photos in the image bucket (e.g. rooms/suite.jpg),
-- or NULL for no photo. Room.Image_HTML from the original schema is no longer used.
ALTER TABLE `hotel` ADD COLUMN `ImageKey` varchar(255) DEFAULT NULL;
ALTER TABLE `room` ADD COLUMN `ImageKey` varchar(255) DEFAULT NULL;
