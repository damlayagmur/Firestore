package com.damlayagmur.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val personCollectionRef = Firebase.firestore.collection("persons")
    lateinit var saveData: Button
    lateinit var retrieveData: Button
    lateinit var nameEt: EditText
    lateinit var surnameEt: EditText
    lateinit var ageEt: EditText
    lateinit var personTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveData = findViewById(R.id.saveData)
        retrieveData = findViewById(R.id.retrieveData)
        nameEt = findViewById(R.id.name)
        surnameEt = findViewById(R.id.surname)
        ageEt = findViewById(R.id.age)
        personTv = findViewById(R.id.personTv)

        saveData.setOnClickListener {
            val name = nameEt.text.toString()
            val surname = surnameEt.text.toString()
            val age = ageEt.text.toString().toInt()
            val person = Person(name, surname, age)
            savePerson(person)
        }

        retrieveData.setOnClickListener {
            retrievePerson()
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Successfully saved data", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun retrievePerson() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = personCollectionRef.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<Person>()
                sb.append("$person\n")
                withContext(Dispatchers.Main) {
                    personTv.text = sb.toString()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}