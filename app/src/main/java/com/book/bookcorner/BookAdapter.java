package com.book.bookcorner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder>  implements Filterable {
    private ArrayList<Book> booksData = new ArrayList<>();
    private ArrayList<Book> booksDataAll = new ArrayList<>();
    private Context context;
    private int lastPosition = -1;
    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Book> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = booksDataAll.size();
                results.values = booksDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Book book : booksDataAll) {
                    if (book.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(book);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            booksData = (ArrayList<Book>) filterResults.values;

            notifyDataSetChanged();
        }
    };

    public BookAdapter(Context context, ArrayList<Book> itemsData) {
        booksData = itemsData;
        booksDataAll = itemsData;
        this.context = context;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(context).inflate(R.layout.book_card, parent, false));
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        Book currentBook = booksData.get(position);

        holder.bindTo(currentBook);
    }

    @Override
    public int getItemCount() {
        return booksData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    class BookHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private ImageView cover;
        private Button bookDetailsButton;

        public BookHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            cover = itemView.findViewById(R.id.bookImage);
            bookDetailsButton = itemView.findViewById(R.id.bookDetailsButton);
        }

        public void bindTo(Book currentBook) {
            title.setText(currentBook.getTitle());
            author.setText(currentBook.getAuthor());

            bookDetailsButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailsActivity.class);

                intent.putExtra("bookId", currentBook.getId());
                intent.putExtra("bookTitle", currentBook.getTitle());
                intent.putExtra("bookAuthor", currentBook.getAuthor());
                intent.putExtra("bookPrice", currentBook.getPrice());
                intent.putExtra("bookImgUrl", currentBook.getImgUrl());
                intent.putExtra("bookDescription", currentBook.getDescription());

                context.startActivity(intent);
            });

            Glide.with(context).load(currentBook.getImgUrl()).into(cover);
        }
    }
}

