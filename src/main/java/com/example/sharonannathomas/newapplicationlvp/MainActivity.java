package com.example.sharonannathomas.newapplicationlvp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  /*  implementing click listener */ {
    //a constant to track the file chooser intent
    private static final int PICKFILE_RESULT_CODE = 1;
    View view;
    String FilePath;
    //Buttons
    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editTextFileName;
    private TextView textViewUploads;
    private TextView textFile;
    private Uri filePath;
    private StorageReference StorageRef;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        textViewUploads = (TextView)findViewById(R.id.textViewUploads);
        textFile = (TextView)findViewById(R.id.textfile);
        editTextFileName = (EditText) findViewById(R.id.editTextFileName);
        textViewUploads.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        StorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            //case android.R.id.home:
              //  NavUtils.navigateUpFromSameTask(this);
                //return true;

            case R.id.action_sign:
                mAuth.signOut();
                Toast.makeText(MainActivity.this,R.string.signed_out,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            //return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {
            uploadFile();
        }
        else if (view == textViewUploads){
            startActivity(new Intent(this, ViewUploadsActivity.class));
        }

    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"image/jpeg", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICKFILE_RESULT_CODE);
    }

    private void uploadFile() {
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference sRef = StorageRef.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis());

            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successful
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(editTextFileName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                            mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(),exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploading...");
                        }
                    });
        }
        //if there is not any file
        else {
            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
           }
    }


    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            {
                FilePath = data.getData().getPath();
                filePath = data.getData();
                textFile.setText(FilePath);
            }
        }


    }}