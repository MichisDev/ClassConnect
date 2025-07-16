package com.example.agendadigital

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityChatGrupalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import model.MensajeGrupal
import adapters.ChatGrupalAdapter
import model.Usuario

/**
 * Actividad que gestiona el chat grupal entre padres y el tutor.
 * Muestra los mensajes y permite al usuario enviar nuevos en tiempo real.
 */
class ChatGrupalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatGrupalBinding
    private val db = FirebaseFirestore.getInstance()
    private val mensajes = mutableListOf<MensajeGrupal>()
    private lateinit var adapter: ChatGrupalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGrupalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView con su adaptador y layout manager
        adapter = ChatGrupalAdapter(mensajes)
        binding.recyclerViewMensajes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMensajes.adapter = adapter

        // Enviar un mensaje al pulsar el botón
        binding.btnEnviar.setOnClickListener {
            val texto = binding.etMensaje.text.toString()
            if (texto.isNotEmpty()) {
                enviarMensaje(texto)
            }
        }

        // Inicia escucha en tiempo real de los mensajes
        escucharMensajes()
    }

    /**
     * Envía un mensaje al chat grupal, usando el nombre del usuario autenticado.
     *
     * @param texto Contenido del mensaje a enviar.
     */
    private fun enviarMensaje(texto: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    val usuario = document.toObject(Usuario::class.java)
                    val nombreUsuario = usuario?.nombre ?: "Usuario"

                    val mensaje = MensajeGrupal(
                        contenido = texto,
                        autor = nombreUsuario,
                        autorId = uid,
                        timestamp = System.currentTimeMillis()
                    )


                    db.collection("chatGrupal")
                        .add(mensaje)
                        .addOnSuccessListener {
                            binding.etMensaje.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Escucha los mensajes del chat grupal en tiempo real y los actualiza en la lista.
     */
    private fun escucharMensajes() {
        db.collection("chatGrupal")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val nuevos = snapshot?.documents?.mapNotNull {
                    it.toObject(MensajeGrupal::class.java)
                } ?: emptyList()

                mensajes.clear()
                mensajes.addAll(nuevos)
                adapter.notifyDataSetChanged()
                binding.recyclerViewMensajes.scrollToPosition(mensajes.size - 1)
            }
    }
}
