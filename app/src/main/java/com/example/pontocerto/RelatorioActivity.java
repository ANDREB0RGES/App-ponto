package com.example.pontocerto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RelatorioActivity extends AppCompatActivity {

    private TextView txtRelatorio;
    private DatabaseReference referencia;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        txtRelatorio = findViewById(R.id.txtRelatorio);
        auth = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference("Pontos");

        carregarRelatorio();
    }

    private void carregarRelatorio() {
        String userId = auth.getCurrentUser().getUid();

        referencia.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder relatorio = new StringBuilder();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    String dataHora = snap.child("dataHora").getValue(String.class);
                    Double lat = snap.child("latitude").getValue(Double.class);
                    Double lon = snap.child("longitude").getValue(Double.class);

                    relatorio.append(String.format(Locale.getDefault(),
                            "Data/Hora: %s\nLocalização: %.5f, %.5f\n\n",
                            dataHora, lat, lon));
                }
                txtRelatorio.setText(relatorio.toString());
            } else {
                txtRelatorio.setText("Erro ao carregar relatório.");
            }
        });
    }
}
