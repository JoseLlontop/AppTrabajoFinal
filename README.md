# Guía para Ejecutar la Aplicación de Kotlin

Esta aplicación de Kotlin necesita que el backend en Python esté activo y accesible a través de una URL pública generada por ngrok. Sigue los pasos a continuación para configurar y ejecutar correctamente la aplicación y su backend.

### Pasos para Configurar y Ejecutar

1. **Ejecutar el Backend de Python con ngrok**
    - Navega al directorio del proyecto del backend en Python.
    - Inicia el servidor con el siguiente comando:
      ```bash
      python app.py
      ```
    - En una terminal aparte, inicia ngrok para exponer el servidor en internet:
      ```bash
      ngrok http 5000 --authtoken=$NGROK_TOKEN
      ```
    - Esto generará una URL pública (como `https://abcd1234.ngrok.io`) que se debe usar en el frontend de la aplicación Kotlin. Copia esta URL para configurarla en el siguiente paso.

2. **Configurar la URL del Backend en el Código de la Aplicación de Kotlin**
    - Abre el proyecto de Kotlin en Android Studio.
    - Navega a la clase `Environment` ubicada en `utn.appmoviles.apptrabajofinal.config`.
    - Modifica el valor de `BACKEND_URL` para que apunte a la URL pública de ngrok obtenida en el paso anterior:
      ```kotlin
      const val BACKEND_URL = "https://abcd1234.ngrok.io"
      ```
      Reemplaza `https://abcd1234.ngrok.io` con la URL generada por ngrok.

3. **Ejecutar la Aplicación de Kotlin**
    - Con la URL configurada, puedes ejecutar la aplicación desde Android Studio.
    - Selecciona el dispositivo donde deseas instalarla y haz clic en **Run** para instalarla en tu dispositivo móvil.

---

Estos pasos aseguran que la aplicación de Kotlin pueda comunicarse con el backend de Python, permitiendo todas las funcionalidades, como el envío de audios por WhatsApp, que dependen de la conexión con el servidor.