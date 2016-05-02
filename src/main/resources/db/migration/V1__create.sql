
create table supplier (
    id int not null ,
    width int not null,
    heigth int not null,
    length int not null
);

create table url_node (
  id int not null primary key auto_increment,
  url varchar(255) not null,
  parent_node int,
  visited boolean
);

alter table url_node add foreign key (parent_node) references url_node(id);

create table node_photo (
  id int not null primary key auto_increment,
  url varchar(255) not null,
  name varchar(255),
  size int
);
