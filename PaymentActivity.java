package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;

public class PaymentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView subTotal, discount, shipping, total;
    Button checkoutButton;
    double amount = 0.0;
    String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        // Toolbar
        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> finish());

        // Retrieve the amount passed from the previous activity
        amount = getIntent().getDoubleExtra("amount", 0.0);
        mAddress = getIntent().getStringExtra("address");

        // Initialize TextViews
        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.textView17);
        shipping = findViewById(R.id.textView18);
        total = findViewById(R.id.total_amt);

        // Set the amount in the subtotal TextView
        subTotal.setText("Rs " + amount);
        discount.setText("Rs  0.00/-" );
        shipping.setText("Rs 150.00/-");

        amount=amount+150;
        total.setText("Rs " + amount);

        // Initialize the checkout button
        checkoutButton = findViewById(R.id.pay_btn);
        // Create an Intent to go to the ModeOfPaymentActivity
        Intent intent = new Intent(PaymentActivity.this, ModeOfPaymentActivity.class);
        intent.putExtra("totalBill", amount); // Pass the total amount to ModeOfPaymentActivity
        intent.putExtra("address", mAddress); // Pass the selected address as well
        startActivity(intent);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
