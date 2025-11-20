package com.uade.tg.services;

import com.uade.tg.dto.CreateReviewRequestDTO;
import com.uade.tg.dto.ReviewPhotoDTO;
import com.uade.tg.dto.ReviewResponseDTO;
import com.uade.tg.entities.Place;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.ReviewPhoto;
import com.uade.tg.entities.ReviewVote;
import com.uade.tg.entities.User;
import com.uade.tg.repositories.PlaceRepository;
import com.uade.tg.repositories.ReviewPhotoRepository;
import com.uade.tg.repositories.ReviewRepository;
import com.uade.tg.repositories.ReviewVoteRepository;
import com.uade.tg.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final FileStorageService fileStorageService;

    public ReviewService(ReviewRepository reviewRepository, 
                        UserRepository userRepository, 
                        PlaceRepository placeRepository, 
                        ReviewVoteRepository reviewVoteRepository,
                        ReviewPhotoRepository reviewPhotoRepository,
                        FileStorageService fileStorageService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
        this.reviewVoteRepository = reviewVoteRepository;
        this.reviewPhotoRepository = reviewPhotoRepository;
        this.fileStorageService = fileStorageService;
    }

    public Review createReview(CreateReviewRequestDTO req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Place place = placeRepository.findById(req.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));

        if (req.getUserLatitude() != null && req.getUserLongitude() != null) {
            double distance = calculateDistance(
                req.getUserLatitude(), 
                req.getUserLongitude(),
                place.getLatitude(), 
                place.getLongitude()
            );
            log.info("Usuario a {} metros del lugar (validación deshabilitada para demo)", distance);
        }

        Review review = Review.builder()
                .user(user)
                .place(place)
                .description(req.getDescription())
                .rateToPlace(req.getRateToPlace())
                .reviewVotes(0)
                .userLatitude(req.getUserLatitude())
                .userLongitude(req.getUserLongitude())
                .photos(new ArrayList<>())
                .build();

        return reviewRepository.save(review);
    }
    
    @Transactional
    public ReviewResponseDTO createReviewWithPhotos(CreateReviewRequestDTO req, List<MultipartFile> photos) {
        Review review = createReview(req);
        
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String filename = fileStorageService.storeFile(photo);
                    
                    ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                            .filename(photo.getOriginalFilename())
                            .filePath(filename)
                            .contentType(photo.getContentType())
                            .fileSize(photo.getSize())
                            .review(review)
                            .build();
                    
                    reviewPhotoRepository.save(reviewPhoto);
                    review.getPhotos().add(reviewPhoto);
                }
            }
        }
        
        return convertToDTO(review);
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    private ReviewResponseDTO convertToDTO(Review review) {
        List<ReviewPhotoDTO> photoDTOs = review.getPhotos().stream()
                .map(photo -> ReviewPhotoDTO.builder()
                        .id(photo.getId())
                        .filename(photo.getFilename())
                        .filePath(photo.getFilePath())
                        .contentType(photo.getContentType())
                        .fileSize(photo.getFileSize())
                        .build())
                .collect(Collectors.toList());
        
        User user = review.getUser();
        Place place = review.getPlace();
        
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .description(review.getDescription())
                .rateToPlace(review.getRateToPlace())
                .reviewVotes(review.getReviewVotes())
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getUsername() : "")
                .placeId(place != null ? place.getId() : null)
                .placeName(place != null ? place.getName() : "")
                .photos(photoDTOs)
                .userLatitude(review.getUserLatitude())
                .userLongitude(review.getUserLongitude())
                .build();
    }

    public List<Review> getReviewsByPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado"));

        return reviewRepository.findByPlace(place);
    }

    public List<Review> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return reviewRepository.findAllReviewsByUser(user.getId());
    }

    public Review upVoteReview(Long userId,Long reviewId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        boolean alreadyVoted = reviewVoteRepository.existsByUserIdAndReviewId(currentUser.getId(), reviewId);
        if (alreadyVoted) {
            throw new IllegalStateException("Ya votaste esta review");
        }

        ReviewVote vote = new ReviewVote();
        vote.setUser(currentUser);
        vote.setReview(review);
        vote.setValue(1);
        reviewVoteRepository.save(vote);

        review.setReviewVotes(review.getReviewVotes() + 1);
        return reviewRepository.save(review);
    }

    public Review downVoteReview(Long userId, Long reviewId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review no encontrada"));

        boolean alreadyVoted = reviewVoteRepository.existsByUserIdAndReviewId(currentUser.getId(), reviewId);
        if (alreadyVoted) {
            throw new IllegalStateException("Ya votaste esta review");
        }

        ReviewVote vote = new ReviewVote();
        vote.setUser(currentUser);
        vote.setReview(review);
        vote.setValue(-1);
        reviewVoteRepository.save(vote);

        review.setReviewVotes(review.getReviewVotes() - 1);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review no encontrada"));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("No podés borrar una review de otro usuario");
        }

        reviewRepository.delete(review);
    }


}