CREATE TABLE IF NOT EXISTS MPA(
     id INTEGER PRIMARY KEY,
     name varchar(5)
);

CREATE TABLE IF NOT EXISTS Film (
    	id INTEGER PRIMARY KEY,
    	name varchar(100) NOT NULL,
    	description varchar(200) NOT NULL,
    	release_date date NOT NULL,
    	duration integer NOT NULL,
    	rating_id integer REFERENCES MPA (id)
);

CREATE TABLE IF NOT EXISTS Users(
    id INTEGER PRIMARY KEY,
    name varchar(100),
    email varchar(100) NOT NULL,
    login varchar(100) NOT NULL,
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship(
    user_id INTEGER REFERENCES Users (id),
    friend_id INTEGER REFERENCES Users (id),
    name varchar(11)
);

CREATE TABLE IF NOT EXISTS Likes(
    film_id INTEGER REFERENCES Film (id),
    user_id INTEGER REFERENCES Users (id)
);

CREATE TABLE IF NOT EXISTS Genre(
     id INTEGER PRIMARY KEY,
     name varchar(14)
);

CREATE TABLE IF NOT EXISTS Film_genre(
     film_id INTEGER REFERENCES Film (id),
     genre_id INTEGER REFERENCES Genre (id)
);
CREATE TABLE IF NOT EXISTS director(
    director_id INTEGER PRIMARY KEY,
    name varchar(1000)
);
CREATE TABLE IF NOT EXISTS film_director(
    film_director_id INTEGER PRIMARY KEY,
    director_id INTEGER REFERENCES director(director_id),
    film_id INTEGER REFERENCES Film (id)
);

CREATE TABLE IF NOT EXISTS feed(
    feed_id INTEGER PRIMARY KEY,
    event_id INTEGER,
    user_id INTEGER REFERENCES Users (id),
    entity_id INTEGER,
    event_type ENUM("FRIEND", "REVIEW", "LIKE"),
    operation ENUM("ADD", "REMOVE", "UPDATE"),
    feed_time TIMESTAMP
);
