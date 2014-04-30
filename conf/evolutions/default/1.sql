# --- !Ups

CREATE TABLE category (
	id serial NOT NULL,
	name text NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (name)
);

CREATE TABLE expense (
	id serial NOT NULL,
	date date DEFAULT NULL,
	id_category integer DEFAULT NULL,
	description text DEFAULT NULL,
	amount real NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (id_category) REFERENCES category (id) ON DELETE SET NULL ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE expense;
DROP TABLE category;
