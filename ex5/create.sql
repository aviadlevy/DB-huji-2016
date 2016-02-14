CREATE TABLE Guest (
	id			INTEGER PRIMARY KEY,
	name		VARCHAR(20) NOT NULL,
	birthYear	INTEGER NOT NULL
	CHECK(birthYear <= 2015)
);

CREATE TABLE Room (
	rnum	INTEGER PRIMARY KEY,
	floor	INTEGER NOT NULL,
	price	INTEGER,
	area	INTEGER default (95) NOT NULL
	CHECK(rnum >= 1 and rnum <=30 and
			floor >= 1 and floor <= 4 and
			((price >= 1000 and area >= 150) or (area < 150 and area > 0 and price > 0)))
);

CREATE TABLE Stayed (
	id		INTEGER,
	rnum	INTEGER,
	UNIQUE(id,rnum),
	FOREIGN KEY (id) REFERENCES Guest(id),
	FOREIGN KEY (rnum) REFERENCES Room(rnum)
	--rnum checked on Room table (because you can't add Stayed rnum when room num is not in already Room)
);
