# App de Viajes

## Core
- Explorar lugares  
- Ver horarios, precios de entrada, fotos, descripciones.  
- Mapa y buscador por categorías (restaurantes, museos, parques, etc.).  
- Validar ubicación de usuario para poder hacer reseñas.  

### Reseñas como críticos
- El usuario deja su experiencia, puntúa y sube fotos/videos.  
- Opción de reseñas rápidas (ej.: “3 palabras para describir este lugar”).  
- Votos de otros usuarios (upvotes / downvotes).  

## El Usuario como el Core
- Perfil viajero público (con link compartible).  
- Foto de perfil, bio corta.  
- Contador de lugares visitados.  
- Índice de aprobación: promedio de votos recibidos en reseñas.  
- Medallas (ej.: “Top crítico en Córdoba”, “Explorador gastronómico”).  
- Mapa coleccionista: un mapa personal con pins de lo visitado y un “wishlist” de lo pendiente.  
- Medallas + insignias evolutivas: que se actualicen con hitos (ej.: “Primer viaje internacional”, “50 reseñas publicadas”, “+3 años de antigüedad”).  

## Muro / Timeline personal
- Lista cronológica de reseñas y fotos subidas.  
- Otros pueden seguir tu perfil, comentar o dar like.  
- Se convierte en una “bitácora social de viajes”.  

## Socialización
- Seguir viajeros, armar una red tipo Instagram pero enfocada en viajes.  
- Feed de lo que visitan tus contactos o gente popular.  
- Rankings por ciudad/país → quiénes son los más activos o mejor valorados.  

## Diferencial respecto a Google / TripAdvisor
- En Google Maps el foco es el lugar.  
- En tu app el foco es el viajero → su trayectoria, su credibilidad, su “marca personal”.  
- La gente querría compartir su link de perfil como “mirá todos los lugares que recorrí”.  

## Posibles Features extras
- Comparar viajeros: ver en qué lugares coincidieron 2 usuarios.  
- Listas personalizadas: “mis cafés favoritos de Buenos Aires”, “5 lugares secretos en Madrid”.  
- Modo coleccionista: un mapa con pins de todos los lugares visitados (y los que faltan).  
- **Integración con cámara/audio:**  
  - Cámara: subir fotos directas del lugar.  
  - Modo offline viajero: mapas y reseñas descargadas para usar sin internet.  
  - Audio: notas rápidas de reseña que luego se transcriben.  

## MVP recomendado
- Registro y perfil público básico con contador de lugares.  
- Carga de reseñas con fotos + sistema de votos.  
- Exploración de lugares con horarios y precios.  
- Feed básico con reseñas de la gente que seguís.  

## Tareas H1
- Figma con pantallas clave.  
- Flujo de pantallas definido.  
- Repositorio inicializado.  
- Tablero de seguimiento.  
- DER (perfil, reseñas, lugares, votos, seguidores).  
- Plan de pruebas (casos de uso críticos).  
- APK demo (puede ser con datos mockeados).  
- Diagrama inicial de arquitectura + descripción de tecnologías.  

### Tareas H1 Detalle

#### Alcance
- Definir alcance del MVP (registro, perfil, reseñas, exploración, feed).  
- Crear tablero de seguimiento (Trello / Jira).  
- Inicializar repositorio.  
- Backlog de user stories priorizado.  

#### Diseño UX/UI (Figma)
- Diseñar pantallas principales en Figma:  
  - Registro/Login.  
  - Perfil viajero básico.  
  - Permisos de la aplicación.  
  - Exploración de lugares (lista + detalle) según ubicación del usuario.  
  - Crear reseña (texto, fotos).  
- Definir flujo de navegación.  
- Hacer prototipo navegable.  

#### Arquitectura & Tech Stack
- Decidir tecnología (React Native ó Android nativo).  
- DER (perfil, reseñas, lugares, votos, seguidores).  

#### Frontend Base
- Setup de app (React Native/Nativo).  
- Pantallas conectadas con datos hardcodeados.  
- Registro/Login.  
- Listado de lugares.  
- Detalle de lugar.  
- Crear reseña.  
- APK mockeada lista para mostrar flujo.  

#### Pruebas
- Plan de pruebas.  
- Casos de prueba principales.  