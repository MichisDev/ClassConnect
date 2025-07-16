package com.example.agendadigital

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Activity que gestiona el registro de un nuevo alumno en la aplicación.
 *
 * Funcionalidades:
 * - Permite al alumno introducir su nombre, curso, correo electrónico y contraseña.
 * - Realiza la autenticación y registro con Firebase Authentication.
 * - Guarda los datos del alumno en la colección "usuarios" de Firestore con el rol "alumno".
 *
 * Componentes utilizados:
 * - {@link FirebaseAuth} para el registro con email y contraseña.
 * - {@link FirebaseFirestore} para almacenar los datos del alumno.
 *
 * Validaciones:
 * - Comprueba que todos los campos estén completos antes de registrar.
 *
 * Navegación:
 * - Tras un registro exitoso, redirige al usuario a {@link MainActivity}.
 *
 * Layout relacionado:
 * - activity_register_alumno.xml
 *
 * Requisitos:
 * - Conexión activa a Internet.
 * - Firebase configurado correctamente en el proyecto.
 */
class RegisterAlumnoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_alumno)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nombre = findViewById<EditText>(R.id.etNombre)
        val curso = findViewById<EditText>(R.id.etCurso)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val loginText = findViewById<TextView>(R.id.loginText)

        btnRegistrar.setOnClickListener {
            val nombreTxt = nombre.text.toString().trim()
            val cursoTxt = curso.text.toString().trim()
            val emailTxt = email.text.toString().trim()
            val passTxt = password.text.toString().trim()

            if (nombreTxt.isEmpty() || cursoTxt.isEmpty() || emailTxt.isEmpty() || passTxt.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailTxt, passTxt)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid

                    val alumnoData = mapOf(
                        "nombre" to nombreTxt,
                        "curso" to cursoTxt,
                        "email" to emailTxt,
                        "rol" to "alumno"
                    )

                    db.collection("usuarios").document(uid).set(alumnoData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Alumno registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
