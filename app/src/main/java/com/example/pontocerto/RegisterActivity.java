package com.example.pontocerto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button btnCadastrar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        auth = FirebaseAuth.getInstance();

        btnCadastrar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                auth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = auth.getCurrentUser().getUid();

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("id", userId);
                                map.put("nome", nome);
                                map.put("email", email);

                                FirebaseDatabase.getInstance().getReference("Usuarios")
                                        .child(userId)
                                        .setValue(map)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
