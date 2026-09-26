package com.example.appmovimientos.data.repository

import com.example.appmovimientos.data.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private fun getUid(): String? = auth.currentUser?.uid

    fun getCategoriesFlow(): Flow<List<Category>> = callbackFlow {
        val uid = getUid()
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = database.child("users").child(uid).child("categorias")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Category>()
                for (child in snapshot.children) {
                    val category = child.getValue(Category::class.java)
                    category?.let { lista.add(it.copy(id = child.key ?: "")) }
                }
                trySend(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun createCategory(category: Category): String {
        val uid = getUid() ?: throw IllegalStateException("Usuario no autenticado")
        val categoryId = database.child("users").child(uid).child("categorias").push().key!!
        val newCategory = category.copy(id = categoryId)
        database.child("users").child(uid).child("categorias").child(categoryId).setValue(newCategory).await()
        return categoryId
    }

    suspend fun updateCategory(category: Category) {
        val uid = getUid() ?: throw IllegalStateException("Usuario no autenticado")
        database.child("users").child(uid).child("categorias").child(category.id).setValue(category).await()
    }

    suspend fun deleteCategory(categoryId: String) {
        val uid = getUid() ?: throw IllegalStateException("Usuario no autenticado")
        database.child("users").child(uid).child("categorias").child(categoryId).removeValue().await()
    }
}
