package com.example.agendadigital

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio que extiende {@link FirebaseMessagingService} para gestionar la recepción de mensajes
 * push enviados desde Firebase Cloud Messaging (FCM).
 *
 * Funcionalidades:
 * - Detecta mensajes entrantes, ya sea con carga de datos o de notificación.
 * - Muestra una notificación en el dispositivo cuando se recibe un mensaje válido.
 * - Crea un canal de notificación si es necesario (Android 8+).
 *
 * Estructura de los mensajes:
 * - Mensajes con payload de datos: `remoteMessage.data["title"]`, `remoteMessage.data["body"]`.
 * - Mensajes con payload de notificación: `remoteMessage.notification.title`, `.body`.
 *
 * Permisos necesarios:
 * - A partir de Android 13, requiere el permiso `POST_NOTIFICATIONS` para mostrar notificaciones.
 *
 * Uso en el proyecto:
 * - Esta clase se activa automáticamente cuando se recibe un mensaje push desde Firebase,
 *   incluso si la app está en segundo plano.
 *
 * Requisitos:
 * - Firebase Cloud Messaging debe estar correctamente configurado en el proyecto.
 * - Se debe registrar este servicio en el `AndroidManifest.xml`.
 *
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 🔹 Si viene como payload de datos
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            mostrarNotificacion(title, body)
        }

        // 🔹 Si viene como payload de notificación
        remoteMessage.notification?.let {
            val title = it.title
            val body = it.body
            mostrarNotificacion(title, body)
        }
    }

    private fun mostrarNotificacion(title: String?, body: String?) {
        val channelId = "evento_channel"
        val channelName = "Notificaciones de Eventos"

        // 🔸 Crear canal de notificación (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones de eventos y avisos escolares"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // 🔸 Crear notificación
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notificacion) // Asegúrate de tener este recurso
            .setContentTitle(title ?: "Nuevo mensaje")
            .setContentText(body ?: "Tienes una nueva notificación.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        // 🔸 Comprobar permisos (Android 13+)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // 🔸 Mostrar notificación (ID = 0, puedes usar otro si necesitas distinguirlas)
        notificationManager.notify(0, builder.build())
    }
}
