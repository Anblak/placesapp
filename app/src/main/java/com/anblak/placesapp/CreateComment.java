package com.anblak.placesapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.anblak.placesapp.data.LoginRepository;
import com.anblak.placesapp.utils.Constants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.util.Optional.ofNullable;

public class CreateComment extends DialogFragment{
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String TAG = "Detail_Activity";
    private static final String SUCCESS_RESULT = "Respect";

    private static String message;
    private NoticeDialogListener noticeDialogListener;

    public CreateComment() {
        // Required empty public constructor
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        CreateComment.message = message;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText editText = ((AppCompatActivity)noticeDialogListener).findViewById(R.id.edit_comment);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                setMessage(s.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EditText editText = getActivity().findViewById(R.id.edit_comment);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                setMessage(s.toString());
            }
        });
        return inflater.inflate(R.layout.fragment_create_comment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        noticeDialogListener = (NoticeDialogListener) context;
        super.onAttach(context);
    }

    public interface NoticeDialogListener {
        void onPositive(DialogFragment dialogFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) noticeDialogListener);
        builder.setView(getLayoutInflater().inflate(R.layout.fragment_create_comment,null));
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(LoginRepository.getUser() != null) {
                    String message = ofNullable(CreateComment.getMessage()).orElse("null");

                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                            "{\n" +
                                    "\"message\":\"asdfgasdfasd\",\n" +
                                    "\"userUUID\":0,\n" +
                                    "\"placeId\":3\n" +
                                    "}"
                    );

                    Request request = new Request.Builder()
                            .url(Constants.SERVER_URL + "/comments")
                            .post(formBody)
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if(!response.body().string().equals(SUCCESS_RESULT)) {
                            Toast.makeText((Context) noticeDialogListener, "Error",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText((Context) noticeDialogListener, "Comment: " + message,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.v(TAG, e.getMessage());
                        Toast.makeText((Context) noticeDialogListener, "Error",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText((Context) noticeDialogListener, "Authorize please",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();

        EditText editText = getActivity().findViewById(R.id.edit_comment);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                setMessage(s.toString());
            }
        });
        return super.onCreateDialog(savedInstanceState);
    }
}
