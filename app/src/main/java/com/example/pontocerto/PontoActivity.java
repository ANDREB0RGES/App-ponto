package com.example.pontocerto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PontoActivity extends AppCompatActivity {

    private Button btnRegistrarPonto;
    private TextView txtStatus;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponto);

        btnRegistrarPonto = findViewById(R.id.btnRegistrarPonto);
        txtStatus = findViewById(R.id.txtStatus);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();

        btnRegistrarPonto.setOnClickListener(v -> registrarPonto());
    }

    private void registrarPonto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        salvarPonto(location);
                    } else {
                        txtStatus.setText("Não foi possível obter localização.");
                    }
                });
    }

    private void salvarPonto(Location location) {
        String userId = auth.getCurrentUser().getUid();
        String dataHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> ponto = new HashMap<>();
        ponto.put("dataHora", dataHora);
        ponto.put("latitude", location.getLatitude());
        ponto.put("longitude", location.getLongitude());

        FirebaseDatabase.getInstance().getReference("Pontos")
                .child(userId)
                .push()
                .setValue(ponto)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        txtStatus.setText("Ponto registrado em: " + dataHora);
                    } else {
                        txtStatus.setText("Erro ao registrar ponto.");
                    }
                });
    }
}
