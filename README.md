# InMind

Este es un proyecto de Compose Multiplatform diseñado para funcionar en Android y Escritorio (Desktop).

## Propósito del Proyecto
El proyecto **InMind** forma parte de un trabajo de investigación/tesis orientado al desarrollo multiplataforma. Su objetivo principal es explorar y demostrar las capacidades de Kotlin Multiplatform y Compose Multiplatform para compartir lógica y UI entre diferentes sistemas operativos.

## Bitácora de Cambios (Log de Commits/Acciones)

### [Inicialización y Correcciones Técnicas]
*   **Corrección de Plugin de Compose Compiler**: Se actualizó el ID del plugin de `id("kotlin-compose-compiler")` a `kotlin("plugin.compose")` en `composeApp/build.gradle.kts` para compatibilidad con Kotlin 2.0.0.
*   **Habilitación de AndroidX**: Se añadieron las propiedades `android.useAndroidX=true` y `android.enableJetifier=true` en el archivo `gradle.properties` para resolver conflictos de dependencias y permitir el uso de librerías modernas de Android.
*   **Verificación de Ejecución**: Se sincronizó exitosamente el proyecto y se verificó su funcionamiento ejecutando la versión de escritorio (Desktop).

### [Módulos Base y Navegación]
*   **Implementación de Navegación Inferior**: Se añadió un componente `Scaffold` con `BottomNavigation` para permitir la alternancia entre tres secciones principales en formato móvil.
*   **Módulo de Tareas**: Creación de una interfaz básica para visualizar tareas pendientes.
*   **Módulo de Calendario**: Implementación de una vista base para la selección de fechas y programación de actividades.
*   **Módulo Vacío**: Adición de un tercer contenedor reservado para futuras funcionalidades.

### [Lógica de Tareas y Calendario]
*   **Modelo de Datos**: Creación de la clase `Task` para representar actividades con nombre, descripción, fecha, recordatorio y recurrencia.
*   **Interactividad en Calendario**: Implementación de una cuadrícula de días interactiva. Al seleccionar un día, se captura la fecha automáticamente.
*   **Diálogo de Creación (Pop-up)**: Desarrollo de un `AlertDialog` con campos para:
    *   Nombre de la tarea.
    *   Descripción.
    *   Fecha (asignada automáticamente desde el calendario).
    *   Opciones de recordatorio (Hora, Diario, Semanal).
    *   Checkbox para asignación a grupo recurrente.
*   **Persistencia de Estado**: Uso de `mutableStateListOf` para que las tareas guardadas se reflejen inmediatamente en el primer módulo (Tareas Pendientes).
*   **Flujo de Usuario**: Al guardar una tarea, la aplicación navega automáticamente a la pantalla de tareas para mostrar el nuevo registro.
