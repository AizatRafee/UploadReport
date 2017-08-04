package com.example.android.uploadreport;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button buttonSend;
    private EditText textReport;
    private EditText title;
    private Spinner category;
    private TextView locationDes;
    private String key;
    private String mUsername;
    private Uri downloadURL;

    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private Uri imageuri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    public static final String ANONYMOUS = "anonymous";
    ProgressDialog progressDialog;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private StorageReference myrefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        //Initialize Firebase component
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("report");

        // Initialize references to views
        imageView = (ImageView) findViewById(R.id.imageView);
        buttonSend = (Button) findViewById(R.id.buttonUpload);
        myrefernce = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(MainActivity.this);

        if (savedInstanceState!=null){

            imageuri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_PERMISSION);

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                progressDialog.setTitle("Uploading..");
                progressDialog.show();
                //upload to firebase storage
                StorageReference filepath = myrefernce.child("photos").child(imageuri.getLastPathSegment());
                filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this,"uploaded",Toast.LENGTH_LONG).show();
                        downloadURL = taskSnapshot.getDownloadUrl();

                        textReport = (EditText) findViewById(R.id.textreport);
                        title = (EditText) findViewById(R.id.titleReport);
                        category = (Spinner) findViewById(R.id.category);
                        locationDes = (TextView) findViewById(R.id.locationDes);

                        //upload to firebase database
                        Report report = new Report(mUsername, downloadURL.toString(),"category.getSelectedItem().toString()",title.getText().toString(),textReport.getText().toString(),locationDes.getText().toString(),key);
                        mMessagesDatabaseReference.push().setValue(report);
                        progressDialog.dismiss();

                        //clear input
                        textReport.setText("");
                        title.setText("");
                        locationDes.setText("location");
                        imageView.setImageBitmap(null);
                    }
                });
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!"+requestCode, Toast.LENGTH_SHORT).show();
                }


        }
    }

    public void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        key = DateFormat.getDateTimeInstance().format(new Date());
        File photo = new File(Environment.getExternalStorageDirectory(), "picture"+key+".jpg");
        imageuri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Scanner scanner = new Scanner();
                final Bitmap bitmap = scanner.decodeBitmapUri(MainActivity.this, imageuri);
                progressDialog.setTitle("Uploading..");
                progressDialog.show();

                imageView.setImageBitmap(bitmap);
                progressDialog.dismiss();

            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageuri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageuri.toString());
            //outState.putString(SAVED_INSTANCE_RESULT, scan.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageuri);
        this.sendBroadcast(mediaScanIntent);
    }

}