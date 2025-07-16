package com.example.agendadigital

import adapters.UsuarioChatAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivitySeleccionarUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.UsuarioChat

/**
 * Actividad que permite al usuario seleccionar otro usuario con un rol diferente
 * para iniciar una conversación privada.
 *
 * Esta pantalla muestra una lista de usuarios disponibles (con el rol opuesto)
 * mediante un RecyclerView. Al seleccionar uno, se abre el chat privado con él.
 *
 * Requiere que se le pase un parámetro por intent llamado "rolContrario" para
 * filtrar qué tipo de usuarios mostrar.
 *
 * @author TuNombre
 */
class SeleccionarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccionarUsuarioBinding
    private lateinit var adapter: UsuarioChatAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val listaUsuarios = mutableListOf<UsuarioChat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySeleccionarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el adapter con la lista de usuarios
        adapter = UsuarioChatAdapter(listaUsuarios) { usuario ->
            val intent = Intent(this, ChatPrivadoActivity::class.java)
            intent.putExtra("receptorUid", usuario.uid)
            intent.putExtra("receptorNombre", usuario.nombre)
            startActivity(intent)
        }

        // Configurar el RecyclerView
        binding.recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsuarios.adapter = adapter

        cargarUsuarios()
    }

    /**
     * Carga desde Firestore los usuarios que tienen un rol opuesto al del usuario actual.
     * Excluye al usuario autenticado para no listarlo a sí mismo.
     */
    private fun cargarUsuarios() {
        val miUid = auth.currentUser?.uid ?: return
        val rolContrario = intent.getStringExtra("rolContrario") ?: return

        db.collection("usuarios")
            .whereEqualTo("rol", rolContrario)
            .get()
            .addOnSuccessListener { result ->
                listaUsuarios.clear()
                for (doc in result) {
                    val uid = doc.getString("uid") ?: continue
                    val nombre = doc.getString("nombre") ?: "Sin nombre"
                    if (uid != miUid) { // Evitar que se liste a sí mismo
                        listaUsuarios.add(UsuarioChat(uid, nombre))
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
    }
}
