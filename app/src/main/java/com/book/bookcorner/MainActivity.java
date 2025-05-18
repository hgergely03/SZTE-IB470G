package com.book.bookcorner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private FirebaseFirestore firestore;
    private CollectionReference booksCollection;
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private BookAdapter bookAdapter;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        books = new ArrayList<>();
        bookAdapter = new BookAdapter(this, books);

        recyclerView = findViewById(R.id.mainBooksList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(bookAdapter);

        firestore = FirebaseFirestore.getInstance();
        booksCollection = firestore.collection("books");
        initializeData();

        setBuyAlarm();
    }

    private void setBuyAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);

        // Set a daily notification
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void initializeData() {
        books.clear();

        // query with index
        booksCollection.orderBy("title").orderBy("price").limit(25).get().addOnCompleteListener(snapshot -> {
            if (snapshot.isSuccessful()) {
                var result = snapshot.getResult();
                for (QueryDocumentSnapshot document : result) {
                    Book book = document.toObject(Book.class);
                    book.setId(document.getId());
                    books.add(book);
                }

                bookAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Hiba történt a könyvek betöltésekor", Toast.LENGTH_LONG).show();
                snapshot.getException().printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuItemOrders) {
            Intent intent = new Intent(this, OrdersActivity.class);
            this.startActivity(intent);

            return true;
        } else if (itemId == R.id.menuItemLogin) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                item.setTitle("Bejelentkezés");
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Sikeres kijelentkezés", Toast.LENGTH_LONG).show();
            } else {
                item.setTitle("Kijelentkezés");
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}