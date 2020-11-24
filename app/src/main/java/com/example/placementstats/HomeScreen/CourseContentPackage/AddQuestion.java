package com.example.placementstats.HomeScreen.CourseContentPackage;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.placementstats.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddQuestion extends AppCompatActivity {

    private EditText edt1,edt2,edt3;
    private ImageView img1;
    private Button btn1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imgUri;
    private String courseName,courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        init();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

    }

    private void addData() {
        final String edt1_text = edt1.getText().toString();
        final String edt2_text = edt2.getText().toString();
        final String edt3_text = edt3.getText().toString();
        final String questionId = System.currentTimeMillis()+"";

        if(imgUri==null){

            ProgrammingQuestionModel model = new ProgrammingQuestionModel(edt1_text,edt2_text);
            databaseReference.child(courseName).child(courseId).child(questionId).setValue(model);
        }else{

            if(edt3_text.equals("")){

                final StorageReference reference = storageReference.child(getString(R.string.ProgrammingQuestionImage)).child(questionId+""+getFileExtension(imgUri));
                reference.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ProgrammingQuestionModel model = new ProgrammingQuestionModel(edt1_text,edt2_text,uri+"");
                                                databaseReference.child(courseName).child(courseId).child(questionId).setValue(model);
                                            }
                                        });
                            }
                        });
            }else{

                final StorageReference reference = storageReference.child(getString(R.string.ProgrammingQuestionImage)).child(questionId+""+getFileExtension(imgUri));
                reference.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ProgrammingQuestionModel model = new ProgrammingQuestionModel(edt1_text,edt2_text,uri+"",edt3_text);
                                                databaseReference.child(courseName).child(courseId).child(questionId).setValue(model);
                                            }
                                        });
                            }
                        });
            }

        }


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();
            Picasso.get().load(imgUri).into(img1);
        }

    }


    private void init() {

        Intent intent = getIntent();
        courseName = intent.getStringExtra(getString(R.string.CourseName));
        courseId = intent.getStringExtra(getString(R.string.CourseId));

        edt1 = findViewById(R.id.add_question_question);
        edt2 = findViewById(R.id.add_question_tags);
        img1 = findViewById(R.id.add_question_image);
        edt3 = findViewById(R.id.add_question_solution);
        btn1 = findViewById(R.id.add_question_add);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }
}