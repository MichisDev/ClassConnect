# 📚 ClassConnect

[![Calificación](https://img.shields.io/badge/Calificación-10/10-brightgreen)](https://github.com/MichisDev/ClassConnect)  
[![Lenguaje](https://img.shields.io/badge/Lenguaje-Kotlin-purple)](https://kotlinlang.org/)
[![Estado](https://img.shields.io/badge/Estado-Completado-blue)]()

ClassConnect es una agenda escolar digital diseñada para facilitar la comunicación entre alumnos, padres y profesores de educación primaria. Permite gestionar tareas, apuntes, eventos escolares, calificaciones y comunicación interna, mejorando la experiencia educativa.

🎓 Proyecto desarrollado como Trabajo de Fin de Grado (DAM) con calificación: **10/10**

---

## 🛠 Tecnologías utilizadas

- Kotlin  
- Firebase Authentication  
- Firestore  
- Android Studio  
- Patrón MVVM  
- XML (layouts)  
- Material Design (componentes visuales)  
- Iconografía de [Flaticon](https://www.flaticon.com/)  

---

## ✨ Funcionalidades principales

- 📅 Gestión completa de eventos escolares  
- ✍️ Toma de apuntes y seguimiento de tareas asignadas  
- 📊 Visualización de estadísticas de notas validadas  
- 🔔 Notificaciones personalizadas según rol (profesor, alumno, padre)  
- 🧑‍🏫 Gestión de roles diferenciados con acceso seguro  
- 💬 Chat interno (grupal y privado) para comunicación directa  
- 🔐 Registro seguro con validación mediante códigos  

---

## 🧠 Aprendizajes y retos

Este proyecto supuso un reto integral donde apliqué conocimientos técnicos y de diseño adquiridos durante el ciclo formativo, abarcando desde la arquitectura MVVM, la implementación de seguridad y roles, hasta el manejo de comunicación en tiempo real y diseño eficiente de bases de datos en la nube.

---

## 📷 Capturas de pantalla

Puedes ver capturas detalladas en la [guía de usuario dentro del documento](docs/TFG_MichelleVelasquez.pdf).

---

## 🚀1. Cómo ejecutar el proyecto

- Clona el repositorio:  
   ```bash
   git clone https://github.com/MichisDev/ClassConnect.git

## 🔐 2. Configuración de Firebase (`google-services.json`)

Este proyecto utiliza Firebase para autenticación y otras funcionalidades.  
Por motivos de seguridad, el archivo real `google-services.json` **no está incluido** en el repositorio (está ignorado por `.gitignore`).

### ➕ ¿Cómo configurarlo?

1. Ve a [Firebase Console](https://console.firebase.google.com/).
2. Selecciona tu proyecto o crea uno nuevo.
3. Haz clic en **"Agregar app"** (Android) y registra el mismo `package name`:  
4. Descarga el archivo `google-services.json` que se te proporciona.
5. Colócalo en la siguiente ruta dentro del proyecto:  

### 🧪 Archivo de ejemplo

Este repositorio contiene un archivo `google-services-example.json` como guía de estructura.  
🔸 **No funcionará en producción**, solo sirve como plantilla para ayudarte a crear el tuyo.

