package me.caelumterrae.parsetagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import me.caelumterrae.parsetagram.models.Post;

public class CameraActivity extends AppCompatActivity {

    ImageView ivPicture;
    Button btnCapture;
    Button btnPost;
    EditText etDescription;
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    Boolean photoTaken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        photoTaken = false;
        ivPicture = findViewById(R.id.ivPicture);
        btnCapture = findViewById(R.id.btnCapture);
        btnPost = findViewById(R.id.btnPost);
        etDescription = findViewById(R.id.etDescription);
        btnCapture.setOnClickListener(new CaptureListener());
        btnPost.setOnClickListener(new PostListener());
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(CameraActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);



        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPicture.setImageBitmap(takenImage);
                photoTaken = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CaptureListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            onLaunchCamera(view);
        }
    }

    private class PostListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (photoTaken) {
                File photoFile = getPhotoFileUri(photoFileName);
                ParseFile parseFile = new ParseFile(photoFile);
                Post post = new Post();

                post.setMedia(parseFile);

                String description = etDescription.getText().toString();
                post.setDescription(description);
                ParseUser user = ParseUser.getCurrentUser();
                post.setUser(user);
                post.setUsername(user.get("username").toString());

                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            // If the image is successfully posted lets render a success message for now.
                            Toast.makeText(CameraActivity.this, "successful image post", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(CameraActivity.this, "failed image post", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
    }
}
