package com.book.bookcorner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
    }

    public void register(View v) {
        String email = ((EditText)findViewById(R.id.inputEmailAddress)).getText().toString();
        String password = ((EditText)findViewById(R.id.inputPassword)).getText().toString();
        String passwordAgain = ((EditText)findViewById(R.id.inputPasswordAgain)).getText().toString();

        if (email.isBlank()) {
            Toast.makeText(this, "Kérjük adja meg az email címét!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isBlank()) {
            Toast.makeText(this, "Kérjük adja meg a jelszavát!", Toast.LENGTH_LONG).show();
            return;
        }

        if (passwordAgain.isBlank()) {
            Toast.makeText(this, "Kérjük adja meg a jelszavát újra!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_LONG).show();
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            } else {
                Toast.makeText(this, "Regisztráció sikertelen!", Toast.LENGTH_LONG).show();
            }
        });
    }
}