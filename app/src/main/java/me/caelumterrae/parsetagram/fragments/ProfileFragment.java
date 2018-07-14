package me.caelumterrae.parsetagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.caelumterrae.parsetagram.LoginActivity;
import me.caelumterrae.parsetagram.ProfilePostAdapter;
import me.caelumterrae.parsetagram.R;
import me.caelumterrae.parsetagram.models.Post;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    RecyclerView rvPosts;
    ProfilePostAdapter postAdapter;
    ArrayList<Post> posts;
    public ParseImageView ivProfile;
    Button btnLogout;
    ProfileInteractionListener activityListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new LogoutListener());
        ParseUser user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ParseFile profilepic = object.getParseFile("profilepic");
                ivProfile.setParseFile(profilepic);
                ivProfile.loadInBackground();
            }
        });

        ivProfile.setOnClickListener(new ProfileListener());

        posts = new ArrayList<Post>();
        postAdapter = new ProfilePostAdapter(posts);
        populateProfile();
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityListener = (ProfileInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    public interface ProfileInteractionListener {
        void onProfileClick();
    }

    private class ProfileListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            activityListener.onProfileClick();
        }
    }

    private class LogoutListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ParseUser.logOut();
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    public void populateProfile(){
        //TODO: populate the timeline by reading in data from PARSE
        postAdapter.clear();
        //Testing rn by adding fake posts to posts arraylist
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(20);
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts = (ArrayList<Post>) objects;
                    postAdapter.addAll(posts);
                } else {
                    Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
                    Log.d("item", "Error" + e.getMessage());
                }
            }
        });
    }
}
