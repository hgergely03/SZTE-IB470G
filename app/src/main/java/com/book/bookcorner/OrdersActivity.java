package com.book.bookcorner;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class OrdersActivity extends AppCompatActivity {
    private FirebaseUser user;
    private String userEmail;
    private CollectionReference ordersCollection;
    private CollectionReference booksCollection;
    private BookAdapter bookAdapter;
    CardView currentOrderCard;
    private RecyclerView recyclerView;
    private ArrayList<Order> orders;
    private ArrayList<Book> books;
    private NotificationHandler notificationHandler;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ORDERS_ACTIVITY", "onPause");
        notificationHandler.sendNotification("Ne felejts el visszatérni és vásárolni!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        books = new ArrayList<>();
        orders = new ArrayList<>();
        ordersCollection = FirebaseFirestore.getInstance().collection("orders");
        booksCollection = FirebaseFirestore.getInstance().collection("books");

        if (user != null) {
            Log.d("ORDERS_ACTIVITY", "User is logged in");
            userEmail = user.getEmail();
            bookAdapter = new BookAdapter(this, books);
            recyclerView = findViewById(R.id.ordersBookList);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            recyclerView.setAdapter(bookAdapter);

            initializeData();
        }

        Intent intent = getIntent();
        String bookTitle = intent.getStringExtra("bookTitle");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookImgUrl = intent.getStringExtra("bookImgUrl");
        int bookPrice = intent.getIntExtra("bookPrice", 3000);
        String bookId = intent.getStringExtra("bookId");

        currentOrderCard = findViewById(R.id.currentOrder);

        if (bookTitle != null && bookAuthor != null && bookImgUrl != null && bookPrice != 0) {
            TextView currentOrderTitle = findViewById(R.id.currentOrderTitle);
            currentOrderTitle.setText(bookTitle);

            TextView currentOrderAuthor = findViewById(R.id.currentOrderAuthor);
            currentOrderAuthor.setText(bookAuthor);

            ImageView currentOrderImage = findViewById(R.id.currentOrderImage);
            Glide.with(this).load(bookImgUrl).into(currentOrderImage);

            Button currentOrderButton = findViewById(R.id.currentOrderButton);
            currentOrderButton.setText("Vásárlás (" + bookPrice + " Ft)");

            currentOrderButton.setOnClickListener(v ->{
                if (user == null) {
                    Toast.makeText(this, "A vásárlás csak bejelentkezés után lehetséges!", Toast.LENGTH_LONG).show();
                    return;
                }

                Order order = new Order(bookId, userEmail, new Date());
                ordersCollection.add(order)
                    .addOnSuccessListener(documentReference -> {
                        Book book = null;

                        for (Book b : books) {
                            if (b.getId().equals(bookId)) {
                                book = b;
                                break;
                            }
                        }

                        if (book != null) {
                            booksCollection.document(bookId).update("boughtNumber", book.getBoughtNumber() + 1)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "A rendelés sikeresen leadva!", Toast.LENGTH_SHORT).show();
                                    currentOrderCard.setVisibility(GONE);

                                    notificationHandler = new NotificationHandler(this);
                                    notificationHandler.sendNotification("Sikeres vásárlás!");

                                    initializeData();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Hiba történt a rendelés létrehozásakor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba történt a rendelés létrehozásakor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            });
        } else {
            currentOrderCard.setVisibility(GONE);

            findViewById(R.id.orderLabel).setVisibility(GONE);
        }
    }

    public void getBooks() {
        for (Order order : orders) {
            // Log.d("ORDERS_ACTIVITY", "Loading book: " + order.getBookId());
            booksCollection.document(order.getBookId()).get().addOnCompleteListener(snapshot -> {
                if (snapshot.isSuccessful()) {
                    var result = snapshot.getResult();
                    Book book = result.toObject(Book.class);

                    if (book != null) {
                        book.setId(result.getId());
                        Log.d("ORDERS_ACTIVITY", book.getTitle() + " loaded successfully");
                    }

                    books.add(book);
                    bookAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Hiba történt a könyvek betöltésekor", Toast.LENGTH_LONG).show();
                    snapshot.getException().printStackTrace();
                }
            });
        }
    }

    private void initializeData() {
        Log.d("ORDERS_ACTIVITY", "Initialize data");
        orders.clear();
        books.clear();

        // query with index
        ordersCollection.orderBy("orderDate", Query.Direction.DESCENDING)
                .whereEqualTo("buyerEmail", userEmail)
                .limit(10)
                .get()
                .addOnCompleteListener(snapshot -> {
                    if (snapshot.isSuccessful()) {
                        var result = snapshot.getResult();
                        for (QueryDocumentSnapshot document : result) {
                            Order order = document.toObject(Order.class);
                            order.setOrderId(document.getId());
                            orders.add(order);
                        }

                        getBooks();
                    } else {
                        Log.d("ORDERS_ACTIVITY", "Orders loaded unsuccessfully");
                        Toast.makeText(this, "Hiba rendelések a könyvek betöltésekor", Toast.LENGTH_LONG).show();
                        snapshot.getException().printStackTrace();
                    }
                });
    }
}