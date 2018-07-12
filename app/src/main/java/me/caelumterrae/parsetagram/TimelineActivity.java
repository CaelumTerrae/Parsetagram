package me.caelumterrae.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.caelumterrae.parsetagram.models.Post;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView rvPosts;
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    SwipeRefreshLayout swipeContainer;
    FragmentActivity context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return;
        }

        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline();
                swipeContainer.setRefreshing(false);
            }
        });

        // get the recycler view
        rvPosts = findViewById(R.id.rvTimeline);

        // instantiate the arraylist
        posts = new ArrayList<Post>();

        postAdapter = new PostAdapter(posts);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        rvPosts.setAdapter(postAdapter);
        populateTimeline();
    }


    public void populateTimeline(){
        //TODO: populate the timeline by reading in data from PARSE
        postAdapter.clear();
        //Testing rn by adding fake posts to posts arraylist
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.setLimit(20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts = (ArrayList<Post>) objects;
                    postAdapter.addAll(posts);
                } else {
                    Toast.makeText(TimelineActivity.this, "failed", Toast.LENGTH_LONG).show();
                    Log.d("item", "Error" + e.getMessage());
                }
            }
        });


    }
}
