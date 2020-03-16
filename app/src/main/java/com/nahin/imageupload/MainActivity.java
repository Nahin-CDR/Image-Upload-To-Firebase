package com.nahin.imageupload;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ContentResolver;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button choose,upload;
    ImageView imageView;
    String imageUrl;
    Uri uri;

    ProgressBar progressBar;

    private EditText imageName;
    private StorageReference mstorageRef;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        choose = findViewById( R.id.choose_button_id );
        upload =(Button) findViewById( R.id.upload_Button_id );
        imageView = findViewById( R.id.image_id );

        imageName = (EditText)findViewById( R.id.inputImageNameID );


        mstorageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        upload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        } );

        choose.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent( Intent.ACTION_PICK );

                photoPicker.setType( "image/*" );

                    try {
                        startActivityForResult( photoPicker,1 );
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText( MainActivity.this, "Couldn't found required app to show image", Toast.LENGTH_SHORT ).show();
                    }
            }
        } );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == RESULT_OK){
            uri = data.getData();
            imageView.setImageURI( uri );
        }else {
            //Toast.makeText( this, "You Haven't Picked Images", Toast.LENGTH_SHORT ).show();
        }
    }
//
//    private String getFileExtension(Uri uri){
//        ContentResolver cr = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType( cr.getType( uri ) );
//    }

    private void uploadFile() {
        String foldername = "Image";
        if(uri !=null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child( foldername ).child( uri.getLastPathSegment() );
            final ProgressDialog progressDialog = new ProgressDialog( this );
            progressDialog.setMessage( "Image Uploading....." );
            progressDialog.show();

            storageReference.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    imageUrl = uriTask.getResult().toString();
                    uploadeRealtime();
                    progressDialog.dismiss();

                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT ).show();
                    progressDialog.dismiss();
                }
            } );


        }else {
            Toast.makeText( this, "Select Image First....", Toast.LENGTH_SHORT ).show();
        }
//        {
//            StorageReference fileReference = mstorageRef.child(System.currentTimeMillis()
//            +"."+ getFileExtension( uri )
//            );
//
//            fileReference.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Handler handler = new Handler( );
//                    handler.postDelayed( new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setProgress( 0 );
//                        }
//                    } ,5000);
//                    progressBar.setProgress( 0 );
//                    Toast.makeText( MainActivity.this, "Upload Successful !", Toast.LENGTH_SHORT ).show();
//
//
//
//                  //  Upload upload = new Upload(  )
//
//                }
//            } ).addOnFailureListener( new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                    Toast.makeText( MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                }
//            } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//
//                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                    progressBar.setProgress( (int) progress );
//
//
//                }
//            } );
//        }
    }

    private void uploadeRealtime() {
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Image Uploading....." );
        progressDialog.show();
        Upload upload = new Upload( imageUrl );

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy,hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());

       // String mydate = DateFormat.getDateInstance().format( Calendar.getInstance().getTime() );
        FirebaseDatabase.getInstance().getReference("Nahin")
                .child( datetime ).setValue( upload ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    imageView.setImageResource( R.drawable.image_defaut );
                    progressDialog.dismiss();
                    Toast.makeText( MainActivity.this, "Upload successful !", Toast.LENGTH_SHORT ).show();
                }
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }
}
