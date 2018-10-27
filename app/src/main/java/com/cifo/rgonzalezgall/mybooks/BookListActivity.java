package com.cifo.rgonzalezgall.mybooks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cifo.rgonzalezgall.mybooks.helper.BookContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    //private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    //Login Parameters in Firebase
    private String email = "rgonzalezgall@uoc.edu";
    private String password = "azul01*}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("books");

        mAuth.signInWithEmailAndPassword(email, password)  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User signed in successfully
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<ArrayList<BookContent.BookItem>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>(){};
                            ArrayList<BookContent.BookItem> data = dataSnapshot.getValue(genericTypeIndicator);
                            int i = 0;
                            //Iterate to get the keys from the Books
                            BookContent.BookItem book;
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                //Get the title value
                                //String titleFirebase = childSnapshot.child("title").getValue(String.class);
                                //Get the title from the arrayList
                                //String titleArrayList =data.get(i).getTitle();
                                //Check if exists in database
                                Boolean found = BookContent.exists(data.get(i));
                                if (!found) {
                                    //Get the key
                                    String key=childSnapshot.getKey();
                                    //Insert into the database
                                    book = data.get(i);
                                    book.setIdentificador(key);
                                    book.save();
                                }
                                i=i+1;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //Error
                            Toast.makeText(BookListActivity.this, "Error: "+ databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BookListActivity.this, "Botón para enviar un email", Toast.LENGTH_LONG).show();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, BookContent.getBooks(), mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final BookListActivity mParentActivity;
        private final List<BookContent.BookItem> mValues;
        private final boolean mTwoPane;

        /*
        Default constructor
         */
        SimpleItemRecyclerViewAdapter(BookListActivity parent, List<BookContent.BookItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        public void setItems(List<BookContent.BookItem> items) {
            for (int i = 0; i < items.size(); i++) {
                //BookContent.BookItem book = items.get(i);
                //book.save();
            }
        }

        //Private constants
        private static final int TYPE_ONE = 1;
        private static final int TYPE_TWO = 2;

        /*
        Method for obtain if the position is odd or couple
         */
        @Override
        public int getItemViewType(int position) {
            if ((position % 2) == 0) {
                return TYPE_ONE;
            } else {
                return TYPE_TWO;
            }
        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookContent.BookItem item = (BookContent.BookItem) view.getTag();

//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//                    arguments.putInt(BookDetailFragment.ARG_ITEM_ID,
//                            item.getIdentificador());
//                    BookDetailFragment fragment = new BookDetailFragment();
//                    fragment.setArguments(arguments);
//                    mParentActivity.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.book_detail_container, fragment)
//                            .commit();
//                } else {
//                    //Resolution < 900dp
//                    Context context = view.getContext();
//                    Intent intent = new Intent(context, BookDetailActivity.class);
//                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.getIdentificador());
//                    context.startActivity(intent);
//                }
            }
        };

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = null;
            if (viewType == TYPE_ONE) {
                //Layout odd
                view = inflater.inflate(R.layout.book_list_content, parent, false);
            } else {
                //Layout couple
                view = inflater.inflate(R.layout.book_list_content_impar, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTituloView.setText(mValues.get(position).getTitle());
            holder.mAutorView.setText(mValues.get(position).getAuthor());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        // Class ViewHolder
        /*
        Class that contains showed data items
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTituloView;
            final TextView mAutorView;

            ViewHolder(View view) {
                super(view);
                mTituloView = (TextView) view.findViewById(R.id.title);
                mAutorView = (TextView) view.findViewById(R.id.author);
            }
        }
    }
}
