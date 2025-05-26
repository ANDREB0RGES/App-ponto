package com.example.pontocerto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AtestadoActivity extends AppCompatActivity {

    private Button btnSelecionar, btnEnviar;
    private TextView txtArquivo;
    private Uri arquivoUri;

    private FirebaseAuth auth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atestado);

        btnSelecionar = findViewById(R.id.btnSelecionar);
        btnEnviar = findViewById(R.id.btnEnviar);
        txtArquivo = findViewById(R.id.txtArquivo);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnSelecionar.setOnClickListener(v -> selecionarArquivo());
        btnEnviar.setOnClickListener(v -> {
            if (arquivoUri != null) {
                enviarArquivo();
            } else {
                Toast.makeText(this, "Selecione um arquivo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selecionarArquivo() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            arquivoUri = data.getData();
            txtArquivo.setText(arquivoUri.getLastPathSegment());
        }
    }

    private void enviarArquivo() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Enviando...");
        dialog.show();

        String userId = auth.getCurrentUser().getUid();
        String dataHora = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        StorageReference ref = storageReference.child("Atestados/" + userId + "/arquivo_" + dataHora);

        ref.putFile(arquivoUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("url", uri.toString());
                    map.put("dataHora", dataHora);

                    FirebaseDatabase.getInstance().getReference("Atestados")
                            .child(userId)
                            .push()
                            .setValue(map)
                            .addOnCompleteListener(task -> {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Arquivo enviado com sucesso!", Toast.LENGTH_SHORT).show();
                                    txtArquivo.setText("");
                                } else {
                                    Toast.makeText(this, "Erro ao enviar.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }))
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
