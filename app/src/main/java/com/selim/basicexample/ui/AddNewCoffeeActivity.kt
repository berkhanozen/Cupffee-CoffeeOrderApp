package com.selim.basicexample.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.selim.basicexample.R
import com.selim.basicexample.model.Coffee
import kotlinx.android.synthetic.main.activity_add_new_address.*
import kotlinx.android.synthetic.main.activity_add_new_coffee.*
import kotlinx.android.synthetic.main.coffee_item_row.*

class AddNewCoffeeActivity : AppCompatActivity() {

    private var firestore: FirebaseFirestore? = null

    private var coffee: Coffee? = null
    private var categoryId: String = ""
    private val etCoffeeName by lazy { findViewById<EditText>(R.id.editText_kahveAdi) }
    private val etCoffeePrice by lazy {findViewById<EditText>(R.id.editText_kahveFiyati) }
    private val etCoffeeImg by lazy {findViewById<EditText>(R.id.editText_kahveImageUrl) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_coffee)

        categoryId = intent.getStringExtra("CATEGORY_ID") ?: ""
        coffee = intent.getSerializableExtra("COFFEE") as Coffee?

        coffee?.let {
            bindCoffee()
        }

        firestore = FirebaseFirestore.getInstance()

        btn_kahveEkle.setOnClickListener {
            if (etCoffeeName.text.isEmpty()) {
                etCoffeeName.error = "Kahve adı boş geçilemez"
                etCoffeeName.requestFocus()
                return@setOnClickListener
            }

            if (etCoffeePrice.text.isEmpty()) {
                etCoffeePrice.error = "Kahve fiyatı boş geçilemez"
                etCoffeePrice.requestFocus()
                return@setOnClickListener
            }
            if (etCoffeeImg.text.isEmpty()) {
                etCoffeeImg.error = "Resim ekleyiniz"
                etCoffeeImg.requestFocus()
                return@setOnClickListener
            }


            if (coffee == null) {
                addNewCoffee()
            } else {
                updateCoffee()
            }
        }
    }

    private fun updateCoffee() {
        coffee?.id?.let { coffeeId ->

            coffee?.apply {
                name = etCoffeeName.text.toString()
                price = etCoffeePrice.text.toString()
                imageUrl = etCoffeeImg.text.toString()
            }

            firestore?.collection("category")?.document(categoryId)?.collection("coffees")
                ?.document(coffeeId)
                ?.set(
                    coffee!!
                )?.addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> {
                            Toast.makeText(
                                this,
                                "${coffee?.name} kahvesi Güncellendi..",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }
                        false -> Toast.makeText(
                            this,
                            "${coffee?.name} kahvesi Güncellenemedi..",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    private fun bindCoffee() {
        editText_kahveAdi.setText(coffee?.name)
        editText_kahveFiyati.setText(coffee?.price)
        editText_kahveImageUrl.setText(coffee?.imageUrl)
        btn_kahveEkle.text = "Güncelle"
    }

    private fun addNewCoffee() {

        val coffee = Coffee().apply {
            name = editText_kahveAdi.text.toString()
            price = editText_kahveFiyati.text.toString()
            imageUrl = editText_kahveImageUrl.text.toString()
        }

        firestore?.collection("category")?.document(categoryId)?.collection("coffees")?.add(coffee)
            ?.addOnCompleteListener { task ->

                when (task.isSuccessful) {
                    true -> {
                        Toast.makeText(
                            this,
                            "${coffee.name} kahvesi Eklendi..",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    false -> Toast.makeText(
                        this,
                        "${coffee.name} kahvesi Eklenemedi..",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }
}