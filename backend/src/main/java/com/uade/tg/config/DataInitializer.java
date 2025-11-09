package com.uade.tg.config;

import com.uade.tg.entities.Place;
import com.uade.tg.enums.PlaceCategories;
import com.uade.tg.repositories.PlaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.Collections;
@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initPlaces(PlaceRepository placeRepository) {
        return args -> {
            Place place1 = Place.builder()
                    .name("Café Central")
                    .full_address("Av. Siempre Viva 123")
                    .latitude(-34.6037)
                    .longitude(-58.3816)
                    .description("Café Central es un emblemático café tradicional que combina la calidez de un ambiente acogedor " +
                            "con una amplia variedad de cafés de alta calidad, pastelería artesanal y opciones para desayuno y merienda. ")
                    .url("https://images.unsplash.com/photo-1556740767-414a9c4860c1?ixlib=rb-4.1.0&ixid=M3wxMjA3fDF8MHxzZWFyY2h8MXx8Y2FmZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&q=60&w=500")
                    .placeCategory(String.valueOf(PlaceCategories.CAFE))
                    .reviews(Collections.emptyList())
                    .build();

            Place place2 = Place.builder()
                    .name("Parque Central")
                    .full_address("Calle Falsa 456")
                    .latitude(-34.6070)
                    .longitude(-58.3820)
                    .description("El Parque Central es un espacio verde ideal para paseos, picnics y actividades al aire libre. " +
                            "Cuenta con amplias áreas de césped, senderos para caminar o correr, zonas de juegos para niños, " +
                            "y bancos para relajarse mientras se disfruta del entorno natural. Es un lugar muy visitado " )
                    .url("https://images.unsplash.com/photo-1631729779674-1f369e1116b4?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8cGFycXVlJTIwY2VudHJhbHxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&q=60&w=500")
                    .placeCategory(String.valueOf(PlaceCategories.PARQUE))
                    .reviews(Collections.emptyList())
                    .build();

            Place place3 = Place.builder()
                    .name("Museo de Arte")
                    .full_address("Av. Libertad 789")
                    .latitude(-34.6090)
                    .longitude(-58.3830)
                    .description("El Museo de Arte es un espacio cultural dedicado a la exhibición de obras clásicas y contemporáneas. " +
                            "Cuenta con exposiciones permanentes y temporales que incluyen pinturas, esculturas, fotografía y arte digital, " +
                            "proporcionando una experiencia enriquecedora para amantes del arte de todas las edades. ")                    .url("https://images.unsplash.com/photo-1603750003385-3342231a1ff1?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1170")
                    .placeCategory(String.valueOf(PlaceCategories.MUSEO))
                    .reviews(Collections.emptyList())
                    .build();

            placeRepository.saveAll(Arrays.asList(place1, place2, place3));
        };
    }
}