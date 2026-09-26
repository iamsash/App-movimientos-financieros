package com.example.appmovimientos.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

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

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun signOut() {
        auth.signOut()
    }
}
