package com.book.bookcorner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailsActivity extends AppCompatActivity {
    Button buyBookButton;
    Button doNotShowAgainButton;
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookDescription;
    ImageView bookCover;
    CollectionReference booksCollection;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();

        buyBookButton = findViewById(R.id.buyBookButton);
        buyBookButton.setText("Könyv vásárlása " + intent.getIntExtra("bookPrice", 3000) + " Ft-ért");
        buyBookButton.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(DetailsActivity.this, "Kérlek jelentkezz be a vásárláshoz!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent orderIntent = new Intent(DetailsActivity.this, OrdersActivity.class);
            orderIntent.putExtra("bookTitle", intent.getStringExtra("bookTitle"));
            orderIntent.putExtra("bookAuthor", intent.getStringExtra("bookAuthor"));
            orderIntent.putExtra("bookImgUrl", intent.getStringExtra("bookImgUrl"));
            orderIntent.putExtra("bookPrice", intent.getIntExtra("bookPrice", 3000));
            orderIntent.putExtra("bookId", intent.getStringExtra("bookId"));

            startActivity(orderIntent);
        });

        doNotShowAgainButton = findViewById(R.id.doNotShowButton);

        doNotShowAgainButton.setOnClickListener(v -> {
            booksCollection = FirebaseFirestore.getInstance().collection("books");
            booksCollection.document(intent.getStringExtra("bookId")).delete();
            Toast.makeText(DetailsActivity.this, "A könyv eltávolítva a listádból!", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(DetailsActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });

        bookTitle = findViewById(R.id.bookDetailsTitle);
        bookTitle.setText(intent.getStringExtra("bookTitle"));

        bookAuthor = findViewById(R.id.bookDetailsAuthor);
        bookAuthor.setText(intent.getStringExtra("bookAuthor"));

        bookDescription = findViewById(R.id.bookDetailsDescription);
        bookDescription.setText(intent.getStringExtra("bookDescription"));

        bookCover = findViewById(R.id.bookDetailsCover);
        Glide.with(this).load(intent.getStringExtra("bookImgUrl")).into(bookCover);
    }
}