package com.uade.tg.services;


import com.uade.tg.dto.FirstRegisterDTO;
import com.uade.tg.dto.ReviewResponseDTO;
import com.uade.tg.dto.UpdateUserProfileDTO;
import com.uade.tg.dto.UserMeDTO;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ReviewService reviewService;

    public UserService(UserRepository userRepository, ReviewService reviewService) {
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }


    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));

    }

    public UserMeDTO findUserDtoByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Maneja el caso en que el usuario no existe (aunque no es la causa del NPE actual)
            return null;
        }

        User user = userOptional.get();

        // El método de servicio ahora puede devolver null
        ReviewResponseDTO reviewResponseDTO = reviewService.getReviewMostUpByUser(user.getId());

        int cantResenas = reviewService.getReviewsByUser(user.getId()).size();
        Integer sumUpvotes = reviewService.getSumUpvotesByUserId(user.getId());

        return UserMeDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .resenasRealizadas(cantResenas)
                .upvotes(sumUpvotes)
                .profilePictureUrl(user.getProfilePictureUrl())
                // Si reviewResponseDTO es null, se establece como null aquí
                .reviewMasVotada(reviewResponseDTO)
                .build();
    }

    public Optional<User> onBoardingPage(Long userId, FirstRegisterDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setCountry(req.getCountry());
        user.setFavoritePlace(req.getFavoritePlace());
        user.setProfilePictureUrl(req.getProfileImageUrl());

        userRepository.save(user);
        return Optional.of(user);
    }

    public Optional<User>  updateProfile(Long userId, UpdateUserProfileDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getUserName() != null) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            user.setProfilePictureUrl( request.getProfilePicture());
        }
        return Optional.of(userRepository.save(user));
    }


}
