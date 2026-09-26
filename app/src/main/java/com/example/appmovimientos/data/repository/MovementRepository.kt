package com.example.appmovimientos.data.repository

import com.example.appmovimientos.data.model.Account
import com.example.appmovimientos.data.model.Category
import com.example.appmovimientos.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MovementRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private fun getUid(): String? = auth.currentUser?.uid

    fun getMovimientosFlow(): Flow<List<Expense>> = callbackFlow {
        val uid = getUid()
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = database.child("users").child(uid).child("movimientos")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Expense>()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expense::class.java)
                    expense?.let { lista.add(it.copy(id = child.key ?: "")) }
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

    fun getCategoriasFlow(): Flow<List<Category>> = callbackFlow {
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

    fun getCuentasFlow(): Flow<List<Account>> = callbackFlow {
        val uid = getUid()
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = database.child("users").child(uid).child("cuentas")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Account>()
                for (child in snapshot.children) {
                    val account = child.getValue(Account::class.java)
                    account?.let { lista.add(it.copy(id = child.key ?: "")) }
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

    suspend fun createMovement(expense: Expense, account: Account) {
        val uid = getUid() ?: throw IllegalStateException("Usuario no autenticado")
        val movId = database.child("users").child(uid).child("movimientos").push().key!!
        val movimiento = expense.copy(id = movId)

        database.child("users").child(uid).child("movimientos").child(movId).setValue(movimiento).await()

        val montoDouble = expense.amount.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
        val nuevoSaldo = if (expense.type == "Gasto") {
            account.balance - montoDouble
        } else {
            account.balance + montoDouble
        }

        database.child("users").child(uid).child("cuentas").child(account.id).child("balance").setValue(nuevoSaldo).await()
    }

    suspend fun deleteMovement(expense: Expense, account: Account?) {
        val uid = getUid() ?: throw IllegalStateException("Usuario no autenticado")

        if (account != null) {
            val montoDouble = expense.amount.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
            val nuevoSaldo = if (expense.type == "Gasto") {
                account.balance + montoDouble
            } else {
                account.balance - montoDouble
            }

            database.child("users").child(uid).child("cuentas").child(account.id).child("balance").setValue(nuevoSaldo).await()
        }

        database.child("users").child(uid).child("movimientos").child(expense.id).removeValue().await()
    }
}
