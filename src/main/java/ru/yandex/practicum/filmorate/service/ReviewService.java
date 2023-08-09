package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.service.util.FeedSaver;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.DAO.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.storage.ReviewLikeDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final ReviewLikeDbStorage reviewLikeStorage;
    private final FeedSaver feedSaver;

    public List<Review> findAllReviews() {
        List<Review> reviews = reviewStorage.getAllReviews();
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    private void enrichReviewsByUseful(List<Review> allReviews) {
        allReviews.forEach(this::enrichReviewByUseful);
        allReviews.sort(Comparator.comparingInt(Review::getUseful).reversed());
    }

    private void enrichReviewByUseful(Review review) {
        review.setUseful(Optional.ofNullable(reviewLikeStorage.sumLikeDislike(review.getReviewId())).orElse(0));
    }

    public Review findReviewById(Long reviewId) {
        Review reviewById = reviewStorage.findReviewById(reviewId);
        if (Objects.nonNull(reviewById)) {
            enrichReviewByUseful(reviewById);
            return reviewById;
        } else {
            log.error("Отзыв не найден id = {}", reviewId);
            throw new BadRequest("нет отзыва с таким id");
        }
    }

    public Review saveReview(Review review) {
        //проверка что айди фильма и юзера существуют
        userService.findUserById(review.getUserId());
        filmStorage.findFilmById(review.getFilmId());

        Review savedReview = reviewStorage.saveReview(review);
        log.info("Добавлен новый отзыв: {}", savedReview);
        feedSaver.saveFeed(savedReview.getUserId(), savedReview.getReviewId(), EnumEventType.REVIEW, EnumOperation.ADD);
        enrichReviewByUseful(savedReview);
        return savedReview;
    }

    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        //айди фильма и юзера не могут поменяться при обновлении
        Review updateReview = reviewStorage.updateReview(review);
        log.info("Отзыв обновлён : {}", updateReview);
        feedSaver.saveFeed(updateReview.getUserId(), updateReview.getReviewId(), EnumEventType.REVIEW, EnumOperation.UPDATE);
        enrichReviewByUseful(updateReview);
        return updateReview;
    }

    public void deleteReview(Long reviewId) {
        Review reviewById = findReviewById(reviewId);
        feedSaver.saveFeed(reviewById.getUserId(), reviewId, EnumEventType.REVIEW, EnumOperation.REMOVE);
        reviewStorage.deleteReview(reviewId);
        log.info("Отзыв id: {} удалён", reviewId);
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
        userService.findUserById(userId);
        List<Integer> whoLikeReview = reviewLikeStorage.whoLikeReview(reviewId);
        List<Integer> whoDislikeReview = reviewLikeStorage.whoDislikeReview(reviewId);
        if (isLike) { //прилетел лайк
            if (isNotExistLikeOrDislike(userId, whoLikeReview, whoDislikeReview)) { //проверяем, что от этого юзера нет лайка или дизлайка
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //добавляем лайк
                log.info("Пользователь id: {} поставил like отзыву id: {}", userId, reviewId);
            } else if (!whoLikeReview.contains(userId) && whoDislikeReview.contains(userId)) { //если есть дизлайк, то
                deleteAndAddLikeDislike(reviewId, userId, true, "like");
            }
        }
        if (!isLike) { // прилетел дизлайк
            if (isNotExistLikeOrDislike(userId, whoLikeReview, whoDislikeReview)) {
                reviewLikeStorage.addLikeDislike(reviewId, userId, false);
                log.info("Пользователь id: {} поставил dislike отзыву id: {}", userId, reviewId);
            } else if (whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) { //если есть лайк, то
                deleteAndAddLikeDislike(reviewId, userId, false, "dislike");
            }
        }
    }

    private void deleteAndAddLikeDislike(Long reviewId, int userId, boolean isLike, String nameOfAdd) {
        deleteLikeDislike(reviewId, userId, !isLike); //удаляем дизлайк/лайк. Логирование в методе delete
        reviewLikeStorage.addLikeDislike(reviewId, userId, isLike); //ставим лайк/дизлайк
        log.info("Пользователь id: {} поставил {} отзыву id: {}", userId, nameOfAdd, reviewId);
    }

    private boolean isNotExistLikeOrDislike(int userId, List<Integer> whoLikeReview, List<Integer> whoDislikeReview) {
        return !whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId);
    }

    public void deleteLikeDislike(Long reviewId, int userId, Boolean isLike) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        //при удалении лайка/дизлайка, которого нет, ничего не происходит, ошибка не выдаётся
        if (isLike) {
            log.info("Пользователь id: {} удалил like отзыву id: {}", userId, reviewId);
            reviewLikeStorage.deleteLikeDislike(reviewId, userId);
        } else {
            log.info("Пользователь id: {} удалил dislike отзыву id: {}", userId, reviewId);
            reviewLikeStorage.deleteLikeDislike(reviewId, userId);
        }
    }
}
