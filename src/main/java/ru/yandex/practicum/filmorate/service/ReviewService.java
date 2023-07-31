package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.DAO.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.ReviewLikeDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewLikeDbStorage reviewLikeStorage;


    public ReviewService(ReviewDbStorage reviewStorage, UserService userService, FilmService filmService, ReviewLikeDbStorage reviewLikeStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
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
            throw new NotFoundException();
        }
    }

    public Review saveReview(Review review) {
        //проверка что айди фильма и юзера существуют
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        enrichReviewsByUseful(review);
        return reviewStorage.saveReview(review);
    }

    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        //айди фильма и юзера не могут поменяться при обновлении
        enrichReviewsByUseful(review);
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long reviewId) {
        findReviewById(reviewId);
        reviewStorage.deleteReview(reviewId);

    }

    public List<Review> findReviewsByFilmId(int filmId, Integer count) {
        filmService.findFilmById(filmId);
        List<Review> reviews = reviewStorage.findReviewsByFilmId(filmId).stream()
                .limit(count)
                .collect(Collectors.toList());
        enrichReviewsByUseful(reviews);
        return reviews;
    }

    public List<Review> findReviewsByFilmId(int filmId) {
        filmService.findFilmById(filmId);
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
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) { //проверяем, что от этого юзера нет лайка или дизлайка
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //добавляем лайк
            } else if (!whoLikeReview.contains(userId) && whoDislikeReview.contains(userId)) { //если есть дизлайк, то
                deleteLikeDislike(reviewId, userId, false); //удаляем дизлайк
                reviewLikeStorage.addLikeDislike(reviewId, userId, true); //ставим лайк
            }
        }
        if (!isLike) { // прилетел дизлайк
            if (!whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                reviewLikeStorage.addLikeDislike(reviewId, userId, false);
            } else if (whoLikeReview.contains(userId) && !whoDislikeReview.contains(userId)) {
                deleteLikeDislike(reviewId, userId, true); //удаляем лайк
                reviewLikeStorage.addLikeDislike(reviewId, userId, false); //ставим дизлайк
            }
        }
    }

    public void deleteLikeDislike(Long reviewId, int userId, Boolean isLike) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        //при удалении лайка/дизлайка, которого нет, ничего не происходит, ошибка не выдаётся
        reviewLikeStorage.deleteLikeDislike(reviewId, userId, isLike);
    }
}
