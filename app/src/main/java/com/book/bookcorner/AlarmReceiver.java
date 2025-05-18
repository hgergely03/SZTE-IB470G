package com.book.bookcorner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AlarmReceiver extends BroadcastReceiver {
    private CollectionReference booksCollection;

    @Override
    public void onReceive(Context context, Intent intent) {
        booksCollection = FirebaseFirestore.getInstance().collection("books");

        // query with index
        booksCollection.whereGreaterThan("boughtNumber", 0).orderBy("boughtNumber", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String bookTitle = document.getString("title");
                    int boughtNumber = document.getLong("boughtNumber").intValue();

                    new NotificationHandler(context).sendNotification(boughtNumber + " people have already bought this book: " + bookTitle + " Buy now!");
                }
            }
        });
    }
}
