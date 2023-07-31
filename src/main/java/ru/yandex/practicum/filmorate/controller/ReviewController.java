package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/reviews")
@Slf4j
@Validated
public class ReviewController {
    ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping()
    public Review create(@Valid @RequestBody Review review) {
        Review savedReview = reviewService.saveReview(review);
        log.info("Добавлен новый отзыв: {}", savedReview);
        return savedReview;
    }


    @DeleteMapping("{reviewId}")
    public void deleteFilm(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        log.info("Отзыв id = : " + reviewId + " удалён");
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        Review updateReview = reviewService.updateReview(review);
        log.info("Отзыв обновлён : {}", updateReview);
        return updateReview;
    }

    @GetMapping("{reviewId}")
    public Review findReviewById(@PathVariable("reviewId") Long reviewId) {
        return reviewService.findReviewById(reviewId);
    }

    @PutMapping("{reviewId}/like/{userId}")
    public void addLike(@PathVariable("reviewId") Long reviewId,
                        @PathVariable("userId") int userId) {
        reviewService.addLikeDislike(reviewId, userId, true);
        log.info("Пользователь id = : " + userId + " поставил like отзыву id = " + reviewId);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable("reviewId") Long reviewId,
                           @PathVariable("userId") int userId) {
        reviewService.addLikeDislike(reviewId, userId, false);
        log.info("Пользователь id = : " + userId + " поставил dislike отзыву id = " + reviewId);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    public void deleteLike(@PathVariable("reviewId") Long reviewId,
                           @PathVariable("userId") int userId) {
        reviewService.deleteLikeDislike(reviewId, userId, true);
        log.info("Пользователь id = : " + userId + " удалил like отзыву id = " + reviewId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislike(@PathVariable("reviewId") Long reviewId,
                              @PathVariable("userId") int userId) {
        reviewService.deleteLikeDislike(reviewId, userId, false);
        log.info("Пользователь id = : " + userId + " удалил dislike отзыву id = " + reviewId);
    }

    @GetMapping
    public List<Review> findReviewsByFilmId(@RequestParam(required = false) @Min(1) Integer filmId,
                                            @RequestParam(required = false) @Min(1) Integer count) {
        if (Objects.isNull(filmId)) {
            if (Objects.isNull(count)) {
                return reviewService.findAllReviews();
            } else {
                return reviewService.findReviewsWithCount(count);
            }
        } else {
            if (Objects.isNull(count)) {
                return reviewService.findReviewsByFilmId(filmId);
            } else {
                return reviewService.findReviewsByFilmId(filmId, count);
            }
        }
    }
}

