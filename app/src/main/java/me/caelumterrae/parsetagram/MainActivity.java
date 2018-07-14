package me.caelumterrae.parsetagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import me.caelumterrae.parsetagram.fragments.CameraFragment;
import me.caelumterrae.parsetagram.fragments.ProfileFragment;
import me.caelumterrae.parsetagram.fragments.TimelineFragment;

public class MainActivity extends AppCompatActivity implements TimelineFragment.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, ProfileFragment.ProfileInteractionListener{


    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo.jpg";
    File photoFile;

    Fragment timelineFragment;
    Fragment cameraFragment;
    Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        timelineFragment = new TimelineFragment();
        cameraFragment = new CameraFragment();
        profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, timelineFragment).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_timeline:
                        FragmentTransaction fragmentTransactionTimeline = fragmentManager.beginTransaction();
                        fragmentTransactionTimeline.replace(R.id.placeholder, timelineFragment).commit();
                        Toast.makeText(MainActivity.this, "on timeline", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.action_create:
                        FragmentTransaction fragmentTransactionCreate = fragmentManager.beginTransaction();
                        fragmentTransactionCreate.replace(R.id.placeholder, cameraFragment).commit();
                        Toast.makeText(MainActivity.this, "on create post", Toast.LENGTH_LONG).show();
                        onLaunchCamera();
                        // do something here
                        return true;
                    case R.id.action_profile:
                        FragmentTransaction fragmentTransactionProfile = fragmentManager.beginTransaction();
                        fragmentTransactionProfile.replace(R.id.placeholder, profileFragment).commit();
                        Toast.makeText(MainActivity.this, "on profile", Toast.LENGTH_LONG).show();
                        // do something here
                        return true;
                    default:
                        return true;

                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
//        //Get the bottom Menu
//        Toolbar toolbar = findViewById(R.id.bottom_navigation);
//        Menu bottomMenu = toolbar.getMenu();
//        getMenuInflater().inflate(R.menu.menu_bottom_navigation, bottomMenu);
        return true;
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
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
                ((CameraFragment)cameraFragment).ivPicture.setImageBitmap(takenImage);
                ((CameraFragment)cameraFragment).photoTaken = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_PHOTO_CODE) {
            if (data != null) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the selected image into a preview
                File file = new File(photoUri.getPath());
                final ParseFile parseFile = new ParseFile(file);

                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("profilepic", parseFile);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null) {
                                    Toast.makeText(MainActivity.this, "saved profile picture correctly", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "failure to save profile picture", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });


                ((ProfileFragment)profileFragment).ivProfile.setImageBitmap(selectedImage);
            }
        }
    }

    public void getSettings(MenuItem mi){
        Intent i = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(i);
    }


    @Override
    public void onProfileClick() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }
}
