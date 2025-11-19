# 🧪 Guía de Pruebas - Funcionalidad Offline

## 📋 Checklist de Pruebas

### ✅ **Prueba 1: Crear reseña CON conexión**

**Objetivo:** Verificar que las reseñas se envían correctamente al backend cuando hay conexión.

**Pasos:**
1. ✅ Asegurar que el dispositivo tiene conexión WiFi/datos
2. ✅ Abrir la app y navegar a Review Activity
3. ✅ Verificar que el indicador muestra: `🟢 Online - Reseñas se sincronizan automáticamente`
4. ✅ Completar rating, descripción y opcionalmente tomar foto
5. ✅ Presionar **ENVIAR**
6. ✅ Verificar toast: `✅ Reseña publicada exitosamente`
7. ✅ Verificar en backend que la reseña se creó

**Resultado esperado:**
- ✅ Reseña aparece en el backend inmediatamente
- ✅ Se guarda en base de datos local con `synced=true`

---

### ✅ **Prueba 2: Crear reseña SIN conexión**

**Objetivo:** Verificar que las reseñas se guardan localmente cuando no hay conexión.

**Pasos:**
1. ✅ **Activar modo avión** en el dispositivo
2. ✅ Abrir la app y navegar a Review Activity
3. ✅ Verificar que el indicador muestra: `📴 Offline - Las reseñas se guardarán localmente`
4. ✅ Completar rating: `5 estrellas`
5. ✅ Descripción: `"Reseña de prueba offline"`
6. ✅ Opcionalmente tomar foto
7. ✅ Presionar **ENVIAR**
8. ✅ Verificar toast: `💾 Sin conexión. Reseña guardada y se sincronizará automáticamente.`
9. ✅ La pantalla se cierra correctamente

**Resultado esperado:**
- ✅ Reseña NO está en el backend todavía
- ✅ Se guarda en base de datos local con `synced=false`
- ✅ Usuario recibe feedback claro del estado offline

---

### ✅ **Prueba 3: Sincronización automática al recuperar conexión**

**Objetivo:** Verificar que las reseñas pendientes se sincronizan automáticamente.

**Pasos:**
1. ✅ Asegurar que hay al menos 1 reseña pendiente (de Prueba 2)
2. ✅ **Desactivar modo avión** para recuperar conexión
3. ✅ Abrir **Home Activity** (pantalla principal)
4. ✅ Esperar unos segundos
5. ✅ Verificar toast: `✅ 1 reseña(s) sincronizada(s)`
6. ✅ Verificar en backend que la reseña apareció

**Resultado esperado:**
- ✅ Sincronización automática sin intervención del usuario
- ✅ Toast confirma cantidad sincronizada
- ✅ Base de datos local actualiza `synced=true`

---

### ✅ **Prueba 4: Múltiples reseñas offline**

**Objetivo:** Verificar que se pueden crear múltiples reseñas sin conexión.

**Pasos:**
1. ✅ **Activar modo avión**
2. ✅ Crear **3 reseñas diferentes**:
   - Reseña 1: Rating 5, "Primera offline"
   - Reseña 2: Rating 4, "Segunda offline", con foto
   - Reseña 3: Rating 3, "Tercera offline"
3. ✅ Cada vez verificar toast: `💾 Sin conexión...`
4. ✅ **Desactivar modo avión**
5. ✅ Abrir Home Activity
6. ✅ Verificar toast: `✅ 3 reseña(s) sincronizada(s)`

**Resultado esperado:**
- ✅ Todas las reseñas se guardan localmente
- ✅ Al sincronizar, todas se envían al backend
- ✅ No hay pérdida de datos

---

### ✅ **Prueba 5: Crear reseña con foto offline**

**Objetivo:** Verificar que las fotos se manejan correctamente en modo offline.

**Pasos:**
1. ✅ **Activar modo avión**
2. ✅ Abrir Review Activity
3. ✅ Presionar botón **TOMAR FOTO**
4. ✅ Permitir permisos de cámara si es necesario
5. ✅ Tomar una foto de prueba
6. ✅ Verificar que aparece el preview de la foto
7. ✅ Completar rating y descripción
8. ✅ Presionar **ENVIAR**
9. ✅ Verificar toast offline
10. ✅ **Desactivar modo avión**
11. ✅ Abrir Home para sincronizar
12. ✅ Verificar en backend que la foto se subió correctamente

**Resultado esperado:**
- ✅ Foto se guarda localmente en almacenamiento privado
- ✅ Al sincronizar, foto se envía al backend
- ✅ `photoPath` se mantiene hasta sincronización exitosa

---

### ✅ **Prueba 6: Indicadores UI cambian según conectividad**

**Objetivo:** Verificar que los indicadores visuales reflejan el estado real.

**Pasos:**
1. ✅ Con conexión: Abrir Review Activity
2. ✅ Verificar color verde en indicador: `🟢 Online...`
3. ✅ **Activar modo avión**
4. ✅ Cerrar y reabrir Review Activity
5. ✅ Verificar color naranja en indicador: `📴 Offline...`
6. ✅ **Desactivar modo avión**
7. ✅ Cerrar y reabrir Review Activity
8. ✅ Verificar que vuelve a verde: `🟢 Online...`

**Resultado esperado:**
- ✅ Indicador siempre muestra estado correcto
- ✅ Color cambia según estado (verde/naranja)

---

### ✅ **Prueba 7: Verificar base de datos local**

**Objetivo:** Inspeccionar la base de datos SQLite directamente.

**Pasos con ADB:**
```bash
# Conectar dispositivo/emulador
adb devices

# Ver tabla de reseñas
adb shell "run-as ar.edu.uade.api sqlite3 /data/data/ar.edu.uade.api/databases/dappi_database 'SELECT * FROM reviews;'"

# Ver solo reseñas pendientes
adb shell "run-as ar.edu.uade.api sqlite3 /data/data/ar.edu.uade.api/databases/dappi_database 'SELECT id, description, synced FROM reviews WHERE synced=0;'"

# Contar reseñas pendientes
adb shell "run-as ar.edu.uade.api sqlite3 /data/data/ar.edu.uade.api/databases/dappi_database 'SELECT COUNT(*) FROM reviews WHERE synced=0;'"
```

**Resultado esperado:**
- ✅ Comando muestra datos de la BD
- ✅ Campo `synced` refleja estado correcto
- ✅ Datos persisten después de cerrar app

---

### ✅ **Prueba 8: Logs de sincronización**

**Objetivo:** Verificar logs en Logcat.

**Pasos:**
```bash
# Ver logs de sincronización
adb logcat | grep -E "ReviewRepository|NetworkManager"

# Ver logs específicos de Room
adb logcat | grep Room
```

**Buscar en logs:**
- `🟢 Online: Enviando reseña al backend`
- `🔴 Offline: Guardando reseña localmente`
- `💾 Reseña guardada localmente con ID: X`
- `🔄 Sincronizando X reseñas pendientes`
- `✅ Reseña X sincronizada`

**Resultado esperado:**
- ✅ Logs claros con emojis para identificar estados
- ✅ Información útil para debugging

---

### ✅ **Prueba 9: Persistencia de datos**

**Objetivo:** Verificar que los datos sobreviven al cierre de la app.

**Pasos:**
1. ✅ **Activar modo avión**
2. ✅ Crear 2 reseñas offline
3. ✅ **Forzar cierre** de la app (configuración > apps > forzar detención)
4. ✅ Abrir la app nuevamente (aún en modo avión)
5. ✅ Verificar que las reseñas están en BD:
```bash
adb shell "run-as ar.edu.uade.api sqlite3 /data/data/ar.edu.uade.api/databases/dappi_database 'SELECT COUNT(*) FROM reviews WHERE synced=0;'"
```
6. ✅ **Desactivar modo avión**
7. ✅ Abrir Home
8. ✅ Verificar sincronización: `✅ 2 reseña(s) sincronizada(s)`

**Resultado esperado:**
- ✅ Datos persisten después de cerrar app
- ✅ Se sincronizan correctamente al reabrir con conexión

---

### ✅ **Prueba 10: Error de backend con conexión**

**Objetivo:** Verificar comportamiento cuando backend falla pero hay conexión.

**Pasos:**
1. ✅ Asegurar conexión WiFi
2. ✅ **Apagar el backend** (detener servidor Spring Boot)
3. ✅ Intentar crear una reseña
4. ✅ Presionar ENVIAR
5. ✅ Verificar toast: `💾 Reseña guardada. Se sincronizará cuando haya conexión.`
6. ✅ **Iniciar el backend** nuevamente
7. ✅ Abrir Home
8. ✅ Verificar sincronización exitosa

**Resultado esperado:**
- ✅ App no crashea cuando backend está caído
- ✅ Reseña se guarda localmente como pendiente
- ✅ Se intenta sincronizar después cuando backend vuelve

---

## 🎯 Casos Edge (Casos límite)

### **Caso 1: Cambio de conexión durante envío**

**Escenario:** Usuario pierde conexión justo cuando presiona ENVIAR

**Resultado esperado:**
- ✅ Request falla
- ✅ Reseña se guarda como pendiente
- ✅ Usuario recibe feedback apropiado

---

### **Caso 2: Reseña muy grande**

**Escenario:** Reseña con descripción muy larga (>5000 caracteres)

**Resultado esperado:**
- ✅ Se guarda correctamente en SQLite (tipo TEXT)
- ✅ Se sincroniza sin problemas

---

### **Caso 3: Múltiples fotos pesadas**

**Escenario:** Usuario toma foto de alta resolución (>5MB)

**Resultado esperado:**
- ✅ Foto se guarda localmente
- ⚠️ Al sincronizar puede tardar más
- ✅ Usuario debería ver indicador de progreso (pendiente)

---

## 📊 Matriz de Pruebas

| # | Prueba | Estado Online | Estado Offline | Backend UP | Backend DOWN |
|---|--------|---------------|----------------|------------|--------------|
| 1 | Crear reseña | ✅ Sube | - | ✅ | - |
| 2 | Crear reseña | - | ✅ Guarda local | - | - |
| 3 | Sincronización | ✅ Sincroniza | - | ✅ | - |
| 4 | Múltiples offline | - | ✅ Guarda todas | - | - |
| 5 | Con foto offline | - | ✅ Guarda foto | - | - |
| 6 | Indicadores UI | ✅ Verde | ✅ Naranja | - | - |
| 7 | Base datos | ✅ Verifica | ✅ Verifica | - | - |
| 8 | Logs | ✅ Online logs | ✅ Offline logs | - | - |
| 9 | Persistencia | ✅ Persiste | ✅ Persiste | - | - |
| 10 | Error backend | ✅ Guarda local | - | - | ✅ Guarda local |

---

## 🐛 Problemas Conocidos y Soluciones

### **Problema 1: "Network callback not registered"**

**Causa:** Permisos no otorgados

**Solución:**
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

### **Problema 2: Reseñas no se sincronizan automáticamente**

**Causa:** Home Activity no se abre después de recuperar conexión

**Solución:** Usuario debe abrir Home manualmente, o implementar WorkManager para sincronización en background

---

### **Problema 3: Fotos muy pesadas**

**Causa:** Cámara toma fotos en resolución máxima

**Solución:** Implementar compresión antes de guardar (pendiente)

---

## ✅ Resultado Final Esperado

Después de todas las pruebas:

- ✅ **Funcionalidad online:** Reseñas se envían inmediatamente
- ✅ **Funcionalidad offline:** Reseñas se guardan localmente
- ✅ **Sincronización:** Automática al recuperar conexión
- ✅ **Indicadores UI:** Claros y precisos
- ✅ **Persistencia:** Datos sobreviven al cierre de app
- ✅ **Fotos:** Se manejan correctamente offline
- ✅ **Sin crashes:** App estable en todos los escenarios

---

## 📝 Checklist Final

- [ ] Prueba 1: Reseña online ✅
- [ ] Prueba 2: Reseña offline ✅
- [ ] Prueba 3: Sincronización automática ✅
- [ ] Prueba 4: Múltiples offline ✅
- [ ] Prueba 5: Foto offline ✅
- [ ] Prueba 6: Indicadores UI ✅
- [ ] Prueba 7: Verificar BD ✅
- [ ] Prueba 8: Logs ✅
- [ ] Prueba 9: Persistencia ✅
- [ ] Prueba 10: Error backend ✅

---

**Nota:** Para probar en emulador, usar la opción "Extended Controls" (botón `...` en el panel lateral) y luego ir a "Cellular" para deshabilitar datos, o simplemente activar modo avión desde la configuración de Android.

---

**Fecha:** Noviembre 2025  
**Versión:** 1.0

