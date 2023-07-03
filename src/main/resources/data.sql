DELETE From FRIENDSHIP WHERE user_id > -1;
DELETE From LIKES  WHERE film_id > -1;
DELETE From LIKES  WHERE user_id > -1;
DELETE FROM FILM_GENRE WHERE film_id > -1;
DELETE FROM FILM_GENRE WHERE genre_id > -1;
DELETE From USERS  WHERE ID > -1;
DELETE From Film  WHERE ID > -1;
DELETE From MPA  WHERE ID > -1;
DELETE From Genre  WHERE ID > -1;


INSERT INTO MPA (id, name)
VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

INSERT INTO Genre (id, name)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');