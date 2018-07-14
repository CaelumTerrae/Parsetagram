package me.caelumterrae.parsetagram.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import me.caelumterrae.parsetagram.R;
import me.caelumterrae.parsetagram.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    public ImageView ivPicture;
    public Button btnPost;
    public EditText etDescription;
    public final String APP_TAG = "MyCustomApp";
    public Boolean photoTaken;
    public String photoFileName = "photo.jpg";

    private OnFragmentInteractionListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoTaken = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivPicture = view.findViewById(R.id.ivPicture);
        btnPost = view.findViewById(R.id.btnPost);
        etDescription = view.findViewById(R.id.etDescription);
        btnPost.setOnClickListener(new PostListener());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class PostListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (photoTaken) {
                File photoFile = getPhotoFileUri(photoFileName);
                ParseFile parseFile = new ParseFile(photoFile);
                Post post = new Post();


                // Setting the fields of the postclass
                post.setMedia(parseFile);
                String description = etDescription.getText().toString();
                post.setDescription(description);
                ParseUser user = ParseUser.getCurrentUser();
                post.setUser(user);
                post.setUsername(user.get("username").toString());


                //
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null){
                            // If the image is successfully posted lets render a success message for now.
                            Toast.makeText(getContext(), "successful image post", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(), "failed image post", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}
