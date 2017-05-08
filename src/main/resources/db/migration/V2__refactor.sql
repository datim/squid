/* Discovered images */
CREATE TABLE image (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  discovered TIMESTAMP default CURRENT_TIME,
  name VARCHAR(255) NOT NULL,
  etag VARCHAR(255),
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
  md5 VARCHAR(255)
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

/* Create a topology of pages discovered as part of a query */
CREATE TABLE page_topology (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  query_id INT NOT NULL,
  page_id INT NOT NULL,
  parent_page_id INT,
  create_time TIMESTAMP default CURRENT_TIME
);

ALTER TABLE page_topology ADD FOREIGN KEY (query_id) REFERENCES query(id);
ALTER TABLE page_topology ADD FOREIGN KEY (page_id) REFERENCES page(id);
ALTER TABLE page_topology ADD FOREIGN KEY (parent_page_id) REFERENCES page(id);

/* Each query should only visit a page once */
ALTER TABLE page_topology ADD UNIQUE(query_id, page_id);

/* Map images to the pages where they can be found */
CREATE TABLE image_page (
  image_id INT NOT NULL,
  page_id INT NOT NULL
);

ALTER TABLE image_page ADD FOREIGN KEY (image_id) REFERENCES image(id);
ALTER TABLE image_page ADD FOREIGN KEY (page_id) REFERENCES page(id);
