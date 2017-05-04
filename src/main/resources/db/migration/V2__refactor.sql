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

/* page url is unique */
ALTER TABLE page ADD UNIQUE(url);

/* Queries requested by the user */
CREATE TABLE query (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  url VARCHAR(2048) NOT NULL,
  name VARCHAR(255),
  max_pages INT NOT NULL,
  max_images INT NOT NULL
);

/* url field must be unique */
ALTER TABLE query ADD UNIQUE(url);

/* Create a search topology of pages */
CREATE TABLE page_topology (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  query_id INT NOT NULL,
  page_id INT NOT NULL,
  parent_id INT,
  create_time TIMESTAMP default CURRENT_TIME,
);

ALTER TABLE page_topology ADD FOREIGN KEY (query_id) REFERENCES query(id);
ALTER TABLE page_topology ADD FOREIGN KEY (page_id) REFERENCES page(id);
ALTER TABLE page_topology ADD FOREIGN KEY (parent_id) REFERENCES page(id);
