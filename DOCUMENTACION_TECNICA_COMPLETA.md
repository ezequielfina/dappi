# 📱 DAPPI - TravelGuide App
## Documentación Técnica Completa del Proyecto

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Equipo:** Desarrollo de Apps 1 - UADE  
**Estado:** En desarrollo / Demo

---

# 📑 Índice

1. [Visión General del Proyecto](#1-visión-general-del-proyecto)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Backend - Spring Boot](#3-backend---spring-boot)
4. [Frontend - Android](#4-frontend---android)
5. [Base de Datos](#5-base-de-datos)
6. [API REST](#6-api-rest)
7. [Funcionalidades Implementadas](#7-funcionalidades-implementadas)
8. [Flujo de Navegación](#8-flujo-de-navegación)
9. [Guía de Uso](#9-guía-de-uso)
10. [Configuración y Despliegue](#10-configuración-y-despliegue)

---

# 1. Visión General del Proyecto

## 1.1 ¿Qué es DAPPI?

**DAPPI (TravelGuide)** es una aplicación móvil de **turismo social** donde el **usuario es el protagonista**, no solo los lugares. Es una alternativa a Google Maps y TripAdvisor que pone el foco en la **trayectoria del viajero**, su credibilidad y su "marca personal".

### 🎯 Objetivo Principal
Crear una red social enfocada en viajes donde los usuarios puedan:
- ✈️ Explorar lugares turísticos
- ⭐ Dejar reseñas con fotos
- 📍 Validar su presencia mediante GPS
- 👥 Seguir a otros viajeros
- 🏆 Ganar medallas y reconocimientos
- 📊 Construir su perfil viajero

### 🌟 Diferencial Clave
> **"En Google Maps el foco es el lugar. En DAPPI el foco es el viajero."**

---

# 2. Arquitectura del Sistema

## 2.1 Arquitectura General

```
┌──────────────────────────────────────────────────────┐
│                    CLIENTE                            │
│  ┌────────────────────────────────────────────────┐  │
│  │        Android App (Java/XML)                  │  │
│  │  - Activities, Fragments, Adapters             │  │
│  │  - UI/UX Material Design                       │  │
│  │  - Google Maps SDK, Camera, GPS                │  │
│  └────────────────┬───────────────────────────────┘  │
└───────────────────┼──────────────────────────────────┘
                    │ HTTP/REST (JSON + Multipart)
                    │
┌───────────────────▼──────────────────────────────────┐
│                   BACKEND                             │
│  ┌────────────────────────────────────────────────┐  │
│  │     Spring Boot 3.5 (Java 21)                  │  │
│  │  - REST Controllers                            │  │
│  │  - Services (Business Logic)                   │  │
│  │  - Repositories (JPA)                          │  │
│  │  - Security (JWT)                              │  │
│  └────────────────┬───────────────────────────────┘  │
└───────────────────┼──────────────────────────────────┘
                    │ JDBC
                    │
┌───────────────────▼──────────────────────────────────┐
│              BASE DE DATOS                            │
│  ┌────────────────────────────────────────────────┐  │
│  │         PostgreSQL 15+                         │  │
│  │  - users, profiles, places, reviews            │  │
│  │  - review_photos, review_votes, follows        │  │
│  │  - posts, post_photos                          │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│            FILESYSTEM (Almacenamiento)                │
│                 uploads/reviews/                      │
│         (Fotos de reseñas con UUID)                   │
└──────────────────────────────────────────────────────┘
```

## 2.2 Patrón de Arquitectura

### Backend: **MVC + Repository Pattern + Service Layer**
```
Controller → Service → Repository → Database
    ↓          ↓           ↓
   DTO    Business     Entity
          Logic
```

### Android: **MVVM (parcial) + Activities**
```
Activity → ApiClient → Backend
   ↓
  UI (XML)
```

## 2.3 Tecnologías Principales

| Componente | Tecnología | Versión |
|------------|------------|---------|
| **Backend Framework** | Spring Boot | 3.5.7 |
| **Lenguaje Backend** | Java | 21 |
| **Base de Datos** | PostgreSQL | 15+ |
| **ORM** | Hibernate (JPA) | 6.x |
| **Seguridad** | Spring Security + JWT | 6.x |
| **Frontend** | Android Nativo | SDK 36 |
| **Lenguaje Android** | Java | 11 |
| **Build Tool Android** | Gradle (Kotlin DSL) | 8.13.0 |
| **Build Tool Backend** | Maven | 3.x |
| **Mapas** | Google Maps SDK | 19.0.0 |
| **Ubicación** | Google Location Services | 21.3.0 |

---

# 3. Backend - Spring Boot

## 3.1 Estructura del Proyecto Backend

```
backend/
├── src/main/java/com/uade/tg/
│   ├── TgApplication.java              # 🚀 Main class (Entry point)
│   │
│   ├── config/                          # ⚙️ Configuraciones
│   │   ├── ApplicationConfig.java      # Bean de UserDetailsService, PasswordEncoder
│   │   ├── SecurityConfig.java         # Configuración de seguridad HTTP
│   │   ├── JwtService.java             # Generación y validación de JWT
│   │   └── JwtAuthenticationFilter.java # Filtro de autenticación
│   │
│   ├── controllers/                     # 🎮 REST Controllers
│   │   ├── AuthenticationController.java  # Login/Register endpoints
│   │   ├── UserController.java            # Perfil de usuario
│   │   ├── ReviewController.java          # CRUD de reseñas + fotos
│   │   ├── HealthController.java          # Health check
│   │   └── GlobalExceptionHandler.java    # Manejo global de errores
│   │
│   ├── services/                        # 💼 Lógica de negocio
│   │   ├── AuthenticateService.java    # Autenticación y registro
│   │   ├── UserService.java            # Gestión de usuarios
│   │   ├── ReviewService.java          # Gestión de reseñas + validación GPS
│   │   ├── HealthService.java          # Health check
│   │   └── FileStorageService.java     # Almacenamiento de archivos
│   │
│   ├── repositories/                    # 📊 Acceso a datos (JPA)
│   │   ├── UserRepository.java
│   │   ├── PlaceRepository.java
│   │   ├── ReviewRepository.java
│   │   ├── ReviewPhotoRepository.java
│   │   └── ReviewVoteRepository.java
│   │
│   ├── entities/                        # 🗃️ Modelos de datos (JPA Entities)
│   │   ├── User.java                   # Usuario (implementa UserDetails)
│   │   ├── Place.java                  # Lugar turístico
│   │   ├── PlaceType.java              # Tipo de lugar (museo, parque, etc.)
│   │   ├── Review.java                 # Reseña (con GPS)
│   │   ├── ReviewPhoto.java            # Foto de reseña
│   │   └── ReviewVote.java             # Voto up/down en reseña
│   │
│   ├── dto/                             # 📦 Data Transfer Objects
│   │   ├── CreateReviewRequestDTO.java
│   │   ├── ReviewResponseDTO.java
│   │   ├── ReviewPhotoDTO.java
│   │   ├── UpdateUserProfileDTO.java
│   │   └── FirstRegisterDTO.java
│   │
│   └── exceptions/                      # ⚠️ Excepciones personalizadas
│       └── BusinessException.java
│
├── src/main/resources/
│   └── application.properties          # 🔧 Configuración de la app
│
├── uploads/reviews/                    # 📁 Almacenamiento de fotos
│
└── pom.xml                             # 📋 Dependencias Maven
```

## 3.2 Clase Principal (Main)

### **TgApplication.java**
```java
@SpringBootApplication
public class TgApplication {
    public static void main(String[] args) {
        SpringApplication.run(TgApplication.class, args);
    }
}
```

**Función:** Entry point de la aplicación Spring Boot. Inicia el servidor embebido (Tomcat) en el puerto 8080.

## 3.3 Configuración de Seguridad

### **SecurityConfig.java**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Configuración de endpoints públicos/privados
        // JWT filter
        // CORS
    }
}
```

**Endpoints públicos:**
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/authenticate`
- `GET /health`

**Endpoints protegidos (requieren JWT):**
- `POST /api/v1/reviews/**`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/**`

## 3.4 Entidades Principales

### **User.java**
```java
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    
    private String userName;      // Nombre de usuario
    private String email;         // Email (único)
    private String password;      // Contraseña hasheada (BCrypt)
    private String phoneNumber;
    private String country;
    private String favoritePlace;
    private String profilePicture;
    
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;  // Reseñas del usuario
}
```

### **Place.java**
```java
@Entity
@Table(name = "places")
public class Place {
    @Id @GeneratedValue
    private Long id;
    
    private String name;          // Nombre del lugar
    private Double latitude;      // Coordenada GPS
    private Double longitude;     // Coordenada GPS
    private String full_address;
    private String description;
    private Time entryTime;       // Horario de apertura
    private Time endTime;         // Horario de cierre
    private String instagram;
    private String website;
    
    @ManyToOne
    private PlaceType placeType;  // Categoría (museo, parque, etc.)
}
```

### **Review.java**
```java
@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue
    private Long id;
    
    private String description;   // Texto de la reseña
    private Integer rateToPlace;  // Rating 1-5
    private Integer reviewVotes;  // Suma de votos (+1/-1)
    
    // GPS del usuario al crear reseña
    private Double userLatitude;
    private Double userLongitude;
    
    @ManyToOne
    private User user;            // Autor de la reseña
    
    @ManyToOne
    private Place place;          // Lugar reseñado
    
    @OneToMany(mappedBy = "review")
    private List<ReviewPhoto> photos;  // Fotos de la reseña
}
```

### **ReviewPhoto.java**
```java
@Entity
@Table(name = "review_photos")
public class ReviewPhoto {
    @Id @GeneratedValue
    private Long id;
    
    private String filename;      // Nombre original
    private String filePath;      // UUID.jpg
    private String contentType;   // image/jpeg
    private Long fileSize;        // Bytes
    private LocalDateTime uploadDate;
    
    @ManyToOne
    private Review review;        // Reseña asociada
}
```

## 3.5 Servicios Clave

### **ReviewService.java**
```java
@Service
public class ReviewService {
    
    // Crear reseña simple (sin fotos)
    public Review createReview(CreateReviewRequestDTO req) {
        // 1. Validar usuario y lugar existen
        // 2. Calcular distancia GPS (sin validar en modo demo)
        // 3. Crear y guardar reseña
    }
    
    // Crear reseña con fotos
    @Transactional
    public ReviewResponseDTO createReviewWithPhotos(
        CreateReviewRequestDTO req, 
        List<MultipartFile> photos
    ) {
        // 1. Crear reseña
        // 2. Guardar fotos en filesystem
        // 3. Crear registros ReviewPhoto en BD
        // 4. Asociar fotos con reseña
    }
    
    // Calcular distancia con Haversine
    private double calculateDistance(
        double lat1, double lon1, 
        double lat2, double lon2
    ) {
        // Fórmula de Haversine para calcular distancia en metros
    }
}
```

### **FileStorageService.java**
```java
@Service
public class FileStorageService {
    
    private String uploadDir = "uploads/reviews/";
    
    public String storeFile(MultipartFile file) {
        // 1. Validar archivo no vacío
        // 2. Generar nombre único (UUID)
        // 3. Guardar en filesystem
        // 4. Retornar nombre del archivo
    }
}
```

## 3.6 Configuración (application.properties)

```properties
# Aplicación
spring.application.name=tg

# Base de datos PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tg_db
spring.datasource.username=postgres
spring.datasource.password=12345678
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update    # Actualiza esquema automáticamente
spring.jpa.show-sql=true                # Muestra SQL en consola

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB      # Máximo por archivo
spring.servlet.multipart.max-request-size=15MB   # Máximo por request
file.upload-dir=uploads/reviews
```

---

# 4. Frontend - Android

## 4.1 Estructura del Proyecto Android

```
android/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml         # 📋 Manifest de la app
│   │   │
│   │   ├── java/ar/edu/uade/api/
│   │   │   ├── ui/                     # 📱 Activities y UI
│   │   │   │   ├── welcome/
│   │   │   │   │   └── WelcomeActivity.java     # Pantalla inicial
│   │   │   │   ├── login/
│   │   │   │   │   └── LoginActivity.java       # Login
│   │   │   │   ├── register/
│   │   │   │   │   └── RegisterActivity.java    # Registro
│   │   │   │   ├── onboarding/
│   │   │   │   │   └── OnboardingActivity.java  # Tutorial inicial
│   │   │   │   ├── home/
│   │   │   │   │   └── Home.java                # Home (main screen)
│   │   │   │   ├── map/
│   │   │   │   │   └── MapActivity.java         # Mapa con Google Maps
│   │   │   │   ├── places/
│   │   │   │   │   ├── Place.java               # Detalle de lugar
│   │   │   │   │   └── Review.java              # Crear reseña + foto
│   │   │   │   └── user/
│   │   │   │       ├── ProfileActivity.java     # Perfil de usuario
│   │   │   │       ├── ProfileConfigActivity.java
│   │   │   │       ├── FavoritesAdapter.java
│   │   │   │       └── FlagsFavoritesAdapter.java
│   │   │   │
│   │   │   └── network/
│   │   │       └── ApiClient.java       # Cliente HTTP para backend
│   │   │
│   │   └── res/                         # 🎨 Recursos
│   │       ├── layout/                  # XML de pantallas
│   │       │   ├── activity_welcome.xml
│   │       │   ├── activity_login.xml
│   │       │   ├── activity_home.xml
│   │       │   ├── activity_map.xml
│   │       │   ├── activity_place.xml
│   │       │   ├── activity_review.xml  # ⭐ Layout de reseña
│   │       │   └── activity_profile.xml
│   │       │
│   │       ├── drawable/                # Imágenes, iconos, fondos
│   │       ├── values/                  # Colores, strings, estilos
│   │       ├── menu/                    # Bottom navigation menu
│   │       └── xml/
│   │           └── file_paths.xml       # FileProvider config
│   │
│   └── build.gradle.kts                 # 🔧 Dependencias Gradle
│
└── gradle/
    └── libs.versions.toml               # 📦 Versiones centralizadas
```

## 4.2 AndroidManifest.xml

### **Configuración Principal**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ar.edu.uade.api">

    <!-- ============= PERMISOS ============= -->
    
    <!-- Internet para API -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- GPS para validación de ubicación -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- Cámara para fotos de reseñas -->
    <uses-permission android:name="android.permission.CAMERA" />
    
    <!-- Almacenamiento (Android ≤ 12) -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    
    <!-- Feature: Cámara opcional -->
    <uses-feature android:name="android.hardware.camera" 
        android:required="false" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/logo"
        android:label="TravelGuide"
        android:theme="@style/Theme.Api">

        <!-- ============= GOOGLE MAPS API KEY ============= -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="AIzaSyCQi2ewYbxlK8D_ADcrH2TKiLBL6cyQei0" />

        <!-- ============= ACTIVITIES ============= -->
        
        <!-- LAUNCHER: Primera pantalla -->
        <activity
            android:name=".ui.welcome.WelcomeActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Autenticación -->
        <activity android:name=".ui.login.LoginActivity" />
        <activity android:name=".ui.register.RegisterActivity" />
        
        <!-- Onboarding -->
        <activity android:name=".ui.onboarding.OnboardingActivity" />

        <!-- Pantalla principal -->
        <activity android:name=".ui.home.Home" />

        <!-- Mapa -->
        <activity android:name=".ui.map.MapActivity" />

        <!-- Lugares y reseñas -->
        <activity android:name=".ui.places.Place" />
        <activity android:name=".ui.places.Review" />

        <!-- Perfil -->
        <activity android:name=".ui.user.ProfileActivity" />
        <activity android:name=".ui.user.ProfileConfigActivity" />

        <!-- ============= FILE PROVIDER ============= -->
        <!-- Para compartir archivos con la cámara de forma segura -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="ar.edu.uade.api.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>
</manifest>
```

### **Explicación de Componentes:**

#### **Permisos:**
- `INTERNET`: Para conectar con el backend
- `ACCESS_FINE_LOCATION`: GPS preciso para validar ubicación
- `CAMERA`: Para tomar fotos de reseñas
- `WRITE/READ_EXTERNAL_STORAGE`: Para guardar fotos (Android ≤ 12)

#### **Metadata:**
- `com.google.android.geo.API_KEY`: API Key de Google Maps (para desarrollo)

#### **FileProvider:**
- Permite compartir archivos de forma segura entre apps
- Necesario para la cámara en Android 7+
- Authority: `ar.edu.uade.api.fileprovider`

## 4.3 Activity Principal (Home)

### **Home.java**
```java
public class Home extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Personalizar saludo con SharedPreferences
        SharedPreferences prefs = getSharedPreferences("TravelGuidePrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Usuario");
        tvGreeting.setText("Hola, " + username);

        // Click en card de lugar
        findViewById(R.id.place_card_mount).setOnClickListener(v -> {
            startActivity(new Intent(this, Place.class));
        });

        // Click en foto de perfil
        findViewById(R.id.avatar).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Bottom Navigation
        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        bottom.setSelectedItemId(R.id.navigation_home);
        bottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_explore) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
```

**Responsabilidades:**
- 📱 Pantalla principal después del login
- 👋 Muestra saludo personalizado
- 🏠 Cards de lugares destacados
- 📍 Bottom navigation (Home, Explore, Profile)

## 4.4 Activity de Reseñas (Review)

### **Review.java** (Simplificada)
```java
public class Review extends AppCompatActivity {
    
    // UI Components
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button takePhotoButton;
    private ImageView photoPreview;
    private Button submitButton;
    
    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude, userLongitude;
    
    // Photo
    private File photoFile;
    
    // API
    private ApiClient apiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        
        initializeViews();
        apiClient = new ApiClient();
        setupLocationClient();
        setupPhotoLauncher();
        setupClickListeners();
        checkPermissionsAndValidateLocation();
    }
    
    private void setupClickListeners() {
        // Botón tomar foto
        takePhotoButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });
        
        // Botón enviar
        submitButton.setOnClickListener(v -> submitReview());
    }
    
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = createImageFile();
        Uri photoUri = FileProvider.getUriForFile(this,
                "ar.edu.uade.api.fileprovider",
                photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        takePictureLauncher.launch(takePictureIntent);
    }
    
    private void submitReview() {
        // Validaciones
        if (ratingBar.getRating() == 0) {
            Toast.makeText(this, "Selecciona calificación").show();
            return;
        }
        
        // Enviar al backend
        apiClient.sendReviewWithPhoto(
            userId, placeId, 
            reviewEditText.getText().toString(),
            (int) ratingBar.getRating(),
            userLatitude, userLongitude,
            photoFile,
            new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(Review.this, "✅ Reseña enviada").show();
                    finish();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(Review.this, "❌ Error: " + error).show();
                }
            }
        );
    }
}
```

**Funcionalidades:**
- ⭐ Rating de 1-5 estrellas
- 📝 Campo de texto para reseña
- 📷 Captura de foto con cámara nativa
- 🖼️ Vista previa de foto
- 📍 Obtención de GPS (modo demo: sin validación)
- 🚀 Envío multipart al backend

## 4.5 Cliente HTTP (ApiClient)

### **ApiClient.java**
```java
public class ApiClient {
    
    private static final String BASE_URL = "http://10.0.2.2:8080"; // Emulador
    private final ExecutorService executorService;
    
    public void sendReviewWithPhoto(
        long userId, long placeId, String description, 
        int rating, double latitude, double longitude,
        File photoFile, ApiCallback callback
    ) {
        executorService.execute(() -> {
            try {
                // 1. Crear conexión multipart/form-data
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                URL url = new URL(BASE_URL + "/api/v1/reviews/with-photos");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", 
                    "multipart/form-data; boundary=" + boundary);
                
                DataOutputStream outputStream = new DataOutputStream(
                    connection.getOutputStream());
                
                // 2. Agregar JSON de la reseña
                JSONObject reviewJson = new JSONObject();
                reviewJson.put("userId", userId);
                reviewJson.put("placeId", placeId);
                reviewJson.put("description", description);
                reviewJson.put("rateToPlace", rating);
                reviewJson.put("userLatitude", latitude);
                reviewJson.put("userLongitude", longitude);
                
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"review\"\r\n");
                outputStream.writeBytes("Content-Type: application/json\r\n\r\n");
                outputStream.writeBytes(reviewJson.toString());
                outputStream.writeBytes("\r\n");
                
                // 3. Agregar foto si existe
                if (photoFile != null && photoFile.exists()) {
                    outputStream.writeBytes("--" + boundary + "\r\n");
                    outputStream.writeBytes("Content-Disposition: form-data; " +
                        "name=\"photos\"; filename=\"" + photoFile.getName() + "\"\r\n");
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    
                    FileInputStream fileInputStream = new FileInputStream(photoFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                    outputStream.writeBytes("\r\n");
                }
                
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
                outputStream.close();
                
                // 4. Procesar respuesta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess("Success");
                } else {
                    callback.onError("Error " + responseCode);
                }
                
            } catch (Exception e) {
                callback.onError("Error: " + e.getMessage());
            }
        });
    }
    
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
```

**Características:**
- 🌐 Conexión HTTP manual (sin bibliotecas externas)
- 📤 Soporte multipart/form-data
- 📦 Envío de JSON + archivos
- ⚡ Ejecución asíncrona (ExecutorService)
- 🔄 Callbacks para respuestas

## 4.6 Dependencias (build.gradle.kts)

```kotlin
dependencies {
    // Android Core
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Navigation
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    
    // Google Maps y Location
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
```

---

# 5. Base de Datos

## 5.1 Diagrama Entidad-Relación (DER)

```
┌──────────┐         ┌───────────┐
│  users   │◄────────│ profiles  │
└────┬─────┘         └─────┬─────┘
     │                     │
     │                     │
     │ 1:N                 │ 1:N
     │                     │
     ▼                     ▼
┌──────────┐         ┌───────────┐
│ reviews  │────────►│   posts   │
└────┬─────┘         └─────┬─────┘
     │                     │
     │ 1:N                 │ 1:N
     │                     │
     ▼                     ▼
┌──────────┐         ┌───────────┐
│review_   │         │post_      │
│photos    │         │photos     │
└──────────┘         └───────────┘

     │
     │ N:1
     │
     ▼
┌──────────┐         ┌───────────┐
│ places   │◄────────│place_types│
└──────────┘         └───────────┘

┌──────────┐
│review_   │
│votes     │
└──────────┘
     ▲
     │ N:1
     │
┌────┴─────┐
│ reviews  │
└──────────┘

┌──────────┐
│ follows  │  (relación muchos a muchos entre users)
└──────────┘
```

## 5.2 Tablas Principales

### **users**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(45) UNIQUE NOT NULL,
    email VARCHAR(45) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(45),
    country VARCHAR(45) NOT NULL,
    favorite_place VARCHAR(45) NOT NULL,
    profile_picture VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **places**
```sql
CREATE TABLE places (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    id_place_type INTEGER REFERENCES place_types(id),
    complete_address VARCHAR(100),
    description VARCHAR(200),
    entry_time TIME,
    end_time TIME,
    phone_number VARCHAR(45),
    website VARCHAR(100)
);
```

### **reviews**
```sql
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(500) UNIQUE NOT NULL,
    rate_to_place INTEGER CHECK (rate_to_place BETWEEN 1 AND 5),
    review_votes INTEGER DEFAULT 0 NOT NULL,
    user_latitude DOUBLE PRECISION,   -- GPS del usuario
    user_longitude DOUBLE PRECISION,  -- GPS del usuario
    user_id BIGINT NOT NULL REFERENCES users(id),
    place_id BIGINT NOT NULL REFERENCES places(id),
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **review_photos**
```sql
CREATE TABLE review_photos (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,      -- UUID.jpg
    content_type VARCHAR(50),             -- image/jpeg
    file_size BIGINT,                     -- bytes
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_review BIGINT NOT NULL REFERENCES reviews(id)
);
```

### **review_votes**
```sql
CREATE TABLE review_votes (
    id BIGSERIAL PRIMARY KEY,
    value INTEGER NOT NULL CHECK (value IN (-1, 1)),  -- +1 o -1
    id_profile INTEGER NOT NULL REFERENCES profiles(id),
    id_review BIGINT NOT NULL REFERENCES reviews(id),
    UNIQUE(id_profile, id_review)  -- Un voto por usuario por reseña
);
```

---

# 6. API REST

## 6.1 Endpoints Disponibles

### **Autenticación** (`/api/v1/auth`)

#### `POST /api/v1/auth/register`
Registrar nuevo usuario.

**Request:**
```json
{
  "userName": "juan123",
  "email": "juan@example.com",
  "password": "password123",
  "phoneNumber": "+54911234567"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /api/v1/auth/authenticate`
Login de usuario.

**Request:**
```json
{
  "userName": "juan@example.com",
  "password": "password123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### **Usuarios** (`/api/v1/users`)

#### `GET /api/v1/users/me`
Obtener perfil del usuario autenticado.

**Headers:**
```
Authorization: Bearer {JWT_TOKEN}
```

**Response 200:**
```json
{
  "id": 1,
  "userName": "juan123",
  "email": "juan@example.com",
  "country": "Argentina",
  "favoritePlace": "Bariloche"
}
```

---

### **Reseñas** (`/api/v1/reviews`)

#### `POST /api/v1/reviews`
Crear reseña simple (sin fotos).

**Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request:**
```json
{
  "userId": 1,
  "placeId": 5,
  "description": "Excelente lugar para visitar",
  "rateToPlace": 5,
  "userLatitude": -34.6037,
  "userLongitude": -58.3816
}
```

**Response 200:**
```json
{
  "id": 42,
  "description": "Excelente lugar para visitar",
  "rateToPlace": 5,
  "reviewVotes": 0,
  "userId": 1,
  "placeId": 5
}
```

#### `POST /api/v1/reviews/with-photos`
Crear reseña con fotos.

**Headers:**
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data
```

**Form Data:**
- `review` (JSON string):
```json
{
  "userId": 1,
  "placeId": 5,
  "description": "Excelente lugar",
  "rateToPlace": 5,
  "userLatitude": -34.6037,
  "userLongitude": -58.3816
}
```
- `photos` (file): imagen.jpg

**Response 200:**
```json
{
  "id": 42,
  "description": "Excelente lugar",
  "rateToPlace": 5,
  "reviewVotes": 0,
  "userId": 1,
  "userName": "juan@example.com",
  "placeId": 5,
  "placeName": "Mount Bromo",
  "photos": [
    {
      "id": 1,
      "filename": "imagen.jpg",
      "filePath": "a1b2c3d4-e5f6-7890.jpg",
      "contentType": "image/jpeg",
      "fileSize": 524288
    }
  ],
  "userLatitude": -34.6037,
  "userLongitude": -58.3816
}
```

#### `GET /api/v1/reviews/places/{placeId}`
Obtener todas las reseñas de un lugar.

**Response 200:**
```json
[
  {
    "id": 42,
    "description": "Excelente lugar",
    "rateToPlace": 5,
    "reviewVotes": 3
  },
  {
    "id": 43,
    "description": "Muy bonito",
    "rateToPlace": 4,
    "reviewVotes": 1
  }
]
```

#### `GET /api/v1/reviews/users`
Obtener todas las reseñas del usuario autenticado.

#### `POST /api/v1/reviews/{reviewId}/votes/up`
Votar positivo una reseña (+1).

#### `POST /api/v1/reviews/{reviewId}/votes/down`
Votar negativo una reseña (-1).

#### `DELETE /api/v1/reviews/{reviewId}`
Eliminar reseña (solo el autor).

---

# 7. Funcionalidades Implementadas

## 7.1 Autenticación y Seguridad

### ✅ **JWT (JSON Web Tokens)**
- Token generado al login/register
- Validez: configurable (default 24h)
- Almacenamiento: SharedPreferences en Android
- Envío: Header `Authorization: Bearer {token}`

### ✅ **BCrypt Password Hashing**
- Contraseñas hasheadas con BCrypt
- Nunca se almacenan en texto plano
- Salt generado automáticamente

### ✅ **Endpoints Protegidos**
- Filtro JWT intercepta requests
- Valida token en cada request
- Extrae usuario del token

---

## 7.2 Gestión de Lugares

### ✅ **Exploración de Lugares**
- Lista de lugares turísticos
- Filtrado por categoría (museo, parque, restaurante)
- Información detallada:
  - 📍 Ubicación GPS
  - 🕒 Horarios de apertura
  - 📷 Fotos
  - 📝 Descripción
  - 🌐 Sitio web / Instagram

### ✅ **Google Maps Integración**
- Mapa interactivo con marcadores
- Ubicación actual del usuario
- Vista del lugar en el mapa
- Navegación al lugar

---

## 7.3 Sistema de Reseñas

### ✅ **Crear Reseñas**
- Rating de 1-5 estrellas (obligatorio)
- Texto descriptivo (obligatorio)
- Foto opcional (captura con cámara)
- Ubicación GPS automática

### ✅ **Captura de Fotos**
- Cámara nativa del dispositivo
- Vista previa inmediata
- Almacenamiento temporal
- Envío al backend
- Guardado en filesystem con UUID

### ✅ **Validación GPS** (Deshabilitada en Modo Demo)
- Obtención de ubicación actual
- Cálculo de distancia con Haversine
- Radio máximo: 500 metros
- **Modo Demo:** Guardado sin validación

### ✅ **Sistema de Votos**
- Upvote (+1) / Downvote (-1)
- Un voto por usuario por reseña
- Contador acumulativo de votos
- Prevención de múltiples votos

---

## 7.4 Perfil de Usuario

### ✅ **Perfil Viajero**
- Foto de perfil
- Información personal
- País de origen
- Lugar favorito
- Contador de lugares visitados
- Lista de reseñas publicadas

### ✅ **Configuración**
- Editar perfil
- Cambiar foto de perfil
- Actualizar información

---

## 7.5 Navegación

### ✅ **Bottom Navigation**
- **Home:** Pantalla principal con lugares destacados
- **Explore:** Mapa interactivo con lugares
- **Profile:** Perfil del usuario

### ✅ **Flujo de Pantallas**
```
WelcomeActivity
    ├─► LoginActivity → Home
    └─► RegisterActivity → OnboardingActivity → Home

Home
    ├─► Place → Review (crear reseña)
    ├─► MapActivity (ver mapa)
    └─► ProfileActivity → ProfileConfigActivity
```

---

# 8. Flujo de Navegación

## 8.1 Flujo Completo de Usuario

```
┌─────────────────────────────────────────────────────────┐
│ 1. PRIMERA VEZ                                          │
└─────────────────────────────────────────────────────────┘
         │
         ▼
    ┌─────────┐
    │ Welcome │  (Pantalla inicial)
    └────┬────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
 ┌──────┐  ┌────────┐
 │Login │  │Register│
 └───┬──┘  └───┬────┘
     │         │
     │         ▼
     │    ┌────────────┐
     │    │Onboarding  │ (Tutorial)
     │    └─────┬──────┘
     │          │
     └──────────┴───────┐
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│ 2. HOME (Pantalla Principal)                            │
└─────────────────────────────────────────────────────────┘
                        │
                        │
           ┌────────────┼────────────┐
           │            │            │
           ▼            ▼            ▼
      ┌────────┐   ┌────────┐   ┌────────┐
      │ Home   │   │Explore │   │Profile │
      │        │   │ (Map)  │   │        │
      └───┬────┘   └────────┘   └───┬────┘
          │                          │
          ▼                          ▼
      ┌────────┐               ┌─────────────┐
      │ Place  │               │ProfileConfig│
      │(Detail)│               └─────────────┘
      └───┬────┘
          │
          ▼
      ┌────────┐
      │ Review │
      │(Create)│
      └────────┘
```

## 8.2 Flujo de Crear Reseña (Detallado)

```
1. Usuario en Place Activity
   │
   ▼
2. Toca botón "DEJAR RESEÑA"
   │
   ▼
3. Se abre Review Activity
   │
   ▼
4. Activity solicita permisos (GPS + Cámara)
   │
   ├─► Permiso GPS concedido ────► Obtiene ubicación actual
   │                                │
   │                                ▼
   │                         Muestra: "ℹ️ Modo Demo"
   │
   └─► Permiso GPS denegado ─────► Usa ubicación del lugar
   │
   ▼
5. Usuario interactúa con UI:
   │
   ├─► Selecciona rating (1-5 estrellas)
   │
   ├─► Escribe texto de reseña
   │
   └─► (Opcional) Toca "📷 TOMAR FOTO"
       │
       ├─► Permiso cámara concedido ──► Abre cámara nativa
       │                                  │
       │                                  ▼
       │                           Usuario toma foto
       │                                  │
       │                                  ▼
       │                           Muestra vista previa
       │
       └─► Permiso cámara denegado ───► Toast de error
   │
   ▼
6. Usuario toca "ENVIAR"
   │
   ▼
7. Validaciones:
   │
   ├─► ¿Rating seleccionado? ─NO─► Toast: "Selecciona calificación"
   │    │
   │    SÍ
   │    │
   ├─► ¿Texto escrito? ──────NO──► Toast: "Escribe tu reseña"
   │    │
   │    SÍ
   │    │
   └─► [MODO DEMO] No valida ubicación
   │
   ▼
8. Prepara datos:
   │
   ├─► JSON: { userId, placeId, description, rating, lat, lng }
   │
   └─► (Opcional) File: foto.jpg
   │
   ▼
9. ApiClient.sendReviewWithPhoto()
   │
   ├─► Crea conexión HTTP multipart/form-data
   │
   ├─► Envía JSON como "review"
   │
   ├─► Envía archivo como "photos"
   │
   └─► Espera respuesta del backend
   │
   ▼
10. Backend procesa:
    │
    ├─► Valida datos
    │
    ├─► Guarda reseña en BD
    │
    ├─► (Si hay foto) Guarda archivo con UUID
    │
    ├─► (Si hay foto) Crea registro review_photo
    │
    └─► Retorna ReviewResponseDTO
    │
    ▼
11. Callback en Android:
    │
    ├─► onSuccess() ──► Toast: "✅ Reseña enviada"
    │                   │
    │                   ▼
    │                finish() (cierra Activity)
    │
    └─► onError() ───► Toast: "❌ Error: ..."
                       │
                       ▼
                 Re-habilita botón ENVIAR
```

---

# 9. Guía de Uso

## 9.1 Para Usuarios

### **Registro:**
1. Abrir app → Pantalla "Welcome"
2. Tocar "REGISTRARSE"
3. Completar:
   - Nombre de usuario
   - Email
   - Contraseña
   - Teléfono
4. Tocar "REGISTRARSE"
5. Tutorial de onboarding
6. ¡Listo para usar!

### **Explorar Lugares:**
1. En Home, ver lugares destacados
2. Tocar un card de lugar
3. Ver información detallada
4. Opciones:
   - 📍 Ver en mapa
   - 📝 Dejar reseña
   - 📖 Ver reseñas de otros

### **Crear Reseña:**
1. En detalle de lugar → "DEJAR RESEÑA"
2. Seleccionar estrellas (1-5)
3. Escribir experiencia
4. (Opcional) Tocar "📷 TOMAR FOTO"
5. Tomar foto con cámara
6. Ver preview
7. Tocar "ENVIAR"
8. ✅ Reseña publicada

### **Ver Mapa:**
1. Bottom navigation → Ícono de mapa
2. Ver lugares marcados
3. Tocar marcador → Ver info
4. Navegar al lugar

### **Ver Perfil:**
1. Bottom navigation → Ícono de perfil
2. Ver información personal
3. Ver lista de reseñas publicadas
4. Editar perfil (ícono editar)

---

## 9.2 Para Desarrolladores

### **Setup Backend:**
```bash
# 1. Clonar repositorio
git clone <repo-url>
cd dappi/backend

# 2. Configurar PostgreSQL
createdb tg_db
# Editar src/main/resources/application.properties

# 3. Instalar dependencias y compilar
./mvnw clean install

# 4. Ejecutar
./mvnw spring-boot:run

# Backend disponible en http://localhost:8080
```

### **Setup Android:**
```bash
# 1. Abrir proyecto en Android Studio
# Abrir dappi/android/

# 2. Sincronizar Gradle
# File → Sync Project with Gradle Files

# 3. Configurar API URL
# Si usas dispositivo físico, cambiar BASE_URL en ApiClient.java
# De: http://10.0.2.2:8080 (emulador)
# A:  http://<TU_IP>:8080 (físico)

# 4. Ejecutar
# Run → Run 'app' (o Shift+F10)
```

### **Crear APK:**
```bash
cd dappi/android
./gradlew assembleDebug

# APK generado en:
# app/build/outputs/apk/debug/app-debug.apk
```

---

# 10. Configuración y Despliegue

## 10.1 Variables de Entorno

### **Backend (application.properties)**
```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/tg_db
spring.datasource.username=postgres
spring.datasource.password=12345678

# JPA
spring.jpa.hibernate.ddl-auto=update  # Producción: validate

# File upload
spring.servlet.multipart.max-file-size=10MB
file.upload-dir=uploads/reviews

# JWT (agregar en futuro)
jwt.secret=tu-secreto-super-seguro
jwt.expiration=86400000  # 24 horas
```

### **Android**
- **BASE_URL**: Cambiar en `ApiClient.java`
  - Emulador: `http://10.0.2.2:8080`
  - Físico: `http://<IP_PC>:8080`
  - Producción: `https://api.dappi.com`

- **Google Maps API Key**: En `AndroidManifest.xml`
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="TU_API_KEY_AQUI" />
  ```

## 10.2 Modo Demo vs Producción

### **Modo Demo (Actual):**
- ✅ Validación GPS deshabilitada
- ✅ Botón ENVIAR siempre habilitado
- ✅ Mensaje "ℹ️ Modo Demo"
- ✅ Funciona desde cualquier ubicación

### **Para Producción:**

**Backend:**
```java
// ReviewService.java
// Descomentar MAX_DISTANCE_METERS
private static final double MAX_DISTANCE_METERS = 500;

// En createReview(), restaurar validación:
if (distance > MAX_DISTANCE_METERS) {
    throw new IllegalStateException(
        "Debes estar a menos de 500 metros del lugar"
    );
}
```

**Android:**
```java
// Review.java
// Cambiar getLocationForDemo() por validateUserLocation()
// Restaurar lógica de habilitar/deshabilitar submitButton

// En layout XML, cambiar mensaje:
<TextView
    android:text="📍 Validando ubicación..." />
```

---

# 📊 Resumen Técnico

## Stack Completo

| Capa | Tecnología |
|------|------------|
| **Frontend** | Android Java 11 + Material Design 3 |
| **Backend** | Spring Boot 3.5 + Java 21 |
| **Base de Datos** | PostgreSQL 15+ |
| **ORM** | Hibernate/JPA |
| **Seguridad** | Spring Security + JWT |
| **Mapas** | Google Maps SDK |
| **Sensores** | Camera API + GPS (FusedLocationProvider) |
| **HTTP** | HttpURLConnection (nativo) |
| **Build** | Maven (Backend) + Gradle Kotlin DSL (Android) |

## Métricas del Proyecto

```
📂 Total de archivos:       ~200
📝 Líneas de código:        ~15,000
🗃️ Tablas en BD:            10
🔌 Endpoints API:           15+
📱 Activities Android:      10
🎨 Layouts XML:             13
🌍 Idiomas soportados:      Español
👥 Usuarios concurrentes:   N/A (en desarrollo)
```

## Checklist de Funcionalidades

- ✅ Autenticación (Login/Register)
- ✅ JWT Security
- ✅ CRUD Usuarios
- ✅ CRUD Reseñas
- ✅ Captura de fotos
- ✅ Almacenamiento de archivos
- ✅ Validación GPS (implementada, deshabilitada en demo)
- ✅ Google Maps integración
- ✅ Sistema de votos
- ✅ Perfil de usuario
- ✅ Bottom navigation
- ⏳ Feed social
- ⏳ Sistema de seguidores
- ⏳ Medallas y achievements
- ⏳ Búsqueda de lugares
- ⏳ Filtros por categoría

---

# 📞 Información Adicional

## Documentación Relacionada

- 📄 `CAMERA_FEATURE_IMPLEMENTATION.md` - Implementación de cámara y GPS
- 🎭 `MODO_DEMO.md` - Configuración de modo demo
- 🚀 `INSTRUCCIONES_PRUEBA.md` - Guía de testing
- 📋 `RESUMEN_IMPLEMENTACION.md` - Resumen ejecutivo
- 🎨 `UI_IMPROVEMENTS.md` - Mejoras de UI/UX

## Repositorio Git

```bash
git remote -v
# origin  <URL_DEL_REPO>
```

## Convenciones de Código

- **Java Backend:** Camel case, 4 espacios
- **Java Android:** Camel case, 4 espacios
- **SQL:** Snake case (user_name, review_votes)
- **REST:** Kebab case en URLs cuando aplique

## Contacto del Equipo

**Proyecto:** DAPPI - TravelGuide  
**Materia:** Desarrollo de Aplicaciones 1  
**Universidad:** UADE  
**Fecha:** Noviembre 2025  

---

**© 2025 DAPPI Team - UADE**  
**Versión:** 1.0.0  
**Última actualización:** Noviembre 2025

