# Guía Técnica: Proyecto InMind (Compose Multiplatform)

Este proyecto está desarrollado con **Compose Multiplatform (KMP)**, soportando Android y Desktop (JVM).

## 1. Requisitos de Software

Para inicializar el proyecto correctamente, asegúrate de tener instalados los siguientes componentes:

*   **Android Studio:** Jellyfish o superior (recomendado Ladybug para soporte completo de Kotlin 2.0).
*   **JDK (Java Development Kit):** Versión **21** o superior. Este proyecto utiliza Gradle 9.3, que requiere una versión de Java moderna.
*   **Android SDK:**
    *   `compileSdk`: 34
    *   `minSdk`: 24
*   **Plugins de Android Studio:**
    *   Kotlin (integrado)
    *   Compose Multiplatform IDE Support

## 2. Inicialización del Proyecto

1.  **Clonar el repositorio:**
    ```bash
    git clone <url-del-repositorio>
    cd InMind
    ```
2.  **Configurar el JDK en Android Studio:**
    *   Ve a `File` -> `Settings` (o `Android Studio` -> `Settings` en macOS).
    *   Navega a `Build, Execution, Deployment` -> `Build Tools` -> `Gradle`.
    *   Asegúrate de que el **Gradle JDK** esté apuntando a **Java 21**.
3.  **Sincronizar Gradle:**
    *   Al abrir el proyecto, Android Studio debería pedir sincronizar. Si no, pulsa el icono de "Elephant" (Sync Project with Gradle Files).

## 3. Ejecución

*   **Android:** Selecciona el módulo `composeApp` y un dispositivo/emulador, luego presiona `Run`.
*   **Desktop:** Ejecuta la tarea de Gradle:
    ```bash
    ./gradlew :composeApp:run
    ```

## 4. Contramedidas ante Errores Comunes

### Error: "Cannot mutate the dependencies of configuration..."
*   **Causa:** Conflicto entre versiones de AGP, Compose y Gradle después de que una configuración ha sido resuelta.
*   **Solución:** 
    *   Asegúrate de que `agp.version` (8.5.2+) y `compose.version` (1.7.0+) sean compatibles.
    *   Evita modificar manualmente los `sourceSets` de Android si el plugin de Compose ya está gestionando los recursos, a menos que sea estrictamente necesario.
    *   Ejecuta `./gradlew clean` antes de volver a intentar la sincronización.

### Error: "Incompatible Gradle JVM version"
*   **Causa:** Estás intentando ejecutar Gradle con una versión de Java inferior a la requerida por el wrapper o incompatible con el AGP.
*   **Solución:** Actualiza el JDK en los ajustes de Gradle de Android Studio a Java 21 o superior.

### Error: "Android resource linking failed (AAPT)"
*   **Causa:** Faltan recursos referenciados en el `AndroidManifest.xml` (como `ic_launcher`).
*   **Solución:** Verifica que la carpeta `composeApp/src/androidMain/res` contenga los iconos necesarios o que el manifiesto apunte a recursos existentes en `drawable`.

### Problemas con el Cache de Gradle
Si los errores persisten tras cambios de versión:
1.  Cierra Android Studio.
2.  Elimina las carpetas `.gradle` y `build` en la raíz y en `composeApp`.
3.  Reinicia y sincroniza.
