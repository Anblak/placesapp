package com.anblak.placesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.anblak.placesapp.data.LoginRepository;
import com.anblak.placesapp.utils.Constants;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String TAG = "Detail_Activity";
    private static final String SUCCESS_RESULT = "Respect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        addRatingListener();
        addCommentListener();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    private void addCommentListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(getLayoutInflater().inflate(R.layout.fragment_create_comment,null));
                builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(LoginRepository.getUser() != null) {
                            String message = ((EditText)findViewById(R.id.edit_comment)).getEditableText().toString();
                            RequestBody formBody = new FormBody.Builder()
                                    .add("message", message)
                                    .add("uuid", LoginRepository.getUser().getUuid())
                                    .add("placeId", String.valueOf(ItemDetailFragment.getmItem().getId()))
                                    .build();

                            Request request = new Request.Builder()
                                    .url(Constants.SERVER_URL + "/comments")
                                    .post(formBody)
                                    .build();
                            try {
                                Response response = okHttpClient.newCall(request).execute();
                                if(!response.body().string().equals(SUCCESS_RESULT)) {
                                    Toast.makeText(ItemDetailActivity.this, "Error",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ItemDetailActivity.this, "Comment: " + message,
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                Log.v(TAG, e.getMessage());
                                Toast.makeText(ItemDetailActivity.this, "Error",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(ItemDetailActivity.this, "Authorize please",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private void addRatingListener() {
        final RatingBar defaultRatingBar = findViewById(R.id.ratingBar_default);
        defaultRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                if(LoginRepository.getUser() != null) {

                    RequestBody formBody = new FormBody.Builder()
                            .add("rating", String.valueOf(rating))
                            .add("uuid", LoginRepository.getUser().getUuid())
                            .add("placeId", String.valueOf(ItemDetailFragment.getmItem().getId()))
                            .build();

                    Request request = new Request.Builder()
                            .url(Constants.SERVER_URL + "/places/rating")
                            .post(formBody)
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if(!response.body().string().equals(SUCCESS_RESULT)) {
                            Toast.makeText(ItemDetailActivity.this, "Error",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ItemDetailActivity.this, "Rating: " + String.valueOf(rating),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.v(TAG, e.getMessage());
                        Toast.makeText(ItemDetailActivity.this, "Error",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ItemDetailActivity.this, "Authorize please",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
