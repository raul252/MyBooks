package com.cifo.rgonzalezgall.mybooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cifo.rgonzalezgall.mybooks.model.BookContent;
import com.cifo.rgonzalezgall.mybooks.model.BookContent.BookItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private BookItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    /**
     * Return the book that matches with the position
     * @param identificador
     * @return
     */
    public BookItem getBook(Long identificador){
        List<BookItem> books = BookContent.getBooks();
        for (BookItem book : books) {
            if (book.getId().equals(identificador)) {
                return book;
            }
        }
        return null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //Long identificador = getArguments().getLong(ARG_ITEM_ID);
            //mItem = getBook(identificador);
            int posicion = getArguments().getInt(ARG_ITEM_ID);
            mItem = BookContent.getBooks().get(posicion);

            if (mItem != null) {
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.getTitle());
                }
            } else {
                Context context = this.getContext();
                Toast.makeText(context, "No se encuentra el registro", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, BookListActivity.class);
                intent.putExtra(BookDetailFragment.ARG_ITEM_ID, 1);

                context.startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.textViewAutor)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.textViewData)).setText(mItem.getPublication_date());
            ((TextView) rootView.findViewById(R.id.textViewDescripcion)).setText(mItem.getDescription());
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            String imageUri = mItem.getUrl_image();
            Picasso.with(getActivity()).load(imageUri).fit().into(imageView);
        }

        return rootView;
    }
}
