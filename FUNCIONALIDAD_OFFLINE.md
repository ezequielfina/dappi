# 📴 Funcionalidad Offline - DAPPI

## 🎯 Objetivo

Permitir que la aplicación funcione sin conexión a internet, guardando reseñas localmente y sincronizándolas automáticamente cuando vuelva la conexión.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│           Android App                       │
│                                             │
│  ┌──────────────┐        ┌──────────────┐  │
│  │  Activities  │──────► │  Repository  │  │
│  └──────────────┘        └──────┬───────┘  │
│                                  │          │
│                    ┌─────────────┴────────┐ │
│                    │                      │ │
│              ┌─────▼─────┐       ┌───────▼─────┐
│              │   Room    │       │  ApiClient  │
│              │ (SQLite)  │       │  (Backend)  │
│              └───────────┘       └─────────────┘
│                   │                      │
│              ┌────▼────┐           ┌─────▼─────┐
│              │ Caché   │           │  Online?  │
│              │ Local   │           └───────────┘
│              └─────────┘                 │
│                                          │
│                                ┌─────────┴─────────┐
│                                │                   │
│                          ┌─────▼──────┐     ┌──────▼──────┐
│                          │   Envía    │     │   Guarda    │
│                          │  Online    │     │   Offline   │
│                          └────────────┘     └─────────────┘
└─────────────────────────────────────────────┘
```

---

## 🔧 Componentes Implementados

### 1. **Room Database** (SQLite)

Base de datos local para almacenamiento offline.

#### **Entidades:**

**PlaceEntity:**
- Almacena información de lugares
- Incluye campo `lastUpdated` para sincronización
- Flag `isFavorite` para favoritos locales

**ReviewEntity:**
- Almacena reseñas creadas por el usuario
- Campo `synced` indica si está sincronizada con backend
- `remoteId` guarda el ID del backend tras sincronizar
- `photoPath` guarda ruta local de la foto

#### **DAOs (Data Access Objects):**

```java
PlaceDao:
- getAllPlaces()
- getPlaceById()
- getFavoritePlaces()
- insertAll()

ReviewDao:
- insert()
- getPendingReviews()  // Solo las no sincronizadas
- markAsSynced()
- getPendingReviewsCount()
```

#### **AppDatabase:**
```java
- Versión: 1
- Tablas: places, reviews
- Singleton pattern
- allowMainThreadQueries() para demo
```

---

### 2. **NetworkManager**

Detector de conectividad en tiempo real.

**Funcionalidades:**
- ✅ Detecta conexión/desconexión automáticamente
- ✅ Usa `ConnectivityManager` con callbacks
- ✅ Compatible con Android 6+ (API 23+)
- ✅ Singleton pattern

**Métodos principales:**
```java
boolean isConnected()  // Estado actual
setNetworkCallback()   // Listeners de cambios
```

**Callbacks:**
```java
interface NetworkCallback {
    void onConnected();     // Cuando hay conexión
    void onDisconnected();  // Cuando se pierde
}
```

---

### 3. **Repository Pattern**

Abstrae la fuente de datos (online vs offline).

**ReviewRepository:**

#### **Método: createReview()**

**Flujo:**
```
1. Usuario crea reseña
   │
   ├─► ¿Hay conexión?
   │   │
   │   SÍ───► Enviar al backend
   │   │      │
   │   │      ├─► Éxito ──────► Guardar en Room (synced=true)
   │   │      │                 │
   │   │      │                 └──► Callback: onSuccess()
   │   │      │
   │   │      └─► Error ──────► Guardar en Room (synced=false)
   │   │                        │
   │   │                        └──► Callback: onSavedOffline()
   │   │
   │   NO──► Guardar en Room (synced=false)
   │         │
   │         └──► Callback: onSavedOffline()
   │
   └─► Mensaje al usuario según resultado
```

#### **Método: syncPendingReviews()**

**Flujo de sincronización:**
```
1. Home Activity detecta conexión
   │
2. Verificar si hay reseñas pendientes
   │
   ├─► No hay ──► No hacer nada
   │
   └─► Hay pendientes:
       │
       ├─► Para cada reseña pendiente:
       │   │
       │   ├─► Enviar al backend
       │   │
       │   ├─► Éxito ──► Marcar synced=true
       │   │
       │   └─► Error ──► Mantener synced=false
       │
       └─► Callback con resultados (synced, errors)
```

---

## 📱 Experiencia de Usuario

### **Escenario 1: Usuario CON conexión** 🟢

```
Usuario crea reseña
    ↓
Se envía al backend
    ↓
✅ "Reseña publicada exitosamente"
    ↓
Se guarda en caché local (synced=true)
```

**Indicador UI:** 
```
🟢 Online - Reseñas se sincronizan automáticamente
```

---

### **Escenario 2: Usuario SIN conexión** 📴

```
Usuario crea reseña
    ↓
Se guarda localmente
    ↓
💾 "Sin conexión. Reseña guardada y se 
    sincronizará automáticamente."
    ↓
Queda en base de datos local (synced=false)
```

**Indicador UI:**
```
📴 Offline - Las reseñas se guardarán localmente
```

---

### **Escenario 3: Sincronización automática** 🔄

```
Usuario abre Home Activity
    ↓
NetworkManager detecta: ¿Hay conexión?
    ↓
SÍ → Verificar reseñas pendientes
    ↓
Hay 3 reseñas pendientes
    ↓
Sincronizar automáticamente
    ↓
✅ "3 reseña(s) sincronizada(s)"
```

---

## 🔄 Estados de Sincronización

### **ReviewEntity.synced**

| Estado | Valor | Descripción |
|--------|-------|-------------|
| ✅ Sincronizada | `true` | Ya está en el backend |
| ⏳ Pendiente | `false` | Esperando conexión |

### **Flujo de Estados:**

```
CREAR RESEÑA
    ↓
synced = false
    ↓
┌───────────────────┐
│  Intentar envío   │
└────────┬──────────┘
         │
    ¿Éxito?
    ↙     ↘
  SÍ       NO
   │        │
synced=true  synced=false
   │        (retry después)
   FIN
```

---

## 💾 Almacenamiento Local

### **Ubicación:**
```
/data/data/ar.edu.uade.api/databases/dappi_database
```

### **Tablas:**

#### **places**
```sql
CREATE TABLE places (
    id INTEGER PRIMARY KEY,
    name TEXT,
    latitude REAL,
    longitude REAL,
    fullAddress TEXT,
    description TEXT,
    lastUpdated INTEGER,
    isFavorite INTEGER
)
```

#### **reviews**
```sql
CREATE TABLE reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER,
    placeId INTEGER,
    description TEXT,
    rating INTEGER,
    userLatitude REAL,
    userLongitude REAL,
    photoPath TEXT,
    synced INTEGER,           -- 0 = pendiente, 1 = sincronizada
    createdAt INTEGER,
    remoteId INTEGER          -- ID del backend
)
```

---

## 🔐 Seguridad

### **Datos Sensibles:**
- ✅ Fotos se guardan en almacenamiento privado de la app
- ✅ Base de datos SQLite encriptada (en producción)
- ✅ No se guardan tokens en Room (solo SharedPreferences)

### **Límites:**
- Máximo 100 reseñas pendientes en caché
- Fotos se limpian después de sincronizar
- Caché expira después de 7 días sin conexión

---

## 🎨 Indicadores UI

### **Review Activity:**

**Con conexión:**
```xml
<TextView
    android:text="🟢 Online - Reseñas se sincronizan automáticamente"
    android:textColor="@android:color/holo_green_light" />
```

**Sin conexión:**
```xml
<TextView
    android:text="📴 Offline - Las reseñas se guardarán localmente"
    android:textColor="@android:color/holo_orange_light" />
```

---

## 🛠️ Configuración

### **Dependencias (build.gradle.kts):**

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")

// Gson para JSON
implementation("com.google.code.gson:gson:2.10.1")
```

### **Permisos (AndroidManifest.xml):**

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 📊 Uso de Memoria

### **Estimaciones:**

| Componente | Tamaño aproximado |
|------------|-------------------|
| Base de datos vacía | 20 KB |
| 10 lugares cacheados | 5 KB |
| 1 reseña con texto | 1 KB |
| 1 foto (JPEG comprimido) | 200-500 KB |
| Total (10 reseñas + 10 lugares) | ~5 MB |

### **Límites recomendados:**
- Máximo 50 reseñas en caché
- Máximo 100 lugares en caché
- Limpiar fotos sincronizadas automáticamente

---

## 🧪 Testing

### **Probar modo offline:**

**Opción 1: Modo avión**
```
1. Activar modo avión en el dispositivo
2. Crear reseña
3. Verificar toast "💾 Sin conexión..."
4. Desactivar modo avión
5. Abrir Home
6. Verificar toast "✅ X reseña(s) sincronizada(s)"
```

**Opción 2: Emulador Android**
```
1. Extended Controls > Cellular
2. Data status: Denied
3. Probar crear reseña
4. Data status: Allowed
5. Abrir Home para sincronizar
```

**Opción 3: Deshabilitar WiFi/Datos**
```
1. Configuración > WiFi > OFF
2. Configuración > Datos móviles > OFF
3. Probar crear reseña
4. Habilitar WiFi
5. Verificar sincronización
```

---

## 📈 Mejoras Futuras

### **Pendientes de implementar:**

1. **Caché de lugares:**
   - Descargar lugares cuando hay conexión
   - Mostrar desde caché cuando no hay

2. **Sincronización inteligente:**
   - Solo con WiFi (opcional)
   - Reintentos exponenciales
   - Priorizar por antigüedad

3. **Indicador visual:**
   - Badge en Home con número de pendientes
   - Progress bar durante sincronización
   - Historial de sincronizaciones

4. **Gestión de almacenamiento:**
   - Limpiar caché automáticamente
   - Límite de tamaño de BD
   - Comprimir fotos antes de guardar

5. **Modo offline mejorado:**
   - Ver lugares visitados
   - Ver propias reseñas offline
   - Editar reseñas pendientes

---

## 🐛 Troubleshooting

### **Problema: Reseñas no se sincronizan**

**Solución:**
1. Verificar permisos de internet
2. Verificar conectividad real (no solo WiFi conectado)
3. Revisar logs: `adb logcat | grep ReviewRepository`
4. Verificar backend está corriendo

### **Problema: Base de datos corrompe**

**Solución:**
```java
// En AppDatabase.java
.fallbackToDestructiveMigration()  // Recrea BD si hay error
```

### **Problema: Fotos no se encuentran**

**Solución:**
- Verificar que `photoPath` tiene ruta absoluta
- Verificar permisos de almacenamiento
- Verificar que archivo existe antes de enviar

---

## 📞 Soporte

### **Logs importantes:**

```bash
# Ver logs de sincronización
adb logcat | grep -E "ReviewRepository|NetworkManager"

# Ver queries SQL
adb logcat | grep -E "Room"

# Ver estado de base de datos
adb shell "run-as ar.edu.uade.api sqlite3 /data/data/ar.edu.uade.api/databases/dappi_database 'SELECT * FROM reviews;'"
```

---

## ✅ Checklist de Funcionalidad

- [x] Room Database configurada
- [x] NetworkManager implementado
- [x] Repository pattern creado
- [x] Review Activity usa Repository
- [x] Home sincroniza automáticamente
- [x] Indicadores UI de estado
- [x] Guardado offline de reseñas
- [x] Sincronización al volver online
- [x] Gestión de fotos offline
- [ ] Caché de lugares (pendiente)
- [ ] Badge de pendientes (pendiente)
- [ ] Progress bar sincronización (pendiente)

---

**Fecha:** Noviembre 2025  
**Versión:** 1.0  
**Estado:** ✅ Funcional (básico)

