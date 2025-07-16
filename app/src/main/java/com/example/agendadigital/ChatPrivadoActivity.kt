package com.example.agendadigital

import adapters.ChatPrivadoAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityChatPrivadoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.MensajePrivado

/**
 * Activity que gestiona el chat privado entre dos usuarios.
 *
 * Esta pantalla permite:
 * - Mostrar el nombre del receptor (otro usuario).
 * - Visualizar los mensajes privados ordenados por tiempo.
 * - Enviar nuevos mensajes que se almacenan en Firestore.
 *
 * La conversación se identifica mediante un ID único generado
 * a partir de los UID de los dos participantes.
 *
 * Usa Firebase Authentication para identificar al usuario actual
 * y Firestore para leer/escribir mensajes y datos de usuario.
 */
class ChatPrivadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatPrivadoBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: ChatPrivadoAdapter

    private lateinit var receptorId: String
    private lateinit var receptorNombre: String
    private lateinit var miNombre: String
    private lateinit var conversacionId: String
    private val mensajes = mutableListOf<MensajePrivado>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatPrivadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del intent
        receptorId = intent.getStringExtra("receptorUid") ?: return finish()
        receptorNombre = intent.getStringExtra("receptorNombre") ?: "Desconocido"
        println("👀 Receptor ID: $receptorId")
        println("👀 Receptor Nombre: $receptorNombre")

        // Mostrar en pantalla
        binding.tvNombreReceptor.text = if (receptorNombre == "Desconocido") {
            "Chat privado"
        } else {
            "Chat con $receptorNombre"
        }

        // Inicializar RecyclerView
        adapter = ChatPrivadoAdapter(mensajes, auth.currentUser?.uid ?: "")
        binding.recyclerViewChatPrivado.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChatPrivado.adapter = adapter

        // Calcular ID único de conversación (orden alfabético)
        val actualId = auth.currentUser!!.uid
        conversacionId = if (actualId < receptorId) {
            "$actualId-$receptorId"
        } else {
            "$receptorId-$actualId"
        }

        // Obtener mi nombre y empezar a escuchar
        obtenerMiNombre {
            escucharMensajes()
        }

        // Enviar mensaje
        binding.btnEnviarPrivado.setOnClickListener {
            val texto = binding.etMensajePrivado.text.toString().trim()
            if (texto.isNotEmpty()) {
                val mensaje = MensajePrivado(
                    contenido = texto,
                    autorId = actualId,
                    receptorId = receptorId,
                    timestamp = System.currentTimeMillis(),
                    autorNombre = miNombre,
                    conversacionId = conversacionId
                )

                val nuevoId = db.collection("mensajesPrivados").document().id
                db.collection("mensajesPrivados").document(nuevoId)
                    .set(mensaje)
                    .addOnSuccessListener {
                        binding.etMensajePrivado.text.clear()
                        println("✅ Mensaje enviado correctamente")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                        println("❌ Error al enviar mensaje: ${it.message}")
                    }
            }
        }
    }

    /**
     * Recupera el nombre del usuario autenticado desde la colección "usuarios".
     * Este nombre se usará como autor de los mensajes.
     *
     * @param callback Función a ejecutar una vez obtenido el nombre.
     */
    private fun obtenerMiNombre(callback: () -> Unit) {
        val uid = auth.currentUser!!.uid
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                miNombre = doc.getString("nombre") ?: "Yo"
                callback()
            }
            .addOnFailureListener {
                miNombre = "Yo"
                callback()
            }
    }

    /**
     * Escucha en tiempo real los mensajes asociados a una conversación privada.
     * Utiliza el ID de conversación calculado entre el usuario actual y el receptor.
     * Actualiza el RecyclerView cada vez que se reciben cambios.
     */
    private fun escucharMensajes() {
        println("📡 Escuchando conversación: $conversacionId")
        db.collection("mensajesPrivados")
            .whereEqualTo("conversacionId", conversacionId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ Error en snapshot: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val nuevosMensajes = snapshot.toObjects(MensajePrivado::class.java)
                    println("📥 Mensajes recibidos: ${nuevosMensajes.size}")

                    mensajes.clear()
                    mensajes.addAll(nuevosMensajes)
                    //adapter.actualizarMensajes(mensajes)

                    if (mensajes.isNotEmpty()) {
                        binding.recyclerViewChatPrivado.scrollToPosition(mensajes.size - 1)
                    }
                }
            }
    }
}
