package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping()
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.saveReview(review);
    }

    @DeleteMapping("{reviewId}")
    public void deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("{reviewId}")
    public Review findReviewById(@PathVariable("reviewId") Long reviewId) {
        return reviewService.findReviewById(reviewId);
    }

    @PutMapping("{reviewId}/like/{userId}")
    public void addLike(@PathVariable("reviewId") Long reviewId,
                        @PathVariable("userId") int userId) {
        reviewService.addLikeDislike(reviewId, userId, true);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable("reviewId") Long reviewId,
                           @PathVariable("userId") int userId) {
        reviewService.addLikeDislike(reviewId, userId, false);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    public void deleteLike(@PathVariable("reviewId") Long reviewId,
                           @PathVariable("userId") int userId) {
        reviewService.deleteLikeDislike(reviewId, userId, true);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public void deleteDislike(@PathVariable("reviewId") Long reviewId,
                              @PathVariable("userId") int userId) {
        reviewService.deleteLikeDislike(reviewId, userId, false);
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

