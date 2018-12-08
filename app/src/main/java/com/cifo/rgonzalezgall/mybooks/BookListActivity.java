package com.cifo.rgonzalezgall.mybooks;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cifo.rgonzalezgall.mybooks.model.BookContent;
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
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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
    private SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView recyclerView;

    private static final String TAG = "BookListActivity";

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

        if (getIntent() != null && getIntent().getAction() != null) {
            int position = -1;
            if (getIntent().getAction().equalsIgnoreCase(MyFirebaseMessagingService.ACTION_DELETE)) {
                // Action delete el libro
                position = Integer.valueOf(getIntent().getStringExtra(MyFirebaseMessagingService.BOOK_POSITION));
                Log.d(TAG, "Eliminar el libro "+position);
                if(position >= BookContent.getBooks().size()){
                    Toast.makeText(BookListActivity.this, "No se encuentra el libro a eliminar con posición " + position, Toast.LENGTH_LONG).show();
                }else{
                    try {
                        BookContent.BookItem book = BookContent.getBooks().get(position);
                        if (book == null) {
                            Toast.makeText(BookListActivity.this, "No se encuentra el libro a eliminar con posición " + position, Toast.LENGTH_LONG).show();
                        } else {
                            //Delete a book
                            book.delete();
                        }
                    } catch (Exception e) {
                        Toast.makeText(BookListActivity.this, "No se encuentra el libro a eliminar con posición " + position, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (getIntent().getAction().equalsIgnoreCase(MyFirebaseMessagingService.ACTION_VIEW)) {
               // Ver el detalle del libro
                position = Integer.valueOf(getIntent().getStringExtra(MyFirebaseMessagingService.BOOK_POSITION));
                Log.d(TAG, "Ver el libro "+position);

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(BookDetailFragment.ARG_ITEM_ID, position);
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    //Resolution < 900dp
                    Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, position);
                    startActivity(intent);
                }
            }
         }

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
        recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                SimpleItemRecyclerViewAdapter simpleRefrestRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(BookListActivity.this, BookContent.getBooks(), mTwoPane);
                simpleRefrestRecyclerViewAdapter.setItems(BookContent.getBooks());
                recyclerView.setAdapter(simpleRefrestRecyclerViewAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //Items del menu lateral
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.share);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.copy);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.whatsApp);

        //Part del menu lateral amb el header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Raul gonzalez").withEmail("raul@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_user))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something based on item clicked
                        int opcion = ((Number)drawerItem.getIdentifier()).intValue();
                        switch (opcion)
                        {
                            case 1:
                                shareBook();
                                break;
                            case 2:
                                copyClipBoard();
                                break;
                            case 3:
                                shareWhatsApp();
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }

    /**
     * Method for sharing context in whatsApp
     */
    private void shareWhatsApp() {
        Intent shareIntent = share();
        shareIntent.setPackage("com.whatsApp");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.whatsApp)));
    }

    /**
     * Method for copying in the clickboard
     */
    private void copyClipBoard() {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("MyBooks", getString(R.string.text));
        clipBoard.setPrimaryClip(clipData);
        Toast.makeText(this,  getString(R.string.clipBoard), Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @return Intent
     */
    private Intent share(){
        Uri imatgeAEnviar = prepareImage();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imatgeAEnviar);
        shareIntent.setType("image/jpeg");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return shareIntent;
    }

    /**
     * Method for sharing text and image of a book
     */
    private void shareBook() {
        Uri imatgeAEnviar = prepareImage();
        Intent shareIntent = share();
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    /**
     * Prepare and save image
     * @return Uri
     */
    private Uri prepareImage() {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        File imagePath = new File(getFilesDir(), "temporal");
        imagePath.mkdir();
        File imageFile = new File(imagePath.getPath(), "app_icon.png");

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return FileProvider.getUriForFile(getApplicationContext(), getPackageName(), imageFile);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, BookContent.getBooks(), mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final BookListActivity mParentActivity;
        private List<BookContent.BookItem> mValues;
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
            mValues.clear();
            mValues = new ArrayList<>();
            mValues.addAll(items);
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
                //BookContent.BookItem item = (BookContent.BookItem) view.getTag();
                int position = (int) view.getTag();
                /*I use the identificator but if I insert something in the first position of the database it fails.
                // But I can use the position
                */
                //Long identificador = item.getId();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(BookDetailFragment.ARG_ITEM_ID, position);
                    //arguments.putLong(BookDetailFragment.ARG_ITEM_ID, identificador);
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    //Resolution < 900dp
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    //intent.putExtra(BookDetailFragment.ARG_ITEM_ID, identificador);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, position);
                    context.startActivity(intent);
                }
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

            //holder.itemView.setTag(mValues.get(position));
            holder.itemView.setTag(position);
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

    @Override
    protected void onResume() {
        super.onResume();

    // Clear all notification
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
