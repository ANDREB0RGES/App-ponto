package com.example.pontocerto;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnPonto, btnAtestado, btnRelatorio, btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPonto = findViewById(R.id.btnPonto);
        btnAtestado = findViewById(R.id.btnAtestado);
        btnRelatorio = findViewById(R.id.btnRelatorio);
        btnSair = findViewById(R.id.btnSair);

        btnPonto.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PontoActivity.class));
        });

        btnAtestado.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AtestadoActivity.class));
        });

        btnRelatorio.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RelatorioActivity.class));
        });

        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
