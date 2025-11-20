# Preguntas y Respuestas - Proyecto Travel Guide (DAPPI)

## Índice
1. [Arquitectura y Diseño General](#arquitectura-y-diseño-general)
2. [Backend - Spring Boot](#backend---spring-boot)
3. [Android - Aplicación Móvil](#android---aplicación-móvil)
4. [Base de Datos](#base-de-datos)
5. [Seguridad](#seguridad)
6. [Funcionalidad Offline](#funcionalidad-offline)
7. [APIs y Servicios](#apis-y-servicios)
8. [Patrones de Diseño](#patrones-de-diseño)
9. [Testing y Calidad](#testing-y-calidad)

---

## Arquitectura y Diseño General

### P1: ¿Qué arquitectura utiliza el proyecto?
**R:** El proyecto utiliza una arquitectura **Cliente-Servidor** con:
- **Backend:** API REST desarrollada en Spring Boot (Java)
- **Frontend:** Aplicación móvil nativa Android
- **Base de Datos:** PostgreSQL (relacional)
- **Comunicación:** HTTP/HTTPS mediante JSON

La aplicación sigue el patrón **MVC (Model-View-Controller)** en el backend y una arquitectura por capas en Android.

---

### P2: ¿Cómo se comunican el frontend y el backend?
**R:** La comunicación se realiza mediante:
- **Protocolo:** HTTP/HTTPS
- **Formato:** JSON para intercambio de datos
- **Librería Android:** Retrofit2 para consumir la API REST
- **Autenticación:** Bearer Token (JWT) en los headers
- **Endpoints:** `/api/v1/` como prefijo base

Ejemplo de llamada:
```java
@GET("places/{placeId}")
Call<PlaceResponse> getPlaceById(@Path("placeId") Long placeId);
```

---

### P3: ¿Por qué eligieron una aplicación nativa de Android en lugar de una híbrida?
**R:** Elegimos Android nativo por:
- **Rendimiento:** Mejor performance y fluidez
- **Acceso completo a APIs:** GPS, cámara, almacenamiento local
- **Experiencia de usuario:** Material Design nativo
- **Funcionalidad offline:** Room Database y SQLite
- **Control total:** Sobre el ciclo de vida y recursos del dispositivo

---

## Backend - Spring Boot

### P4: ¿Qué es Spring Boot y por qué se utilizó?
**R:** Spring Boot es un framework de Java que simplifica el desarrollo de aplicaciones empresariales. Se utilizó porque:
- **Autoconfiguración:** Reduce configuración manual
- **Embedded Server:** Tomcat embebido, no requiere servidor externo
- **Inyección de dependencias:** Gestión automática con @Autowired
- **Ecosistema completo:** Spring Security, JPA, Data, etc.
- **Producción ready:** Métricas, health checks integrados

---

### P5: Explique el flujo de una petición HTTP en el backend
**R:** El flujo es:

1. **Cliente** envía petición HTTP → `http://api/v1/places/1`
2. **JwtAuthenticationFilter** intercepta y valida el token JWT
3. **SecurityConfig** verifica permisos del endpoint
4. **Controller** (`@RestController`) recibe la petición
5. **Service** (`@Service`) ejecuta la lógica de negocio
6. **Repository** (`@Repository`) accede a la base de datos vía JPA
7. **Entity** (modelo) representa los datos
8. **DTO** transforma la respuesta
9. **Controller** devuelve ResponseEntity con JSON

```java
@GetMapping("/places/{id}")
public ResponseEntity<PlaceDTO> getPlace(@PathVariable Long id) {
    Place place = placeService.findById(id);
    return ResponseEntity.ok(PlaceDTO.from(place));
}
```

---

### P6: ¿Qué son los DTOs y por qué se usan?
**R:** **DTO (Data Transfer Object)** es un patrón que define objetos para transferir datos entre capas.

**Ventajas:**
- **Seguridad:** No exponer entidades completas con datos sensibles
- **Desacoplamiento:** La estructura interna no se expone al cliente
- **Optimización:** Solo se transfieren campos necesarios
- **Versionado:** Facilita mantener compatibilidad de APIs

**Ejemplo:**
```java
@Data
@Builder
public class ReviewResponseDTO {
    private Long id;
    private String description;
    private Integer rateToPlace;
    // No incluye password del usuario ni datos internos
}
```

---

### P7: ¿Cómo funciona JPA/Hibernate en el proyecto?
**R:** JPA (Java Persistence API) con Hibernate como implementación:

**Características:**
- **ORM:** Mapea objetos Java a tablas SQL
- **Anotaciones:** `@Entity`, `@Table`, `@Column`, `@ManyToOne`
- **Repository Pattern:** Interfaces que extienden JpaRepository
- **Consultas:** Métodos derivados o @Query personalizadas
- **Transacciones:** `@Transactional` gestiona commits/rollbacks

**Ejemplo:**
```java
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
```

---

### P8: ¿Qué relaciones existen en la base de datos?
**R:** El modelo tiene las siguientes relaciones:

1. **User → Review** (1:N)
   - Un usuario puede tener muchas reseñas
   
2. **Place → Review** (1:N)
   - Un lugar puede tener muchas reseñas

3. **User → ReviewVote** (1:N)
   - Un usuario puede votar muchas reseñas

4. **Review → ReviewVote** (1:N)
   - Una reseña puede tener muchos votos

5. **Review → ReviewPhoto** (1:N)
   - Una reseña puede tener varias fotos

**Restricciones:**
- `UNIQUE(user_id, review_id)` en `review_votes` (un usuario solo puede votar una vez por reseña)

---

## Android - Aplicación Móvil

### P9: ¿Qué es el ciclo de vida de una Activity en Android?
**R:** El ciclo de vida de una Activity tiene los siguientes estados:

1. **onCreate()**: Se crea la Activity, se inicializan vistas
2. **onStart()**: La Activity se vuelve visible
3. **onResume()**: La Activity está en primer plano, interactuable
4. **onPause()**: Otra Activity toma el foco (llamada entrante)
5. **onStop()**: La Activity ya no es visible
6. **onDestroy()**: Se destruye la Activity

**En el proyecto:**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityProfileBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    sessionManager = SessionManager.getInstance(this);
    loadUserProfile();
}
```

---

### P10: ¿Qué es View Binding y por qué se usa?
**R:** **View Binding** genera automáticamente clases de enlace para cada layout XML.

**Ventajas:**
- **Seguridad de tipos:** No más ClassCastException
- **Null safety:** Detecta vistas nulas en compile-time
- **Performance:** Más rápido que findViewById
- **Código limpio:** Sin IDs mágicos

**Uso:**
```java
// Habilitar en build.gradle
buildFeatures {
    viewBinding true
}

// En Activity
private ActivityProfileBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
    binding = ActivityProfileBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    binding.tvUsername.setText("Usuario");
}
```

---

### P11: ¿Qué es Retrofit y cómo funciona?
**R:** **Retrofit** es una librería para consumir APIs REST de forma type-safe.

**Características:**
- **Anotaciones:** Define endpoints con @GET, @POST, etc.
- **Conversión automática:** JSON ↔ Objetos Java (con Gson)
- **Callbacks:** Asíncronos con `enqueue()`
- **Interceptores:** Para logging, autenticación

**Implementación:**
```java
// 1. Definir interfaz
public interface ReviewApi {
    @GET("reviews/places/{placeId}")
    Call<List<ReviewResponse>> getReviewsByPlace(@Path("placeId") Long placeId);
}

// 2. Crear instancia
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://api.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();

ReviewApi api = retrofit.create(ReviewApi.class);

// 3. Hacer llamada
api.getReviewsByPlace(1L).enqueue(new Callback<>() {
    @Override
    public void onResponse(Call call, Response response) {
        List<ReviewResponse> reviews = response.body();
    }
});
```

---

### P12: ¿Qué es Room y cómo se implementó?
**R:** **Room** es una librería de persistencia que proporciona una capa de abstracción sobre SQLite.

**Componentes:**
1. **@Entity**: Define tabla
2. **@Dao**: Define operaciones (Data Access Object)
3. **@Database**: Clase abstracta que conecta todo

**Implementación en el proyecto:**
```java
// Entity
@Entity(tableName = "reviews")
public class ReviewEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String description;
    private boolean synced; // Para offline
}

// DAO
@Dao
public interface ReviewDao {
    @Insert
    long insert(ReviewEntity review);
    
    @Query("SELECT * FROM reviews WHERE synced = 0")
    List<ReviewEntity> getPendingReviews();
}

// Database
@Database(entities = {ReviewEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReviewDao reviewDao();
}
```

---

### P13: ¿Cómo funcionan los RecyclerView en el proyecto?
**R:** **RecyclerView** muestra listas eficientemente reciclando vistas.

**Componentes:**
1. **Adapter**: Convierte datos en vistas
2. **ViewHolder**: Contiene referencias a las vistas
3. **LayoutManager**: Define cómo se disponen los items

**Implementación:**
```java
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<ReviewResponse> reviews;
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        RatingBar ratingBar;
        
        ViewHolder(View view) {
            super(view);
            tvDescription = view.findViewById(R.id.tv_description);
            ratingBar = view.findViewById(R.id.rating_bar);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewResponse review = reviews.get(position);
        holder.tvDescription.setText(review.getDescription());
        holder.ratingBar.setRating(review.getRateToPlace());
    }
}
```

---

### P14: ¿Qué es el patrón Repository en Android?
**R:** El **Repository Pattern** centraliza el acceso a datos de múltiples fuentes.

**Ventajas:**
- **Única fuente de verdad:** UI no sabe de dónde vienen los datos
- **Abstracción:** Esconde implementación (API, DB, cache)
- **Fácil testing:** Se puede mockear
- **Manejo de offline:** Combina datos locales y remotos

**Implementación:**
```java
public class ReviewRepository {
    private final ReviewApi api;
    private final ReviewDao dao;
    private final NetworkManager networkManager;
    
    public void getReviews(Long placeId, ReviewListCallback callback) {
        if (networkManager.isConnected()) {
            // Obtener de API
            api.getReviewsByPlace(placeId).enqueue(new Callback<>() {
                public void onResponse(...) {
                    List<ReviewResponse> reviews = response.body();
                    // Guardar en cache
                    dao.insertAll(reviews);
                    callback.onSuccess(reviews);
                }
            });
        } else {
            // Obtener de cache local
            List<ReviewEntity> cachedReviews = dao.getReviewsByPlace(placeId);
            callback.onLocalData(cachedReviews);
        }
    }
}
```

---

### P15: ¿Cómo se manejan los permisos en Android?
**R:** Los permisos se declaran en `AndroidManifest.xml` y se solicitan en runtime para permisos peligrosos.

**Tipos de permisos:**
- **Normal**: Se otorgan automáticamente (INTERNET)
- **Dangerous**: Requieren aprobación del usuario (CAMERA, LOCATION)

**Implementación:**
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

```java
// Solicitar en runtime
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    != PackageManager.PERMISSION_GRANTED) {
    
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.CAMERA},
        REQUEST_CAMERA_PERMISSION);
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, 
                                       int[] grantResults) {
    if (requestCode == REQUEST_CAMERA_PERMISSION) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}
```

---

## Base de Datos

### P16: ¿Cómo está diseñada la base de datos?
**R:** La base de datos PostgreSQL tiene el siguiente diseño:

**Tablas principales:**
1. **users**: id, userName, email, password, profilePictureUrl, country, favoritePlace
2. **places**: id, name, full_address, latitude, longitude, description, url, placeCategory
3. **reviews**: id, description, rateToPlace, reviewVotes, photoUrl, createdAt, user_id, place_id
4. **review_votes**: id, user_id, review_id, voteType (UPVOTE/DOWNVOTE)
5. **review_photos**: id, filename, filePath, contentType, fileSize, review_id

**Claves foráneas:**
- reviews.user_id → users.id
- reviews.place_id → places.id
- review_votes.user_id → users.id
- review_votes.review_id → reviews.id

---

### P17: ¿Qué índices tiene la base de datos y por qué?
**R:** Los índices mejoran el rendimiento de las consultas:

1. **PRIMARY KEY**: Automático en todos los IDs
2. **UNIQUE(user_id, review_id)** en review_votes: Evita votos duplicados
3. **INDEX en reviews.place_id**: Consultas frecuentes por lugar
4. **INDEX en reviews.user_id**: Consultas de reseñas por usuario
5. **INDEX en reviews.reviewVotes**: Ordenamiento por popularidad

**Ventajas:**
- Búsquedas O(log n) en lugar de O(n)
- JOIN más eficientes
- ORDER BY más rápidos

---

### P18: ¿Cómo se gestionan las transacciones?
**R:** Con la anotación **@Transactional** de Spring:

```java
@Transactional
public ReviewResponseDTO createReviewWithPhotos(CreateReviewRequestDTO req, 
                                                 List<MultipartFile> photos) {
    // 1. Crear review
    Review review = createReview(req);
    
    // 2. Guardar fotos
    for (MultipartFile photo : photos) {
        String filename = fileStorageService.storeFile(photo);
        ReviewPhoto reviewPhoto = new ReviewPhoto();
        reviewPhoto.setReview(review);
        reviewPhotoRepository.save(reviewPhoto);
    }
    
    // Si algo falla, se hace ROLLBACK automático
    return convertToDTO(review);
}
```

**Propiedades ACID:**
- **Atomicidad**: Todo o nada
- **Consistencia**: Mantiene integridad referencial
- **Aislamiento**: Transacciones no interfieren
- **Durabilidad**: Cambios persistentes

---

## Seguridad

### P19: ¿Cómo funciona la autenticación JWT?
**R:** **JWT (JSON Web Token)** es un token que contiene información codificada.

**Flujo:**
1. Usuario se registra/loguea con email/password
2. Backend valida credenciales
3. Backend genera JWT firmado con clave secreta
4. Cliente guarda el token (SharedPreferences)
5. Cliente envía token en header: `Authorization: Bearer {token}`
6. Backend valida firma y extrae información

**Estructura JWT:**
```
header.payload.signature

eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.signature
```

**Implementación:**
```java
// Generar token
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
}

// Validar token
public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUserName(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}
```

---

### P20: ¿Cómo se protegen las contraseñas?
**R:** Las contraseñas se hashean con **BCrypt** antes de guardarlas:

```java
@Configuration
public class ApplicationConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// Al registrar
String hashedPassword = passwordEncoder.encode(plainPassword);
user.setPassword(hashedPassword);

// Al autenticar
boolean matches = passwordEncoder.matches(plainPassword, user.getPassword());
```

**Características BCrypt:**
- **Salt aleatorio**: Previene rainbow tables
- **Adaptive**: Se puede aumentar el costo
- **One-way**: No se puede revertir
- **Lento**: Dificulta fuerza bruta

---

### P21: ¿Qué es Spring Security y cómo se configuró?
**R:** Spring Security maneja autenticación y autorización.

**Configuración:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()  // Público
                .anyRequest().authenticated()                     // Protegido
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

**Flujo:**
1. Llega petición
2. JwtAuthenticationFilter valida token
3. Si válido, crea Authentication en SecurityContext
4. SecurityFilterChain verifica permisos
5. Si autorizado, ejecuta endpoint

---

### P22: ¿Cómo se previenen ataques comunes?
**R:** El proyecto implementa protecciones contra:

**1. SQL Injection:**
- JPA con prepared statements
```java
@Query("SELECT r FROM Review r WHERE r.user.id = ?1")
List<Review> findAllReviewsByUser(Long userId);
```

**2. XSS (Cross-Site Scripting):**
- Validación de entrada
- Escape de datos en frontend

**3. CSRF (Cross-Site Request Forgery):**
- Deshabilitado porque usamos JWT (stateless)
```java
.csrf(csrf -> csrf.disable())
```

**4. Path Traversal:**
```java
if (filename.contains("..")) {
    throw new BusinessException("Nombre de archivo inválido");
}
```

**5. Brute Force:**
- Rate limiting en el servidor
- Account lockout después de X intentos

---

## Funcionalidad Offline

### P23: ¿Cómo funciona el modo offline?
**R:** La app puede funcionar sin conexión usando:

**1. Detección de conectividad:**
```java
public class NetworkManager {
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
```

**2. Cache local con Room:**
- Todas las reseñas se guardan en SQLite
- Se consulta primero la cache
- Se actualiza cuando hay conexión

**3. Cola de sincronización:**
```java
public void createReview(...) {
    ReviewEntity review = new ReviewEntity();
    
    if (networkManager.isConnected()) {
        // Enviar a backend
        sendToBackend(review);
    } else {
        // Guardar local con synced = false
        review.setSynced(false);
        database.reviewDao().insert(review);
    }
}

public void syncPendingReviews() {
    List<ReviewEntity> pending = dao.getPendingReviews();
    for (ReviewEntity review : pending) {
        sendToBackend(review);
        review.setSynced(true);
        dao.update(review);
    }
}
```

---

### P24: ¿Qué pasa cuando se recupera la conexión?
**R:** Al recuperar conexión:

1. **BroadcastReceiver** detecta cambio de conectividad
2. Se ejecuta `syncPendingReviews()`
3. Se envían todas las reseñas con `synced = false`
4. Si se envían exitosamente, se marca `synced = true`
5. Se actualizan datos desde el servidor

```java
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkManager.getInstance(context).isConnected()) {
            ReviewRepository repo = ReviewRepository.getInstance(context);
            repo.syncPendingReviews(new SyncCallback() {
                public void onComplete(int synced, int errors) {
                    Toast.makeText(context, 
                        synced + " reseñas sincronizadas", 
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
```

---

## APIs y Servicios

### P25: ¿Qué endpoints tiene la API?
**R:** La API REST tiene los siguientes endpoints:

**Autenticación:**
- `POST /api/v1/auth/register` - Registro
- `POST /api/v1/auth/authenticate` - Login

**Usuarios:**
- `GET /api/v1/users/me` - Obtener perfil actual
- `PUT /api/v1/users/me/update` - Actualizar perfil
- `POST /api/v1/users/me/photo` - Subir foto de perfil

**Lugares:**
- `GET /api/v1/places` - Listar todos
- `GET /api/v1/places/{id}` - Obtener uno
- `GET /api/v1/places/nearby?lat={lat}&lng={lng}` - Cercanos

**Reseñas:**
- `POST /api/v1/reviews` - Crear reseña
- `POST /api/v1/reviews/with-photos` - Crear con fotos
- `GET /api/v1/reviews/places/{placeId}?sortBy=best` - Listar por lugar
- `POST /api/v1/reviews/{reviewId}/votes/up` - Upvote
- `POST /api/v1/reviews/{reviewId}/votes/down` - Downvote
- `DELETE /api/v1/reviews/{reviewId}` - Eliminar

---

### P26: ¿Cómo se suben archivos al servidor?
**R:** Se usa **Multipart Form Data**:

**Backend:**
```java
@PostMapping(value = "/with-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ReviewResponseDTO> createWithPhotos(
        @RequestPart("review") String reviewJson,
        @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
    
    ObjectMapper mapper = new ObjectMapper();
    CreateReviewRequestDTO req = mapper.readValue(reviewJson, CreateReviewRequestDTO.class);
    
    ReviewResponseDTO response = reviewService.createReviewWithPhotos(req, photos);
    return ResponseEntity.ok(response);
}
```

**Android:**
```java
// 1. Crear RequestBody para JSON
ReviewRequest request = new ReviewRequest(...);
String json = new Gson().toJson(request);
RequestBody reviewBody = RequestBody.create(MediaType.parse("application/json"), json);

// 2. Crear parte para foto
File photoFile = new File(photoPath);
RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
    "photos", photoFile.getName(), photoBody);

// 3. Enviar
api.createReviewWithPhotos(reviewBody, Arrays.asList(photoPart)).enqueue(...);
```

---

### P27: ¿Cómo se manejan los errores en las APIs?
**R:** Con manejo global de excepciones:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(400, ex.getMessage());
        return ResponseEntity.status(400).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Error interno del servidor");
        return ResponseEntity.status(500).body(error);
    }
}
```

**Respuesta JSON:**
```json
{
  "status": 404,
  "message": "Usuario no encontrado",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Patrones de Diseño

### P28: ¿Qué patrones de diseño se utilizan?
**R:** El proyecto implementa varios patrones:

**1. Singleton:**
```java
public class SessionManager {
    private static SessionManager instance;
    
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }
}
```

**2. Repository:**
- Abstrae acceso a datos (API + DB local)

**3. Builder:**
```java
Review review = Review.builder()
    .description("Excelente lugar")
    .rateToPlace(5)
    .user(user)
    .build();
```

**4. Observer (Callback):**
```java
public interface ReviewCallback {
    void onSuccess(String message);
    void onError(String error);
}
```

**5. Adapter (RecyclerView):**
- Adapta datos a vistas

**6. DTO (Data Transfer Object):**
- Separa modelos de transferencia

**7. Dependency Injection:**
```java
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
}
```

---

### P29: ¿Qué es la inyección de dependencias?
**R:** Es un patrón donde los objetos reciben sus dependencias en lugar de crearlas.

**Sin DI (Mal):**
```java
public class ReviewService {
    private ReviewRepository repo = new ReviewRepository(); // Acoplamiento fuerte
}
```

**Con DI (Bien):**
```java
public class ReviewService {
    private final ReviewRepository repo;
    
    @Autowired  // Spring inyecta automáticamente
    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }
}
```

**Ventajas:**
- **Testeable**: Se pueden pasar mocks
- **Desacoplado**: Cambiar implementación sin modificar código
- **Mantenible**: Dependencias explícitas
- **Reutilizable**: Componentes independientes

---

## Testing y Calidad

### P30: ¿Qué pruebas se deberían hacer?
**R:** El proyecto debería incluir:

**1. Unit Tests (JUnit):**
```java
@Test
public void testCreateReview() {
    // Arrange
    User user = new User();
    Place place = new Place();
    CreateReviewRequestDTO req = new CreateReviewRequestDTO();
    
    // Act
    Review review = reviewService.createReview(req, user);
    
    // Assert
    assertNotNull(review);
    assertEquals(5, review.getRateToPlace());
}
```

**2. Integration Tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetReviews() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/places/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].description").exists());
    }
}
```

**3. Android Instrumentation Tests:**
```java
@Test
public void testDatabaseInsert() {
    ReviewEntity review = new ReviewEntity();
    review.setDescription("Test");
    
    long id = dao.insert(review);
    
    ReviewEntity retrieved = dao.getById(id);
    assertEquals("Test", retrieved.getDescription());
}
```

**4. UI Tests (Espresso):**
```java
@Test
public void testLoginFlow() {
    onView(withId(R.id.et_email)).perform(typeText("test@test.com"));
    onView(withId(R.id.et_password)).perform(typeText("123456"));
    onView(withId(R.id.btn_login)).perform(click());
    
    onView(withId(R.id.home_layout)).check(matches(isDisplayed()));
}
```

---

### P31: ¿Cómo se asegura la calidad del código?
**R:** Mediante:

1. **Code Review**: Revisión de pull requests
2. **Linting**: Checkstyle, Android Lint
3. **Formateo**: Google Java Style Guide
4. **Documentación**: JavaDoc en métodos públicos
5. **Versionado**: Git con branches por feature
6. **CI/CD**: GitHub Actions para builds automáticos

---

## Preguntas Avanzadas

### P32: ¿Cómo escalaría la aplicación?
**R:** Estrategias de escalabilidad:

**Backend:**
- **Horizontal**: Múltiples instancias con Load Balancer
- **Cache distribuido**: Redis para sesiones
- **CDN**: Para imágenes y assets estáticos
- **Microservicios**: Separar reviews, users, places
- **Database sharding**: Particionar por región geográfica

**Android:**
- **Paginación**: Cargar reviews en lotes
- **Image caching**: Glide/Picasso con cache
- **Background sync**: WorkManager para sincronizar

---

### P33: ¿Qué mejoras futuras implementaría?
**R:** Posibles mejoras:

1. **Notificaciones push**: Firebase Cloud Messaging
2. **Chat entre usuarios**: WebSocket o Firebase Realtime
3. **Modo dark**: Themes en Android
4. **Traducción**: i18n para múltiples idiomas
5. **Analytics**: Firebase Analytics para métricas
6. **A/B Testing**: Experimentar con features
7. **Machine Learning**: Recomendaciones personalizadas
8. **Social Login**: Google, Facebook OAuth
9. **Gamificación**: Badges, niveles de usuario
10. **Mapa offline**: Tiles precargados

---

### P34: ¿Cómo optimizaría el rendimiento?
**R:** Optimizaciones:

**Backend:**
- **Índices en BD**: En campos de búsqueda frecuente
- **Lazy loading**: FetchType.LAZY en relaciones
- **Query optimization**: Evitar N+1 queries
- **Compression**: GZIP en responses
- **Connection pooling**: HikariCP

**Android:**
- **RecyclerView**: Reutiliza vistas
- **Image optimization**: Resize antes de subir
- **Background threads**: No bloquear UI
- **Memory leaks**: Usar LeakCanary
- **APK size**: ProGuard para minificar

```java
// Lazy loading
@ManyToOne(fetch = FetchType.LAZY)
private User user;

// Batch query
@Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.place.id = ?1")
List<Review> findByPlaceWithUser(Long placeId);
```

---

### P35: ¿Cómo se despliega la aplicación?
**R:** Proceso de deployment:

**Backend:**
1. Build: `mvn clean package`
2. Docker: Containerizar con Dockerfile
3. AWS Elastic Beanstalk / Heroku
4. Base de datos: AWS RDS PostgreSQL
5. CI/CD: GitHub Actions automatiza build y deploy

**Android:**
1. Build: Gradle genera APK/AAB
2. Firma: Keystore para release
3. Google Play Console: Subir AAB
4. Revisión: Google aprueba
5. Publicación: Disponible en Play Store

```dockerfile
# Dockerfile Backend
FROM openjdk:17-jdk-slim
COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Preguntas de Teoría

### P36: ¿Qué es REST y sus principios?
**R:** **REST (Representational State Transfer)** es un estilo arquitectónico.

**Principios:**
1. **Cliente-Servidor**: Separación de responsabilidades
2. **Stateless**: Cada petición contiene toda la información
3. **Cacheable**: Respuestas pueden cachearse
4. **Interface uniforme**: URIs, métodos HTTP estándar
5. **Sistema de capas**: Puede haber intermediarios
6. **Código bajo demanda** (opcional): Servidor envía código ejecutable

**Métodos HTTP:**
- GET: Obtener recurso
- POST: Crear recurso
- PUT: Actualizar completo
- PATCH: Actualizar parcial
- DELETE: Eliminar

---

### P37: ¿Qué es JSON y por qué se usa?
**R:** **JSON (JavaScript Object Notation)** es un formato de intercambio de datos.

**Ventajas:**
- **Legible**: Para humanos y máquinas
- **Ligero**: Menos overhead que XML
- **Universal**: Todos los lenguajes lo soportan
- **Type-safe**: Con librerías (Gson, Jackson)

**Ejemplo:**
```json
{
  "id": 1,
  "description": "Excelente lugar",
  "rateToPlace": 5,
  "user": {
    "id": 10,
    "userName": "Juan"
  },
  "photos": [
    {"url": "photo1.jpg"},
    {"url": "photo2.jpg"}
  ]
}
```

---

### P38: ¿Qué es CORS y por qué importa?
**R:** **CORS (Cross-Origin Resource Sharing)** permite que un frontend en un dominio acceda a una API en otro.

**Problema:**
- Por defecto, navegadores bloquean peticiones cross-origin por seguridad
- Android no tiene este problema (no es navegador)

**Solución en Spring:**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

---

### P39: ¿Qué son las anotaciones en Java?
**R:** Las anotaciones son metadatos que proporcionan información sobre el código.

**Spring:**
- `@RestController`: Marca clase como controlador REST
- `@Service`: Marca clase como servicio
- `@Repository`: Marca clase como repositorio
- `@Autowired`: Inyecta dependencia
- `@GetMapping`, `@PostMapping`: Define endpoints

**JPA:**
- `@Entity`: Marca clase como entidad
- `@Table`: Especifica nombre de tabla
- `@Id`: Marca primary key
- `@GeneratedValue`: Auto-incremento
- `@Column`: Configura columna

**Android:**
- `@Override`: Sobrescribe método
- `@NonNull`, `@Nullable`: Null safety

---

### P40: ¿Qué es Material Design?
**R:** **Material Design** es el sistema de diseño de Google para interfaces.

**Principios:**
- **Material is the metaphor**: Superficies y sombras realistas
- **Bold, graphic, intentional**: Tipografía clara, colores vibrantes
- **Motion provides meaning**: Animaciones coherentes

**Componentes usados:**
- BottomNavigationView
- FloatingActionButton
- CardView
- RecyclerView
- TextInputLayout
- AppBarLayout

**Implementación:**
```xml
<!-- build.gradle -->
implementation 'com.google.android.material:material:1.9.0'

<!-- layout.xml -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardElevation="4dp"
    app:cardCornerRadius="8dp">
    
    <TextView
        android:text="Contenido"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
        
</com.google.android.material.card.MaterialCardView>
```

---

## Conclusión

Este documento cubre las áreas principales del proyecto Travel Guide. Para prepararse para la defensa:

1. **Entender la arquitectura general** del sistema
2. **Conocer el flujo completo** desde UI hasta BD
3. **Explicar decisiones de diseño** y sus trade-offs
4. **Demostrar dominio técnico** de las tecnologías
5. **Identificar limitaciones** y posibles mejoras

**Recomendación:** Practicar explicando cada funcionalidad con un diagrama de flujo y ejemplos de código específicos del proyecto.

