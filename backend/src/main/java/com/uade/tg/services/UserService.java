package com.uade.tg.services;


import com.uade.tg.dto.FirstRegisterDTO;
import com.uade.tg.dto.UpdateUserProfileDTO;
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



    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));

    }
    public Optional<User> onBoardingPage(Long userId, FirstRegisterDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setCountry(req.getCountry());
        user.setFavoritePlace(req.getFavoritePlace());
        user.setProfilePicture(req.getProfileImageUrl());

        return Optional.of(userRepository.save(user));
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
            user.setProfilePicture(request.getProfilePicture());
        }
        return Optional.of(userRepository.save(user));
    }


}
