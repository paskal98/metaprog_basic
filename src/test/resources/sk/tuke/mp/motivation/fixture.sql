-- Testing database structure and data
-- noinspection SqlNoDataSourceInspectionForFile

create table Department (
  id integer primary key autoincrement,
  name varchar(255),
  code varchar(10)
);

create table Employee (
  id integer primary key autoincrement,
  name varchar(255),
  surname varchar(255),
  salary integer,
  department integer,
  foreign key (department) references Department(id)
);

insert into Department(name, code)
  values ("Development", "DEV");

insert into Department(name, code)
  values ("Operations", "OPS");

insert into Employee(name, surname, salary, department)
  values ("Janko", "Hrasko", 1000, 1);

insert into Employee(name, surname, salary, department)
  values ("Jozko", "Mrkvicka", 1200, 2);
