package com.example.agendadigital

import adapters.UsuarioChatAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityConversacionesPrivadasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.UsuarioChat

/**
 * Activity que muestra una lista de usuarios disponibles para iniciar un chat privado.
 *
 * Comportamiento:
 * - Si el usuario actual es un profesor, carga usuarios con rol "padre".
 * - Si el usuario actual es un padre, carga usuarios con rol "profesor".
 *
 * Al seleccionar un usuario (funcionalidad futura), se inicia una conversación privada
 * entre ambos mediante el `ChatPrivadoActivity`.
 *
 * Utiliza Firebase Authentication para obtener el UID actual y Firestore
 * para recuperar la lista de usuarios según su rol.
 */
class ConversacionesPrivadasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversacionesPrivadasBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val listaUsuarios = mutableListOf<UsuarioChat>() // ✅ Clase Usuario ya la creamos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityConversacionesPrivadasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar RecyclerView
        binding.recyclerConversaciones.layoutManager = LinearLayoutManager(this)

        // Cargar usuarios con los que puede chatear
        cargarUsuariosParaConversar()
    }

    private fun cargarUsuariosParaConversar() {
        val miUid = auth.currentUser?.uid ?: return

        // Si es profesor, buscar padres
        // Si es padre, buscar profesores
        db.collection("usuarios").document(miUid).get()
            .addOnSuccessListener { documento ->
                val miRol = documento.getString("rol")

                val rolObjetivo = if (miRol == "profesor") "padre" else "profesor"

                db.collection("usuarios")
                    .whereEqualTo("rol", rolObjetivo)
                    .get()
                    .addOnSuccessListener { resultado ->
                        listaUsuarios.clear()
                        for (doc in resultado.documents) {
                            val nombre = doc.getString("nombre") ?: ""
                            val uid = doc.getString("uid") ?: continue
                            listaUsuarios.add(UsuarioChat(uid, nombre))
                        }

                        // Configurar adaptador con la lista de usuarios disponibles
                        binding.recyclerConversaciones.adapter = UsuarioChatAdapter(listaUsuarios) { usuario ->
                            val intent = Intent(this, ChatPrivadoActivity::class.java)
                            intent.putExtra("receptorUid", usuario.uid)
                            intent.putExtra("receptorNombre", usuario.nombre)
                            startActivity(intent)
                        }

                        Toast.makeText(this, "Cargados ${listaUsuarios.size} usuarios", Toast.LENGTH_SHORT).show()
                    }

                    .addOnFailureListener {
                        Toast.makeText(this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}