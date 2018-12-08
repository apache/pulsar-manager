CREATE TABLE clusters (
  id INTEGER PRIMARY KEY autoincrement,
  cluser_name varchar(255) NOT NULL
);
CREATE TABLE tenants (
  id INTEGER PRIMARY KEY autoincrement,
  tenant_name varchar(255) NOT NULL
);
CREATE TABLE namespaces (
  id INTEGER PRIMARY KEY autoincrement,
  tenant_id int NOT NULL,
  namespace_name varchar(255) NOT NULL
);
