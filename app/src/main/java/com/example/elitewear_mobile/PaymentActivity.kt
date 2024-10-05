package com.example.elitewear_mobile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.elitewear_mobile.Network.ApiClient
import com.example.elitewear_mobile.models.Payment
class PaymentActivity : AppCompatActivity() {

    private lateinit var addButton: Button
    private lateinit var cardTypeSpinner: Spinner
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
        cardTypeSpinner = findViewById(R.id.cardTypeSpinner)
        amountInput = findViewById(R.id.amountInput)
        billingAddressInput = findViewById(R.id.billingAddressInput)
        expireDateInput = findViewById(R.id.expireDateInput)

        // Set the total price to the amount input field
        amountInput.setText(String.format("%.2f", totalPrice)) // Format to 2 decimal places

        // Set up the card type spinner with "Select Card Type" as the default
        val cardTypes = arrayOf("Select Card Type", "Visa", "MasterCard", "Amex", "Discover")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cardTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardTypeSpinner.adapter = adapter

        // Add new payment
        addButton.setOnClickListener {
            val cardType = cardTypeSpinner.selectedItem.toString()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val billingAddress = billingAddressInput.text.toString()
            val expireDate = expireDateInput.text.toString()

            if (validateInput(cardType, amount, billingAddress, expireDate)) {
                val newPayment = Payment(cardType = cardType, amount = amount, billingAddress = billingAddress, expireDate = expireDate)

                ApiClient.addPayment(newPayment) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Payment added successfully", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to add payment", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_CANCELED)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to validate input
    private fun validateInput(cardType: String, amount: Double, billingAddress: String, expireDate: String): Boolean {
        var isValid = true

        // Validate card type
        if (cardType == "Select Card Type") {
            Toast.makeText(this, "Please select a valid card type", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validate amount
        if (amount <= 0.0) {
            amountInput.error = "Please enter a valid amount"
            isValid = false
        }

        // Validate billing address
        if (billingAddress.isEmpty()) {
            billingAddressInput.error = "Billing address is required"
            isValid = false
        }

        // Validate expire date using Regex pattern (MM/YY)
        val expireDatePattern = Regex("(0[1-9]|1[0-2])/[0-9]{2}")
        if (expireDateInput.text.isNullOrEmpty()) {
            expireDateInput.error = "Expire Date is required"
            isValid = false
        } else if (!expireDatePattern.matches(expireDateInput.text.toString())) {
            expireDateInput.error = "Enter a valid date (MM/YY)"
            isValid = false
        }

        return isValid
    }

}

