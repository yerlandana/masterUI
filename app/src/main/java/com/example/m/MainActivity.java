package com.example.m;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.m.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ExampleDialog.SingleChoiceListener {

    private ActivityMainBinding mBinding;
    ProgressDialog pd;
    private FirebaseFirestore db;
    private static final String TAG = "MainActvity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

    }

    private void initViews() {

        db = FirebaseFirestore.getInstance();
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.showBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });

        mBinding.saveBtn.setOnClickListener(v -> {
            String title = mBinding.titleEt.getText().toString().trim();
            String description = mBinding.descriptionEt.getText().toString().trim();
            pd = new ProgressDialog(this);


            uploadData(title, description);
        });

        mBinding.themeChange.setOnClickListener(v -> {
            openDialog();
        });
    }

    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.setCancelable(false);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void uploadData(String title, String description) {
        pd.setTitle("Adding ...");
        pd.show();

        String id = UUID.randomUUID().toString();
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("title", title);
        doc.put("description", description);

        db.collection("Documents").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.setTitle("Failed");
                        pd.show();
                    }
                });
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        mBinding.themeText.setText(list[position]);
        if (list[position].equals("Dark")) {
            Log.d("Main", list[position]);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (list[position].equals("Light")) {
            Log.d("Main", list[position]);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            Log.d("Main", list[position]);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        mBinding.themeText.setText("Dialog canceled");
    }
}