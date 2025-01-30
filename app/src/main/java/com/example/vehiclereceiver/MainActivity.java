package com.example.vehiclereceiver;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvSpeed;
    private Button btnSendWarning;
    private DatabaseReference speedRef;
    private DatabaseReference warningRef;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSpeed = findViewById(R.id.tv_speed);
        btnSendWarning = findViewById(R.id.btnSendWarning);

        speedRef = FirebaseDatabase.getInstance().getReference("vehicle/speed");
        warningRef = FirebaseDatabase.getInstance().getReference("vehicle/warning");

        speedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String speedStr = snapshot.getValue(String.class);
                    // Fetch the speed value
                    int speed = Integer.parseInt(speedStr); // Convert String to Integer
                    tvSpeed.setText("Current Speed: " + speed + " km/h");

                } else {
                    Toast.makeText(MainActivity.this, "No speed data available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to fetch speed data!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendWarning.setOnClickListener(v -> {
            warningRef.setValue("true")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Warning sent!", Toast.LENGTH_SHORT).show();

                        // Turn off warning after 3 seconds
                        handler.postDelayed(() -> warningRef.setValue("false")
                                        .addOnSuccessListener(aVoid1 -> Toast.makeText(MainActivity.this, "Warning cleared!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to clear warning!", Toast.LENGTH_SHORT).show())
                                , 3000);
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to send warning!", Toast.LENGTH_SHORT).show());
        });
    }
}