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
import android.widget.TextView;
import android.widget.Toast;

import com.cifo.rgonzalezgall.mybooks.helper.Helper;
import com.cifo.rgonzalezgall.mybooks.model.BookItem;

import java.text.Format;
import java.text.SimpleDateFormat;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Helper.ITEM_MAP.get(getArguments().getInt(ARG_ITEM_ID));

            if (mItem != null) {
                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.getTitulo());
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
            ((TextView) rootView.findViewById(R.id.textViewAutor)).setText(mItem.getAutor());
            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = formatter.format(mItem.getDataPublicacion());
            ((TextView) rootView.findViewById(R.id.textViewData)).setText(fecha);
            ((TextView) rootView.findViewById(R.id.textViewDescripcion)).setText(mItem.getDescripcion());
        }

        return rootView;
    }
}
