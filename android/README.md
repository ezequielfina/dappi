# TravelGuide - Pantallas de Login/Register

Este proyecto implementa las pantallas de autenticación y onboarding para la aplicación TravelGuide, siguiendo un diseño oscuro con elementos azules claros.

## 📱 Pantallas Implementadas

### 1. Pantalla de Bienvenida (`activity_welcome.xml`)
**Ubicación:** `app/src/main/res/layout/activity_welcome.xml`

**Características:**
- Ilustración vectorial de una playa con pareja
- Título "TravelGuide" en tipografía bold
- Descripción de la funcionalidad de la app en español
- Dos botones principales:
  - "Registrarse" (botón primario azul)
  - "Iniciar Sesión" (botón secundario con borde)

**Elementos visuales:**
- Fondo oscuro (`background_dark`)
- Ilustración centrada de 200x200dp
- Texto blanco para títulos y gris claro para descripción
- Botones con elevación y bordes redondeados

### 2. Pantalla de Registro (`activity_register.xml`)
**Ubicación:** `app/src/main/res/layout/activity_register.xml`

**Campos de entrada:**
- **Nombre de usuario** - Campo de texto simple
- **E-mail** - Campo con validación de email
- **Número de teléfono** - Campo con inputType phone
- **Contraseña** - Campo de contraseña
- **Confirmar contraseña** - Campo de contraseña para verificación

**Botones:**
- "Registrarse" - Botón primario para completar registro
- "Tenes cuenta? Inicia Sesion" - Botón para ir a login

**Características:**
- Labels descriptivos para cada campo
- Placeholders en español
- Validación de tipos de entrada apropiada
- Diseño responsive con ConstraintLayout

### 3. Pantalla de Login (`activity_login.xml`)
**Ubicación:** `app/src/main/res/layout/activity_login.xml`

**Campos de entrada:**
- **Nombre de Usuario** - Campo de texto
- **Contraseña** - Campo de contraseña

**Botón:**
- "Iniciar Sesion" - Botón primario para autenticación

**Características:**
- Diseño minimalista y centrado
- Labels descriptivos
- Placeholders en español
- Fondo oscuro consistente

### 4. Pantalla de Onboarding (`activity_onboarding.xml`)
**Ubicación:** `app/src/main/res/layout/activity_onboarding.xml`

**Título:** "FLUJO DE ONBOARDING DE APP AL REGISTRARSE"

**Secciones:**
- **Seleccionar país de origen** - Botón selector
- **Seleccionar lugar favorito** - Botón selector  
- **Subir foto de perfil** - Botón con ícono "+"
- **Continuar** - Botón para completar onboarding

**Características:**
- Flecha indicadora apuntando al contenido
- Layout vertical con espaciado consistente
- Botones con estilo uniforme
- Diseño alineado a la derecha como en el mockup

## 🎨 Sistema de Colores

**Archivo:** `app/src/main/res/values/colors.xml`

```xml
<!-- Colores base -->
<color name="black">#FF000000</color>
<color name="white">#FFFFFFFF</color>

<!-- Paleta TravelGuide -->
<color name="background_dark">#FF2C3E50</color>      <!-- Fondo principal oscuro -->
<color name="background_light">#FF34495E</color>     <!-- Fondo secundario -->
<color name="accent_blue">#FF3498DB</color>          <!-- Azul principal -->
<color name="light_blue">#FF5DADE2</color>           <!-- Azul claro -->
<color name="text_primary">#FFFFFFFF</color>         <!-- Texto principal -->
<color name="text_secondary">#FFBDC3C7</color>       <!-- Texto secundario -->
<color name="input_border">#FF3498DB</color>         <!-- Borde de inputs -->
<color name="input_background">#FF2C3E50</color>     <!-- Fondo de inputs -->
<color name="button_primary">#FF3498DB</color>       <!-- Botones primarios -->
<color name="button_secondary">#FF2C3E50</color>     <!-- Botones secundarios -->
```

## 🖼️ Recursos Drawable

### Botones
- **`button_primary.xml`** - Botones principales con fondo azul y bordes redondeados
- **`button_secondary.xml`** - Botones secundarios con borde azul y fondo transparente

### Campos de Entrada
- **`input_field.xml`** - Campos de texto con borde azul y fondo oscuro

### Ilustraciones
- **`beach_illustration.xml`** - Ilustración vectorial de playa con pareja, palmeras y océano
- **`ic_arrow_forward.xml`** - Ícono de flecha para navegación

## 📁 Estructura de Archivos

```
app/src/main/res/
├── drawable/
│   ├── beach_illustration.xml
│   ├── button_primary.xml
│   ├── button_secondary.xml
│   ├── ic_arrow_forward.xml
│   └── input_field.xml
├── layout/
│   ├── activity_welcome.xml
│   ├── activity_register.xml
│   ├── activity_login.xml
│   └── activity_onboarding.xml
└── values/
    └── colors.xml
```

## 🚀 Características Técnicas

### Diseño Responsive
- Uso de `ConstraintLayout` para layouts flexibles
- Dimensiones en `dp` para escalabilidad
- Margins y paddings consistentes

### Accesibilidad
- Labels descriptivos para todos los campos
- Contraste adecuado entre texto y fondos
- Tamaños de texto legibles (16sp para inputs, 28sp para títulos)

### Material Design
- Elevación en botones (`android:elevation="4dp"`)
- Bordes redondeados (`android:radius="12dp"` para botones, `8dp` para inputs)
- Colores consistentes con la paleta definida

### Internacionalización
- Todos los textos en español
- Placeholders descriptivos
- Labels apropiados para cada campo

## 📋 Próximos Pasos

Para completar la implementación, necesitarás:

1. **Crear las Activities correspondientes** en Java/Kotlin
2. **Implementar la lógica de navegación** entre pantallas
3. **Agregar validación de formularios**
4. **Conectar con backend** para autenticación
5. **Implementar funcionalidad de selección** en onboarding (países, lugares, foto)

## 🎯 Uso

Para usar estas pantallas en tu aplicación:

1. Copia los archivos XML a sus respectivas carpetas
2. Actualiza las referencias en tus Activities
3. Implementa la lógica de navegación
4. Personaliza los colores si es necesario

## 📝 Notas de Diseño

- El diseño sigue un tema oscuro consistente
- Los elementos interactivos usan el color azul como acento
- La tipografía es clara y legible
- El espaciado es generoso para facilitar la interacción táctil
- Los botones tienen un tamaño mínimo de 56dp para cumplir con las guías de Material Design

---

**Desarrollado para TravelGuide** - Aplicación de guía de viajes con mapas interactivos y comunidad de viajeros.
