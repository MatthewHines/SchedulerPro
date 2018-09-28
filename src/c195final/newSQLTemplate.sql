/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Matthew
 * Created: Apr 4, 2018
 */
/*
SELECT title, COUNT(title) AS count FROM appointment GROUP BY title;
*/

SELECT title, COUNT(title) AS count FROM appointment WHERE MONTH(start) = 2 AND YEAR(start) = 2017 GROUP BY title ;

/*
ALTER TABLE address MODIFY COLUMN addressid INT auto_increment;
ALTER TABLE appointment MODIFY COLUMN appointmentid INT auto_increment;
ALTER TABLE city MODIFY COLUMN cityid INT auto_increment;
ALTER TABLE country MODIFY COLUMN countryid INT auto_increment;
ALTER TABLE customer MODIFY COLUMN customerid INT auto_increment;
ALTER TABLE reminder MODIFY COLUMN reminderid INT auto_increment;
ALTER TABLE user MODIFY COLUMN userid INT auto_increment;



INSERT INTO user 
    (userId,userName,password,active,createBy,createDate,lastUpdatedBy) VALUES 
        (1,'wflick','wflick',1,'admin',CURDATE(),'admin'),
        (2,'user1','user1',1,'admin',CURDATE(),'admin'),
        (3,'user2','user2',1,'admin',CURDATE(),'admin'),
        (4,'test','test',1,'admin',CURDATE(),'admin');

INSERT INTO country
    (countryId,country,createDate,createdBy,lastUpdateBy) VALUES
	(1,'USA',CURDATE(),'admin','admin'),
        (2,'Japan',CURDATE(),'admin','admin'),
        (3,'Australia',CURDATE(),'admin','admin'),
        (4,'Russia',CURDATE(),'admin','admin'),
        (5,'Britain',CURDATE(),'admin','admin');

INSERT INTO city
    (cityId,city,countryId,createDate,createdBy,lastUpdateBy) VALUES
        (1,'Washington',1,CURDATE(),'admin','admin'),
        (2,'New York',1,CURDATE(),'admin','admin'),
        (3,'Los Angeles',1,CURDATE(),'admin','admin'),
        (4,'Chicago',1,CURDATE(),'admin','admin'),
        (5,'Houston',1,CURDATE(),'admin','admin'),
        (6,'Phoenix',1,CURDATE(),'admin','admin'),

        (7,'Tokyo',2,CURDATE(),'admin','admin'),
        (8,'Toyohashi',2,CURDATE(),'admin','admin'),
        (9,'Okazaki',2,CURDATE(),'admin','admin'),
        (10,'Toyota',2,CURDATE(),'admin','admin'),
        (11,'Akita',2,CURDATE(),'admin','admin'),

        (12,'Canberra',3,CURDATE(),'admin','admin'),
        (13,'Sydney',3,CURDATE(),'admin','admin'),
        (14,'Melbourne',3,CURDATE(),'admin','admin'),
        (15,'Brisbane',3,CURDATE(),'admin','admin'),
        (16,'Perth',3,CURDATE(),'admin','admin'),

        (17,'Moscow',4,CURDATE(),'admin','admin'),
        (18,'Saint Petersburg',4,CURDATE(),'admin','admin'),
        (19,'Yeketerinburg',4,CURDATE(),'admin','admin'),
        (20,'Kazan',4,CURDATE(),'admin','admin'),
        (21,'Novosibirsk',4,CURDATE(),'admin','admin'),

        (22,'London',5,CURDATE(),'admin','admin'),
        (23,'Birmingham',5,CURDATE(),'admin','admin'),
        (24,'Manchester',5,CURDATE(),'admin','admin'),
        (25,'Glasgow',5,CURDATE(),'admin','admin'),
        (26,'Leeds',5,CURDATE(),'admin','admin'),
        (27,'Liverpool',5,CURDATE(),'admin','admin');

INSERT INTO address
    (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) VALUES
        (1,'001 Address Road','',1,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (2,'002 Address Road','',1,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (3,'003 Address Road','',3,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (4,'004 Address Road','',5,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (5,'005 Address Road','',8,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (6,'006 Address Road','',11,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (7,'007 Address Road','',12,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (8,'008 Address Road','',13,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (9,'009 Address Road','',17,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (10,'010 Address Road','',17,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (11,'011 Address Road','',19,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (12,'012 Address Road','',22,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (13,'013 Address Road','',21,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (14,'014 Address Road','',24,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (15,'015 Address Road','',27,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (16,'016 Address Road','',22,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (17,'017 Address Road','',18,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (18,'018 Address Road','',13,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (19,'019 Address Road','',2,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (20,'020 Address Road','',4,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (21,'021 Address Road','',6,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (22,'022 Address Road','',8,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (23,'023 Address Road','',10,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (24,'024 Address Road','',12,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (25,'025 Address Road','',14,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (26,'026 Address Road','',16,'00000','111-222-0000',CURDATE(),'admin','admin'),
        (27,'027 Address Road','',18,'00000','111-222-0000',CURDATE(),'admin','admin');

INSERT INTO customer
    (customerId,customerName,addressId,active,createDate,createdBy,lastUpdateBy) VALUES
        (1,'Customer-1',1,1,CURDATE(),'admin','admin'),
        (2,'Customer-2',2,1,CURDATE(),'admin','admin'),
        (3,'Customer-3',3,1,CURDATE(),'admin','admin'),
        (4,'Customer-4',4,1,CURDATE(),'admin','admin'),
        (5,'Customer-5',5,1,CURDATE(),'admin','admin'),
        (6,'Customer-6',6,1,CURDATE(),'admin','admin'),
        (7,'Customer-7',7,1,CURDATE(),'admin','admin'),
        (8,'Customer-8',8,1,CURDATE(),'admin','admin'),
        (9,'Customer-9',9,1,CURDATE(),'admin','admin'),
        (10,'Customer-10',10,1,CURDATE(),'admin','admin'),
        (11,'Customer-11',11,1,CURDATE(),'admin','admin'),
        (12,'Customer-12',12,1,CURDATE(),'admin','admin'),
        (13,'Customer-13',13,1,CURDATE(),'admin','admin'),
        (14,'Customer-14',14,1,CURDATE(),'admin','admin'),
        (15,'Customer-15',15,1,CURDATE(),'admin','admin'),
        (16,'Customer-16',16,1,CURDATE(),'admin','admin'),
        (17,'Customer-17',17,1,CURDATE(),'admin','admin'),
        (18,'Customer-18',18,1,CURDATE(),'admin','admin'),    
        (19,'Customer-19',19,1,CURDATE(),'admin','admin'),
        (20,'Customer-20',20,1,CURDATE(),'admin','admin'),
        (21,'Customer-21',21,1,CURDATE(),'admin','admin'),
        (22,'Customer-22',22,1,CURDATE(),'admin','admin'),
        (23,'Customer-23',23,1,CURDATE(),'admin','admin'),
        (24,'Customer-24',24,1,CURDATE(),'admin','admin'),
        (25,'Customer-25',25,1,CURDATE(),'admin','admin'),
        (26,'Customer-26',26,1,CURDATE(),'admin','admin'),
        (27,'Customer-27',27,1,CURDATE(),'admin','admin');

INSERT INTO appointment
    (appointmentId,customerId,title,description,location,contact,url,start,end,createDate,createdBy,lastUpdateBy) VALUES
        (1,1,'Meeting','First Meeting','New York, New York','wflick','','2017-02-08 09:00:00','2017-02-08 10:00:00',CURDATE(),'admin','admin'),
        (2,1,'Consulting','First Consultation','New York, New York','wflick','','2017-02-09 09:00:00','2017-02-09 10:00:00',CURDATE(),'admin','admin'),
        (3,1,'Consulting','Follow-up','New York, New York','wflick','','2017-02-10 10:00:00','2017-02-10 11:00:00',CURDATE(),'admin','admin'),
        (4,2,'Meeting','First Meeting','New York, New York','wflick','','2017-02-07 09:00:00','2017-02-07 10:00:00',CURDATE(),'admin','admin'),
        (5,2,'Consulting','First Consultation','New York, New York','wflick','','2017-02-15 09:00:00','2017-02-15 10:00:00',CURDATE(),'admin','admin'),
        (6,2,'Consulting','Follow-up','New York, New York','wflick','','2017-02-22 09:00:00','2017-02-22 10:00:00',CURDATE(),'admin','admin'),
        (7,3,'Meeting','First Meeting','New York, New York','wflick','','2017-02-09 09:00:00','2017-02-09 10:00:00',CURDATE(),'admin','admin'),
        (8,3,'Consulting','First Consultation','New York, New York','wflick','','2017-02-16 09:00:00','2017-02-16 10:00:00',CURDATE(),'admin','admin'),
        (9,3,'Consulting','Follow-up','Online','wflick','','2017-02-24 09:00:00','2017-02-24 10:00:00',CURDATE(),'admin','admin'),
        (10,4,'Meeting','First Meeting','New York, New York','wflick','','2017-02-07 11:00:00','2017-02-07 12:00:00',CURDATE(),'admin','admin'),
        (11,4,'Consulting','First Consultation','New York, New York','wflick','','2017-02-10 09:00:00','2017-02-10 09:45:00',CURDATE(),'admin','admin'),
        (12,4,'Consulting','Follow-up','New York, New York','wflick','','2017-02-12 09:00:00','2017-02-12 10:00:00',CURDATE(),'admin','admin'),
        (13,5,'Meeting','First Meeting','Online','wflick','','2017-02-06 09:00:00','2017-02-06 10:00:00',CURDATE(),'admin','admin'),
        (14,5,'Consulting','First Consultation','Online','wflick','','2017-02-13 09:00:00','2017-02-13 10:00:00',CURDATE(),'admin','admin'),
        (15,5,'Consulting','Follow-up','Online','wflick','','2017-02-20 09:00:00','2017-02-20 10:00:00',CURDATE(),'admin','admin'),
        (16,6,'Meeting','First Meeting','Phoenix, Arizona','user1','','2017-02-06 12:00:00','2017-02-06 13:00:00',CURDATE(),'admin','admin'),
        (17,6,'Consulting','First Consultation','Phoenix, Arizona','user1','','2017-02-13 09:00:00','2017-02-13 10:00:00',CURDATE(),'admin','admin'),
        (18,6,'Consulting','Follow-up','Phoenix, Arizona','user1','','2017-02-20 09:00:00','2017-02-20 10:00:00',CURDATE(),'admin','admin'),
        (19,7,'Meeting','First Meeting','Phoenix, Arizona','user1','','2017-02-07 09:00:00','2017-02-07 10:00:00',CURDATE(),'admin','admin'),
        (20,7,'Consulting','First Consultation','Phoenix, Arizona','user1','','2017-02-14 09:00:00','2017-02-14 10:00:00',CURDATE(),'admin','admin'),
        (21,7,'Consulting','Follow-up','Phoenix, Arizona','user1','','2017-02-21 09:00:00','2017-02-21 10:00:00',CURDATE(),'admin','admin'),
        (22,8,'Meeting','First Meeting','Phoenix, Arizona','user1','','2017-02-08 09:00:00','2017-02-08 10:00:00',CURDATE(),'admin','admin'),
        (23,8,'Consulting','First Consultation','Phoenix, Arizona','user1','','2017-02-15 09:00:00','2017-02-15 10:00:00',CURDATE(),'admin','admin'),
        (24,8,'Consulting','Follow-up','Online','user1','','2017-02-22 09:00:00','2017-02-22 10:00:00',CURDATE(),'admin','admin'),
        (25,9,'Meeting','First Meeting','Phoenix, Arizona','user1','','2017-02-10 09:00:00','2017-02-10 10:00:00',CURDATE(),'admin','admin'),
        (26,9,'Consulting','First Consultation','Phoenix, Arizona','user1','','2017-02-17 09:00:00','2017-02-17 10:00:00',CURDATE(),'admin','admin'),
        (27,9,'Consulting','Follow-up','Phoenix, Arizona','user1','','2017-02-24 09:00:00','2017-02-24 10:00:00',CURDATE(),'admin','admin'),
        (28,10,'Meeting','First Meeting','Phoenix, Arizona','user1','','2017-02-11 09:00:00','2017-02-11 10:00:00',CURDATE(),'admin','admin'),
        (29,10,'Consulting','First Consultation','Phoenix, Arizona','user1','','2017-02-18 09:00:00','2017-02-18 10:00:00',CURDATE(),'admin','admin'),
        (30,10,'Consulting','Follow-up','Online','user1','','2017-02-25 09:00:00','2017-02-25 10:00:00',CURDATE(),'admin','admin'),
        (31,11,'Meeting','First Meeting','London, England','user2','','2017-02-06 12:00:00','2017-02-06 13:00:00',CURDATE(),'admin','admin'),
        (32,11,'Consulting','First Consultation','London, England','user2','','2017-02-13 09:00:00','2017-02-13 10:00:00',CURDATE(),'admin','admin'),
        (33,11,'Consulting','Follow-up','London, England','user2','','2017-02-20 09:00:00','2017-02-20 10:00:00',CURDATE(),'admin','admin'),
        (34,12,'Meeting','First Meeting','London, England','user2','','2017-02-07 09:00:00','2017-02-07 10:00:00',CURDATE(),'admin','admin'),
        (35,12,'Consulting','First Consultation','London, England','user2','','2017-02-14 09:00:00','2017-02-14 10:00:00',CURDATE(),'admin','admin'),
        (36,12,'Consulting','Follow-up','London, England','user2','','2017-02-21 09:00:00','2017-02-21 10:00:00',CURDATE(),'admin','admin'),
        (37,13,'Meeting','First Meeting','London, England','user2','','2017-02-08 09:00:00','2017-02-08 10:00:00',CURDATE(),'admin','admin'),
        (38,13,'Consulting','First Consultation','London, England','user2','','2017-02-15 09:00:00','2017-02-15 10:00:00',CURDATE(),'admin','admin'),
        (39,13,'Consulting','Follow-up','Online','user2','','2017-02-22 09:00:00','2017-02-22 10:00:00',CURDATE(),'admin','admin'),
        (40,13,'Meeting','First Meeting','London, England','user2','','2017-02-10 09:00:00','2017-02-10 10:00:00',CURDATE(),'admin','admin'),
        (41,13,'Consulting','First Consultation','London, England','user2','','2017-02-17 09:00:00','2017-02-17 10:00:00',CURDATE(),'admin','admin'),
        (42,13,'Consulting','Follow-up','London, England','user2','','2017-02-24 09:00:00','2017-02-24 10:00:00',CURDATE(),'admin','admin'),
        (43,13,'Meeting','First Meeting','London, England','user2','','2017-02-11 09:00:00','2017-02-11 10:00:00',CURDATE(),'admin','admin'),
        (44,13,'Consulting','First Consultation','London, England','user2','','2017-02-18 09:00:00','2017-02-18 10:00:00',CURDATE(),'admin','admin'),
        (45,13,'Consulting','Follow-up','Online','user2','','2017-02-25 09:00:00','2017-02-25 10:00:00',CURDATE(),'admin','admin');
*/