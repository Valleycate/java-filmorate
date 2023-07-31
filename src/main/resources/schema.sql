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

create table if not exists Review
(
    review_id bigint generated by default as identity primary key,
    film_id bigint not null,
    user_id bigint not null,
    content varchar(2000) not null,
    is_positive boolean,
       CONSTRAINT fk_review_film_id
          FOREIGN KEY(film_id)
          REFERENCES Film (id) ON DELETE CASCADE,
        CONSTRAINT fk_review_user_id
          FOREIGN KEY(user_id)
          REFERENCES Users (id) ON DELETE CASCADE
);

create table if not exists Review_like
(
    review_like_id bigint generated by default as identity primary key,
    review_id     bigint not null,
    user_id    bigint not null,
    like_dislike tinyint,
    CONSTRAINT fk_review_like_film_id
      FOREIGN KEY(review_id)
      REFERENCES Review (review_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_like_user_id
      FOREIGN KEY(user_id)
      REFERENCES Users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX if not exists unique_index_review_like ON Review_like (review_id, user_id);
