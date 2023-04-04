package com.shenron.ryukenwallpapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullImageActivity extends AppCompatActivity {

    private ImageView fullImage;
    private Button apply;
    private Button download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = findViewById(R.id.fullImage);
        apply = findViewById(R.id.apply);
        Button download = findViewById(R.id.download);

        PhotoView fullImage = findViewById(R.id.fullImage);
        String imageUrl = getIntent().getStringExtra("image");
        Glide.with(this).load(imageUrl).into(fullImage);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackground();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUrl = getIntent().getStringExtra("image");
                downloadImage(imageUrl);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                BitmapDrawable drawable = (BitmapDrawable)fullImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Uri uri = getImageUri(getApplicationContext(), bitmap);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        // Create a temporary file for the image
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, "image.png");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get the file provider authority from the manifest
        String authority = context.getPackageName() + ".fileprovider";
        // Create a Uri for the file using the file provider
        return FileProvider.getUriForFile(context, authority, file);
    }

    private void downloadImage(String imageUrl) {
        // Create a storage reference from the Firebase Storage instance
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // Get the filename from the URL
        String filename = storageRef.getName();

        // Get the public storage directory
        File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create a local file to save the downloaded image
        File localFile = new File(publicDir, filename);

        // Show a toast message to indicate that the download has started
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();

        // Download the image from Firebase Storage to the local file
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Show a toast to indicate that the image was downloaded successfully
                Toast.makeText(FullImageActivity.this, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Show a toast to indicate that the download failed
                Toast.makeText(FullImageActivity.this, "Failed to download image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void setBackground() {
        Bitmap bitmap = ((BitmapDrawable) fullImage.getDrawable()).getBitmap();

        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());

        final int[] whichButtonClicked = {-1}; // Default value to indicate no button was clicked

        // Show a dialog to let the user choose between setting the wallpaper as home, lock, or both
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set wallpaper as...");
        String[] options = {"Home", "Lock", "Both"};
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                whichButtonClicked[0] = which;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Apply the wallpaper based on the user's choice
                int setAs;
                switch (whichButtonClicked[0]) {
                    case 0:
                        setAs = WallpaperManager.FLAG_SYSTEM;
                        break;
                    case 1:
                        setAs = WallpaperManager.FLAG_LOCK;
                        break;
                    case 2:
                        setAs = WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK;
                        break;
                    default:
                        return;
                }
                try {
                    manager.setBitmap(bitmap, null, true, setAs);
                    Toast.makeText(FullImageActivity.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(FullImageActivity.this, "Error setting wallpaper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
