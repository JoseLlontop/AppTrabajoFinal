# Guía para Ejecutar la Aplicación de Kotlin

Esta aplicación de Kotlin necesita que el backend en Python esté activo en la misma red local. Sigue los pasos a continuación para configurarla y ejecutarla correctamente.

### Pasos para Configurar y Ejecutar

1. **Ejecutar el Backend de Python**
   - Navega al directorio del proyecto del backend en Python.
   - En la terminal, escribe el siguiente comando para iniciar el servidor:
     ```bash
     python app.py
     ```
   - Asegúrate de que el backend esté ejecutándose en la misma red local donde está el dispositivo que usará la aplicación de Kotlin.

2. **Configurar la URL del Backend en el Código de la Aplicación de Kotlin**
   - Abre el proyecto de Kotlin en Android Studio.
   - Navega a la clase `Environment` que se encuentra en `utn.appmoviles.apptrabajofinal.config`.
   - Modifica el valor de `BACKEND_URL` para que apunte a la IP de la computadora que ejecuta el backend:
     ```kotlin
     const val BACKEND_URL = "http://IP.COMPUTADORA:5000"
     ```
     Reemplaza `IP.COMPUTADORA` con la IP de la computadora donde corre el backend.

3. **Ejecutar la Aplicación de Kotlin**
   - Una vez que la URL esté configurada, puedes ejecutar la aplicación desde Android Studio.
   - Selecciona el dispositivo donde deseas instalarla y haz clic en **Run** para descargarla en tu celular.

---

Estos pasos asegurarán que la aplicación y el backend estén conectados correctamente.
