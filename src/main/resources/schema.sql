CREATE TABLE IF NOT EXISTS MPA(
     id INTEGER PRIMARY KEY,
     name varchar(5)
);

CREATE TABLE IF NOT EXISTS Film (
    	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    	name varchar(100) NOT NULL,
    	description varchar(200) NOT NULL,
    	release_date date NOT NULL,
    	duration integer NOT NULL,
    	rating_id integer REFERENCES MPA (id)
);

CREATE TABLE IF NOT EXISTS Users(
    id INTEGER PRIMARY KEY  AUTO_INCREMENT,
    name varchar(100),
    email varchar(100) NOT NULL,
    login varchar(100) NOT NULL,
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS Friendship(
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    PRIMARY KEY (friend_id, user_id),
    FOREIGN KEY (user_id)
        REFERENCES USERS (id)
        ON DELETE CASCADE,
    FOREIGN KEY (friend_id)
        REFERENCES USERS (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Likes(
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (user_id)
        REFERENCES USERS (id)
        ON DELETE CASCADE,
    FOREIGN KEY (film_id)
        REFERENCES FILM (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Genre(
     id INTEGER PRIMARY KEY,
     name varchar(14)
);

CREATE TABLE IF NOT EXISTS Film_genre(
     film_id INTEGER NOT NULL,
     genre_id INTEGER NOT NULL ,
     PRIMARY KEY (film_id, genre_id),
     FOREIGN KEY (film_id)
         REFERENCES FILM (id)
         ON DELETE CASCADE,
     FOREIGN KEY (genre_id)
         REFERENCES GENRE (id)
         ON DELETE CASCADE
);

create table if not exists Review
(
    review_id bigint generated BY DEFAULT AS IDENTITY,
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    content varchar(2000) NOT NULL,
    is_positive boolean,
    PRIMARY KEY (review_id),
    FOREIGN KEY(film_id)
        REFERENCES Film (id)
        ON DELETE CASCADE,
    FOREIGN KEY(user_id)
        REFERENCES Users (id)
        ON DELETE CASCADE
);

create table if not exists Review_like
(
    review_like_id bigint generated BY DEFAULT AS IDENTITY,
    review_id BIGINT NOT NULL,
    user_id INTEGER NOT NULL,
    like_dislike TINYINT,
    PRIMARY KEY (review_like_id),
    FOREIGN KEY(review_id)
        REFERENCES Review (review_id)
        ON DELETE CASCADE,
    FOREIGN KEY(user_id)
        REFERENCES Users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Directors
(
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name varchar(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS Film_Directors
(
    film_id   INTEGER NOT NULL,
    director_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, director_id),
    FOREIGN KEY (director_id)
        REFERENCES Directors (id)
        ON DELETE CASCADE,
    FOREIGN KEY (film_id)
        REFERENCES Film (id)
        ON DELETE CASCADE
);
CREATE UNIQUE INDEX if NOT EXISTS unique_index_review_like ON Review_like (review_id, user_id);
create table if not exists Feed
(
    event_id bigint generated BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    entity_id INTEGER NOT NULL,
    event_type varchar(20) NOT NULL,
    operation varchar(20) NOT NULL,
    timestamp bigint NOT NULL,
    PRIMARY KEY (event_id),
    FOREIGN KEY(user_id)
        REFERENCES Users(id)
        ON DELETE CASCADE
);
