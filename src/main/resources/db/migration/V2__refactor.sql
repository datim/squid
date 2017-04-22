/* Discovered images */
CREATE TABLE image (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  discovered TIMESTAMP default CURRENT_TIME,
  name VARCHAR(255) NOT NULL,
  etag VARCHAR(255) NOT NULL,
  url VARCHAR(2048) NOT NULL,
  height INT,
  width INT,
  tshirt_size VARCHAR(10),
  b_size INT
);

/* Make image fields unique */
ALTER TABLE image add UNIQUE(url);

/* Discovered Pages */
CREATE TABLE page (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  url VARCHAR(2048) NOT NULL,
  etag VARCHAR(255) NOT NULL
);

/* Make image fields unique */
ALTER TABLE page add UNIQUE(url);
