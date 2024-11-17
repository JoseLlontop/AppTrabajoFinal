# Guía para Ejecutar la Aplicación de Kotlin

Esta aplicación de Kotlin necesita que el backend en Python esté activo y accesible a través de la red local. Sigue los pasos a continuación para configurar y ejecutar correctamente la aplicación y su backend.

### Pasos para Configurar y Ejecutar

1. **Ejecutar el Backend de Python**
   - Navega al directorio del proyecto del backend en Python.
   - Inicia el servidor con el siguiente comando:
     ```bash
     python app.py
     ```

2. **Configurar la URL del Backend en el Código de la Aplicación de Kotlin**
   - Abre el proyecto de Kotlin en Android Studio.
   - Navega a la clase `Environment` ubicada en `utn.appmoviles.apptrabajofinal.config`.
   - Modifica el valor de `BACKEND_URL` para que apunte a la dirección IP de tu computadora y el puerto donde se ejecuta el backend. Por ejemplo:
     ```kotlin
     const val BACKEND_URL = "http://192.168.100.99:5000"
     ```
     Asegúrate de reemplazar `192.168.100.99` con la dirección IP de tu computadora en la red local.

3. **Instalar y Ejecutar la Aplicación en tu Dispositivo Móvil**
   - Asegúrate de que tu dispositivo móvil esté conectado a la misma red local que tu computadora.
   - Desde Android Studio, selecciona el dispositivo móvil como destino y haz clic en **Run** para instalar y ejecutar la aplicación.
   - No es necesario conectar el celular por cable ni usar el emulador de Android Studio, ya que la aplicación puede comunicarse con el backend directamente a través de la red local.

---

Estos pasos aseguran que la aplicación de Kotlin pueda comunicarse correctamente con el backend de Python para realizar sus funcionalidades.