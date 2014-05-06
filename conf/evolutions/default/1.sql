# --- !Ups

CREATE TABLE category (
	id serial NOT NULL,
	name text NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (name)
);

CREATE TABLE expense (
	id serial NOT NULL,
	date date NOT NULL,
	id_category integer NOT NULL,
	description text DEFAULT NULL,
	amount numeric(7, 2) NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (id_category) REFERENCES category (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

# --- !Downs

DROP TABLE expense;
DROP TABLE category;
