CREATE TABLE aircraft.route (
  id int AUTO_INCREMENT PRIMARY KEY,
  flight_number varchar(100) NOT NULL,
  departure varchar(100) DEFAULT NULL,
  destination varchar(100) DEFAULT NULL
)