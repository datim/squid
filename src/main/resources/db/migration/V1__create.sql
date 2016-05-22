
create table supplier (
    id int not null ,
    width int not null,
    heigth int not null,
    length int not null
);

create table node (
  url varchar(255) not null primary key,
  parent_url varchar(255),
  visited boolean default false
);

/*discovered photos for a node */
create table node_photo (
  url varchar(255) not null primary key,
  node_url varchar(255) not null,
  name varchar(255),
  width int,
  heigth int,
  pinned boolean default false,
  saved boolean default false
);

/* Link to node url */

/* Keep track of downloaded photos */
create table downloaded_photos (
  file_path varchar(255) not null primary key,
  name varchar(255),
  photo_url varchar(255),
  width int,
  height int,
  type varchar(20)
);

/* system preferences table */
create table system_preferences (
  num_columns int default 2,
  page_size int default 100,
  default_display_height int default 500,
  default_display_width int default 200,
);

/* Nodes that are not to be searched */
create table node_exclusion (
  node_url varchar(255)
);

/* User specified filter criteria for photos */
create table photo_filter (
  filter_criteria varchar (255)
);
