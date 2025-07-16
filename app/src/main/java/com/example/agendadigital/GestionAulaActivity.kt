package com.example.agendadigital

import adapters.SolicitudesAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agendadigital.databinding.ActivityGestionAulaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import model.SolicitudUsuPadre

/**
 * Actividad encargada de gestionar el aula del profesor.
 * Permite visualizar y regenerar el código de aula y gestionar las solicitudes de padres.
 */
class GestionAulaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionAulaBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: SolicitudesAdapter
    private var claseProfesor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGestionAulaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrarCodigoAula()

        binding.btnGenerarNuevoCodigo.setOnClickListener {
            claseProfesor?.let { clase ->
                generarNuevoCodigo(clase)
            } ?: Toast.makeText(this, "Clase no disponible", Toast.LENGTH_SHORT).show()
        }

        adapter = SolicitudesAdapter(onAceptar = { solicitud ->
            val email = solicitud.email
            val password = solicitud.password
            val nombre = solicitud.nombre
            val solicitudId = solicitud.id

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val uid = auth.currentUser!!.uid

                val datosUsuario = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "nombre" to nombre,
                    "rol" to "padre"
                )

                db.collection("usuarios").document(uid).set(datosUsuario).addOnSuccessListener {
                    db.collection("solicitudes").document(solicitudId).update(
                        "estado", "aceptada",
                        "password", FieldValue.delete()
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Padre aprobado y cuenta creada", Toast.LENGTH_SHORT).show()
                        cargarSolicitudes()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al crear cuenta: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }, onRechazar = { solicitudId ->
            db.collection("solicitudes").document(solicitudId).update("estado", "rechazada")
                .addOnSuccessListener {
                    Toast.makeText(this, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                    cargarSolicitudes()
                }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        cargarSolicitudes()
    }

    /**
     * Muestra el código del aula si existe. Si no, avisa al profesor para generarlo.
     */
    private fun mostrarCodigoAula() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(uid).get().addOnSuccessListener { userDoc ->
            val clase = userDoc.getString("clase")
            if (clase != null) {
                claseProfesor = clase
                db.collection("codigos")
                    .whereEqualTo("clase", clase)
                    .whereEqualTo("tipo", "padre")
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val codigoExistente = result.documents[0].id // ✅ ID del doc = código
                            binding.tvCodigoAula.text = "Código del Aula: $codigoExistente"
                        } else {
                            binding.tvCodigoAula.text = "No hay código generado aún"
                            Toast.makeText(this, "Pulsa para generar un código nuevo", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "No se encontró la clase del profesor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Genera un nuevo código y lo guarda en la colección codigos con el ID como código.
     */
    private fun generarNuevoCodigo(clase: String) {
        val nuevoCodigo = "CLASE${clase.uppercase()}2025"
        val datosCodigo = mapOf(
            "clase" to clase,
            "tipo" to "padre",
            "limite" to 3,
            "usado" to false
        )

        db.collection("codigos").document(nuevoCodigo).set(datosCodigo).addOnSuccessListener {
            binding.tvCodigoAula.text = "Código del Aula: $nuevoCodigo"
            Toast.makeText(this, "Código creado correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Carga las solicitudes pendientes desde Firestore.
     */
    private fun cargarSolicitudes() {
        db.collection("solicitudes").whereEqualTo("estado", "pendiente").get()
            .addOnSuccessListener { result ->
                val solicitudes = result.documents.map { doc ->
                    SolicitudUsuPadre(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        email = doc.getString("email") ?: "",
                        password = doc.getString("password") ?: ""
                    )
                }
                adapter.submitList(solicitudes)
                binding.tvSinSolicitudes.visibility =
                    if (solicitudes.isEmpty()) View.VISIBLE else View.GONE
            }
    }
}
