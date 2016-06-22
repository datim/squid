
/* table for nodes */
create table node (
  id int not null primary key auto_increment,
  url varchar(255) not null,
  parent_url varchar(255),
  visited boolean default false
);

/* add constraints to node */
alter table node add UNIQUE (url);

/*discovered photos for a node */
create table node_photo (
  id int not null primary key auto_increment,
  url varchar(255) not null ,
  node_url varchar(255) not null,
  name varchar(255),
  base_url varchar(255),
  width int,
  heigth int,
  pinned boolean default false,
  saved boolean default false
);

/* add constraints to node_photo */
alter table node_photo add UNIQUE (url);

/* There should be a unique name for every website */
alter table node_photo add UNIQUE (name, base_url);

/* Keep track of downloaded photos */
create table downloaded_photos (
  file_path varchar(255) not null primary key,
  name varchar(255),
  photo_url varchar(255),
  width int,
  height int,
  type varchar(20)
);

/* Represent the status of a search */
create table search_status (
    id int not null primary key auto_increment,
    search_url varchar(255) not null,
    node_count int not null,
    max_depth int not null,
    image_count int not null,
    status varchar(20),
);

/* add constraints to search_status */
alter table search_status add UNIQUE (search_url);

/* contains all users parameters. There will only be one record per user */
create table user_parameters (
  id int not null primary key auto_increment,
  user_id int not null,
  search_url varchar(255),
  search_filter varchar(100),
  max_node_count int not null,
  max_image_count int not null,
  log_path varchar(255),
  save_path varchar (255)
);

alter table user_parameters add UNIQUE (user_id);
