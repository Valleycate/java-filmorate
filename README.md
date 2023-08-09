# java-filmorate

## Это репозиторий проекта "filmorate"

#### Также здесь лежит схема хранения в реляционной базе данных

![БД filmorate.png](src/БД%20Filmorate.png)
Наше приложение **умеет**:

1. Добавлять пользователей в друзья.
2. Отмечать понравившееся фильмы.
3. Добавлять удалять фильмы и пользователей.
4. И многое другое)

#### Таблицы

1. FILM - содержит в себе всю информацию по фильмам. 
2. FILM_GENRE - содержит в себе перечень всех жанров фильма по всем фильмам таблицы. Так же имеет связь с таблицей
   Genre (многие к одному)
3. GENRE - Перечень всевозможных жанров
4. LIKES содержит в себе список id пользователей которые отметили фильм 
5. USERS - содержит в себе всю информацию по пользователям .
6. FRIENDSHIP - список всех потвержденных и непотвержденных друзей соответствено
7. MPA - Таблица рейтинга фильмов
8. REVIEW - содержит в себе обзоры фильмов
9. REVIEW_LIKE - содержит в себе оценки обзоров
10. FEED - содержит  в себе действие пользователей
11. DIRECTOR - содержит в себе перечень режисеров фильмов
12. FILM_DIRECTOR - содержит в себе связи между филмами и режисерами
   Примеры запросов в SQL
   Выборка всех популярных фильмов с ограничением:

```sql
SELECT F.name,
       COUNT(ID) as pop
FROM LIKES
        LEFT JOIN FILM F on F.ID = LIKES.FILM_ID 
GROUP BY name
ORDER BY pop DESC
LIMIT 10
```

Получение общих друзей по id2, id1:

```sql
SELECT ID, NAME, LOGIN, BIRTHDAY,EMAIL FROM USERS
    WHERE ID IN 
          (SELECT F1.FRIEND_ID FROM FRIENDSHIP AS F1
             INNER JOIN FRIENDSHIP AS F2 ON F1.FRIEND_ID = F2.FRIEND_ID
             WHERE F1.USER_ID = :USERID AND F2.USER_ID = :FRIEND_ID
          )          
```
#### Над функционалом работали

**Плотникова Мария** - "Общие фильмы", "Рекоммендации"

**Лоренц Кристина** - "Отзывы", "Лента событий" и "Поиск"

**Галеев Александр** - "Добавление режиссёров в фильмы"

**Мустафин Артём** - "Удаление фильмов и пользователей", "Вывод самых популярных фильмов по жанру и годам"



