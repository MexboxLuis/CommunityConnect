package com.example.conexioncomunitaria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends Activity {

    private EditText inputEmail;
    private Button btnResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        inputEmail = findViewById(R.id.email);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(getApplication(), "Ingresa tu correo electrónico registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplication(), "Sigue los pasos en el correo electrónico para restablecer tu contraseña", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplication(), "Error al enviar el correo de restablecimiento", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
