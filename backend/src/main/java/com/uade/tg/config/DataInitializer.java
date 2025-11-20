package com.uade.tg.config;

import com.uade.tg.entities.Place;
import com.uade.tg.entities.Review;
import com.uade.tg.entities.User;
import com.uade.tg.enums.PlaceCategories;
import com.uade.tg.repositories.PlaceRepository;
import com.uade.tg.repositories.ReviewRepository;
import com.uade.tg.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            PlaceRepository placeRepository,
            ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder

    ) {
        return args -> {

            // =====================
            // 1) Crear usuario dummy
            // =====================
            User demoUser = User.builder()
                    .email("demo@demo.com")
                    .userName("DemoUser")
                    .password(passwordEncoder.encode("123456"))  // ✔ ENCRIPTADA
                    .build();

            userRepository.save(demoUser);

            // =====================
            // 2) Crear PLACES
            // =====================

            Place place1 = Place.builder()
                    .name("Café Central")
                    .full_address("Av. Siempre Viva 123")
                    .latitude(-34.6037)
                    .longitude(-58.3816)
                    .description("Café tradicional con excelente café y pastelería artesanal.")
                    .url("https://images.unsplash.com/photo-1556740767-414a9c4860c1")
                    .placeCategory(String.valueOf(PlaceCategories.CAFE))
                    .build();

            Place place2 = Place.builder()
                    .name("Parque Central")
                    .full_address("Calle Falsa 456")
                    .latitude(-34.6070)
                    .longitude(-58.3820)
                    .description("Gran parque urbano ideal para picnics y actividades al aire libre.")
                    .url("https://images.unsplash.com/photo-1631729779674-1f369e1116b4")
                    .placeCategory(String.valueOf(PlaceCategories.PARQUE))
                    .build();

            Place place3 = Place.builder()
                    .name("Museo de Arte")
                    .full_address("Av. Libertad 789")
                    .latitude(-34.6090)
                    .longitude(-58.3830)
                    .description("Museo con exposiciones clásicas y arte contemporáneo.")
                    .url("https://images.unsplash.com/photo-1603750003385-3342231a1ff1")
                    .placeCategory(String.valueOf(PlaceCategories.MUSEO))
                    .build();

            placeRepository.saveAll(Arrays.asList(place1, place2, place3));

            // =====================
            // 3) Crear REVIEWS
            // =====================

            Review r1 = Review.builder()
                    .description("Excelente café y atención muy amable.")
                    .rateToPlace(5)
                    .photoUrl(null)
                    .user(demoUser)
                    .place(place1)
                    .build();

            Review r2 = Review.builder()
                    .description("Muy lindo parque, ideal para pasar la tarde.")
                    .rateToPlace(4)
                    .photoUrl(null)
                    .user(demoUser)
                    .place(place2)
                    .build();

            Review r3 = Review.builder()
                    .description("Colección increíble, súper recomendado.")
                    .rateToPlace(5)
                    .photoUrl(null)
                    .user(demoUser)
                    .place(place3)
                    .build();

            reviewRepository.saveAll(Arrays.asList(r1, r2, r3));

            System.out.println("=== DATABASE INITIALIZED WITH PLACES + REVIEWS ===");
        };
    }
}
