package seniorhelper.service;

import seniorhelper.entities.Review;
import seniorhelper.repository.ReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }
}