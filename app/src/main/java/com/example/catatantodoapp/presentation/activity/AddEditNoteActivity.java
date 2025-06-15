package com.example.catatantodoapp.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.catatantodoapp.R;
import com.example.catatantodoapp.data.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Activity untuk menambah atau mengedit catatan, termasuk mengunggah gambar ke Firebase Storage.
 */
public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE = "extra_note";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    private EditText etTitle, etContent;
    private ImageView ivPreview;
    private Button btnTakePhoto;
    private Note note;
    private Uri photoUri;
    private File photoFile;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        // Inisialisasi UI
        etTitle = findViewById(R.id.et_note_title);
        etContent = findViewById(R.id.et_note_content);
        ivPreview = findViewById(R.id.image_preview);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        Button btnSave = findViewById(R.id.btn_save_note);

        // Inisialisasi Firebase Storage
        storageRef = FirebaseStorage.getInstance().getReference();

        // Cek apakah mengedit catatan yang ada
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
            if (note.getImageUrl() != null && !note.getImageUrl().isEmpty()) {
                ivPreview.setVisibility(View.VISIBLE);
                // TODO: Muat gambar menggunakan Glide jika diperlukan
            }
            setTitle("Edit Note");
        } else {
            note = new Note();
            note.setId(UUID.randomUUID().toString());
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : "unknown_user";
            note.setUserId(userId);
            setTitle("Add Note");
        }

        // Setup launcher untuk kamera
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (photoFile != null && photoFile.exists()) {
                    photoUri = Uri.fromFile(photoFile);
                    ivPreview.setImageURI(photoUri);
                    ivPreview.setVisibility(View.VISIBLE);
                    Log.d("AddEditNoteActivity", "Photo URI: " + photoUri);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    Log.e("AddEditNoteActivity", "Photo file does not exist");
                }
            }
        });

        // Listener untuk tombol ambil foto
        btnTakePhoto.setOnClickListener(v -> {
            if (checkPermissions()) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                    photoUri = FileProvider.getUriForFile(this, "com.example.catatantodoapp.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(takePictureIntent);
                } catch (IOException e) {
                    Log.e("AddEditNoteActivity", "Error creating image file", e);
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener untuk tombol simpan
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            note.setTitle(title);
            note.setContent(content);
            note.setTimestamp(System.currentTimeMillis());

            if (photoUri != null) {
                uploadImageToStorage(note);
            } else {
                returnResult();
            }
        });
    }

    /**
     * Membuat file sementara untuk menyimpan gambar dari kamera.
     */
    private File createImageFile() throws IOException {
        String fileName = "Note_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    /**
     * Memeriksa izin kamera dan penyimpanan.
     */
    private boolean checkPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    /**
     * Menangani hasil permintaan izin.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                    photoUri = FileProvider.getUriForFile(this, "com.example.catatantodoapp.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(takePictureIntent);
                } catch (IOException e) {
                    Log.e("AddEditNoteActivity", "Error creating image file", e);
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Camera and storage permissions are required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Mengunggah gambar ke Firebase Storage dan menyimpan URL ke objek Note.
     */
    private void uploadImageToStorage(Note note) {
        if (photoUri == null) {
            Toast.makeText(this, "No photo to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("AddEditNoteActivity", "Uploading image with URI: " + photoUri);
        String userId = note.getUserId() != null ? note.getUserId() : "unknown_user";
        StorageReference imageRef = storageRef.child("users/" + userId + "/notes/" + note.getId() + ".jpg");

        imageRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            note.setImageUrl(uri.toString());
                            returnResult();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AddEditNoteActivity", "Failed to get image URL: " + e.getMessage());
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddEditNoteActivity", "Failed to upload image: " + e.getMessage());
                });
    }

    /**
     * Mengembalikan hasil ke aktivitas sebelumnya dan menutup aktivitas ini.
     */
    private void returnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}