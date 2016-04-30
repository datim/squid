
create table supplier (
    id int not null,
    width int not null,
    heigth int not null,
    length int not null
);

create table urlNode (
  id int not null primary key,
  url varchar(100) not null,
  parent varchar(100),
  visted boolean
);

create table nodePhoto (
  id int not null primary key,
  url varchar(100) not null,
  name varchar(100),
  size int
);
