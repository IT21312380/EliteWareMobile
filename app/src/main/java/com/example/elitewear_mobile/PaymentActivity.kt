package com.example.elitewear_mobile

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.Payment

class PaymentActivity : AppCompatActivity() {

    private lateinit var addButton: Button
    private lateinit var cardTypeInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var billingAddressInput: EditText
    private lateinit var expireDateInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Retrieve the total price from the intent
        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)

        // Initialize views
        addButton = findViewById(R.id.addPaymentButton)
        cardTypeInput = findViewById(R.id.cardTypeInput)
        amountInput = findViewById(R.id.amountInput)
        billingAddressInput = findViewById(R.id.billingAddressInput)
        expireDateInput = findViewById(R.id.expireDateInput)

        // Set the total price to the amount input field
        amountInput.setText(String.format("%.2f", totalPrice)) // Format to 2 decimal places

        // Add new payment
        addButton.setOnClickListener {
            val cardType = cardTypeInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val billingAddress = billingAddressInput.text.toString()
            val expireDate = expireDateInput.text.toString()

            val newPayment = Payment(cardType = cardType, amount = amount, billingAddress = billingAddress, expireDate = expireDate)

            ApiClient.addPayment(newPayment) { success ->
                runOnUiThread {
                    if (success) {
                        // Return success to CartActivity
                        Toast.makeText(this, "Payment added successfully", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish() // Close PaymentActivity
                    } else {
                        // Return failure to CartActivity
                        Toast.makeText(this, "Failed to add payment", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_CANCELED)
                    }
                }
            }
        }
    }
}
