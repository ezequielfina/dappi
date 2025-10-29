package com.uade.tg.services;


import com.uade.tg.dto.UpdateUserProfile;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ReviewRepository reviewRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));

    }
    public Optional<User>  updateProfile(Integer userId, UpdateUserProfile request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
            user.setProfilePicture(request.getProfilePicture());
        }
        return Optional.of(userRepository.save(user));
    }

    public List<Review> getMyReviewsOrderedByVotes(Integer userId) {
        // Devolver los reviews con mas votos para mostrarlos en el perfil
        return reviewRepository.findByUserIdOrderByScoreDesc(userId);

    }

}
