package com.example.ecommerceapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.adapters.MyCartAdapter;
import com.example.ecommerceapp.models.MyCartModel;
import com.example.ecommerceapp.models.NewProductsModel;
import com.example.ecommerceapp.models.PopularProductsModel;
import com.example.ecommerceapp.models.ShowAllModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private double overAllTotalAmount;
    private TextView overAllAmount;
    private Button cart_buy_now;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<MyCartModel> cartModelList;
    private MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private NewProductsModel newProductsModel = null;
    private PopularProductsModel popularProductsModel = null;
    private ShowAllModel showAllModel = null;

    private final BroadcastReceiver totalAmountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double totalAmount = intent.getDoubleExtra("totalAmount", 0);
            overAllTotalAmount = totalAmount;
            overAllAmount.setText("Total Amount Rs " + totalAmount + " /-");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        cart_buy_now = findViewById(R.id.buy_now);
        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Object obj = getIntent().getSerializableExtra("detailed");

        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PopularProductsModel) {
            popularProductsModel = (PopularProductsModel) obj;
        } else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        toolbar.setNavigationOnClickListener(view -> finish());

        overAllAmount = findViewById(R.id.textView3);
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        fetchCartItems(); // Fetch the initial cart items

        cart_buy_now.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CartAddressActivity.class);
            intent.putExtra("totalBill", overAllTotalAmount);
            Log.d("CartActivity", "Total amount being passed: " + overAllTotalAmount);
            Toast.makeText(CartActivity.this, "Total Amount Rs " + overAllTotalAmount + " /-", Toast.LENGTH_SHORT).show();

            if (newProductsModel != null) {
                intent.putExtra("item", newProductsModel);
            }
            if (popularProductsModel != null) {
                intent.putExtra("item", popularProductsModel);
            }
            if (showAllModel != null) {
                intent.putExtra("item", showAllModel);
            }

            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(totalAmountReceiver, new IntentFilter("MyTotalAmount"));
    }

    private void fetchCartItems() {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartModelList.clear(); // Clear the list before adding new data
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                            cartModelList.add(myCartModel);
                            Log.d("CartActivity", "Fetched item: " + myCartModel.getProductName() + " Price: " + myCartModel.getTotalPrice());
                        }
                        cartAdapter.notifyDataSetChanged();
                        calculateTotalAmount();
                    } else {
                        Log.e("CartActivity", "Error fetching data", task.getException());
                    }
                });
    }

    private void calculateTotalAmount() {
        double totalAmount = 0;
        for (MyCartModel item : cartModelList) {
            totalAmount += item.getTotalPrice();
        }
        overAllTotalAmount = totalAmount;
        overAllAmount.setText("Total Amount Rs " + totalAmount + " /-");
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCartItems(); // Refresh the cart items when activity resumes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(totalAmountReceiver);
    }
}
