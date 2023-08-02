package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.DAO.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewLikeDbStorage reviewLikeStorage;


    public ReviewService(ReviewDbStorage reviewStorage, UserService userService, FilmService filmService, UserStorage userStorage, FilmStorage filmStorage, ReviewLikeDbStorage reviewLikeStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.reviewLikeStorage = reviewLikeStorage;
    }


    public List<Review> findAllReviews() {
        List<Review> reviews = reviewStorage.getAllReviews();
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    private void enrichReviewsByUseful(List<Review> allReviews) {
        allReviews.forEach(review -> review.setUseful(
                Optional.ofNullable(reviewLikeStorage.sumLikeDislike(review.getReviewId())).orElse(0)));
        allReviews.sort(Comparator.comparingInt(Review::getUseful).reversed());
    }

    private void enrichReviewsByUseful(Review review) {
        review.setUseful(Optional.ofNullable(reviewLikeStorage.sumLikeDislike(review.getReviewId())).orElse(0));
    }


    public Review findReviewById(Long reviewId) {
        Review reviewById = reviewStorage.findReviewById(reviewId);
        if (Objects.nonNull(reviewById)) {
            enrichReviewsByUseful(reviewById);
            return reviewById;
        } else {
            log.error("Отзыв не найден id = {}", reviewId);
            throw new BadRequest("нет отзыва с таким id");
        }
    }

    public Review saveReview(Review review) {
        //проверка что айди фильма и юзера существуют
        userStorage.findUserById(review.getUserId());
        filmStorage.findFilmById(review.getFilmId());
        enrichReviewsByUseful(review);
        Review savedReview = reviewStorage.saveReview(review);
        log.info("Добавлен новый отзыв: {}", savedReview);
        return savedReview;

    }

    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        //айди фильма и юзера не могут поменяться при обновлении
        enrichReviewsByUseful(review);
        Review updateReview = reviewStorage.updateReview(review);
        log.info("Отзыв обновлён : {}", updateReview);
        return updateReview;
    }

    public void deleteReview(Long reviewId) {
        findReviewById(reviewId);
        reviewStorage.deleteReview(reviewId);
        log.info("Отзыв id = : " + reviewId + " удалён");

    }

    public List<Review> findReviewsByFilmId(int filmId, Integer count) {
        filmStorage.findFilmById(filmId);
        List<Review> reviews = reviewStorage.findReviewsByFilmId(filmId).stream()
                .limit(count)
                .collect(Collectors.toList());
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    public List<Review> findReviewsByFilmId(int filmId) {
        filmStorage.findFilmById(filmId);
        List<Review> reviewsByFilmId = reviewStorage.findReviewsByFilmId(filmId);
        enrichReviewsByUseful(reviewsByFilmId);
        return reviewsByFilmId;
    }

    public List<Review> findReviewsWithCount(Integer count) {
        List<Review> reviews = findAllReviews().stream()
                .limit(count)
                .collect(Collectors.toList());
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    public void addLikeDislike(Long reviewId, int userId, Boolean isLike) {
        findReviewById(reviewId);
        userStorage.findUserById(userId);
        List<Integer> whoLikeReview = reviewLikeStorage.whoLikeReview(reviewId);
        List<Integer> whoDislikeReview = reviewLikeStorage.whoDislikeReview(reviewId);
        if (isLike) { //прилетел лайк
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) { //проверяем, что от этого юзера нет лайка или дизлайка
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //добавляем лайк
                log.info("Пользователь id = : " + userId + " поставил like отзыву id = " + reviewId);
            } else if (!whoLikeReview.contains(userId) && whoDislikeReview.contains(userId)) { //если есть дизлайк, то
                deleteLikeDislike(reviewId, userId, false); //удаляем дизлайк
                log.info("Пользователь id = : " + userId + " удалил dislike отзыву id = " + reviewId);
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //ставим лайк
                log.info("Пользователь id = : " + userId + " поставил like отзыву id = " + reviewId);
            }
        }
        if (!isLike) { // прилетел дизлайк
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                reviewLikeStorage.addLikeDislike(reviewId, userId, false);
                log.info("Пользователь id = : " + userId + " поставил dislike отзыву id = " + reviewId);
            } else if (whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                deleteLikeDislike(reviewId, userId, true); //удаляем лайк
                log.info("Пользователь id = : " + userId + " удалил like отзыву id = " + reviewId);
                reviewLikeStorage.addLikeDislike(reviewId, userId, false); //ставим дизлайк
                log.info("Пользователь id = : " + userId + " поставил dislike отзыву id = " + reviewId);
            }
        }
    }

    public void deleteLikeDislike(Long reviewId, int userId, Boolean isLike) {
        findReviewById(reviewId);
        userStorage.findUserById(userId);
        //при удалении лайка/дизлайка, которого нет, ничего не происходит, ошибка не выдаётся
        if (isLike) {
            log.info("Пользователь id = : " + userId + " удалил like отзыву id = " + reviewId);
            reviewLikeStorage.deleteLikeDislike(reviewId, userId, true);
        } else {
            log.info("Пользователь id = : " + userId + " удалил dislike отзыву id = " + reviewId);
            reviewLikeStorage.deleteLikeDislike(reviewId, userId, false);
        }

    }
}
