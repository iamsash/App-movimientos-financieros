package com.example.appmovimientos.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun signInWithEmailAndPassword(email: String, pass: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, pass).await()
    }

    suspend fun createUserWithEmailAndPassword(email: String, pass: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, pass).await()
    }

    suspend fun saveUserData(uid: String, userData: Map<String, Any>) {
        database.child("users").child(uid).setValue(userData).await()
    }

    suspend fun checkUserAcceptance(uid: String): Boolean {
        val snapshot = database.child("users").child(uid).child("aceptacionTerminos").get().await()
        val aceptado = snapshot.child("aceptado").getValue(Boolean::class.java) ?: false
        val versionTerminos = snapshot.child("versionTerminos").getValue(String::class.java)
        val versionPrivacidad = snapshot.child("versionPrivacidad").getValue(String::class.java)
        return aceptado && versionTerminos == "1.0" && versionPrivacidad == "1.0"
    }

    suspend fun updateTerminosAcceptance(uid: String) {
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val aceptacionMap = mapOf(
            "aceptado" to true,
            "versionTerminos" to "1.0",
            "versionPrivacidad" to "1.0",
            "fechaAceptacion" to fechaActual
        )
        database.child("users").child(uid).child("aceptacionTerminos").setValue(aceptacionMap).await()
    }

    suspend fun getEmailByUsername(username: String): String? {
        val snapshot = database.child("users").orderByChild("usuario").equalTo(username).get().await()
        for (child in snapshot.children) {
            val email = child.child("correo").getValue(String::class.java)
            if (email != null) return email
        }
        return null
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun signOut() {
        auth.signOut()
    }
}
