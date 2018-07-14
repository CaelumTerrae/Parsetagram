package me.caelumterrae.parsetagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.ocpsoft.prettytime.PrettyTime;
import org.parceler.Parcels;

import java.util.Date;

import me.caelumterrae.parsetagram.models.Post;

public class DetailsActivity extends AppCompatActivity {

    ParseImageView ivProfile;
    ParseImageView ivPicture;
    TextView tvUsername;
    TextView tvDescription;
    TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ivProfile = findViewById(R.id.ivProfile); //TODO
        ivPicture = findViewById(R.id.ivPicture); //done
        tvUsername = findViewById(R.id.tvUsername); //done
        tvDescription = findViewById(R.id.tvDescription); //done
        tvCreatedAt = findViewById(R.id.tvCreatedAt); //TODO

        Post post = Parcels.unwrap(getIntent().getParcelableExtra("post"));
        ivPicture.setParseFile(post.getMedia());
        ivPicture.loadInBackground();
        tvUsername.setText(post.getUsername());
        tvDescription.setText(post.getDescription());

        PrettyTime p = new PrettyTime();
        Date date = post.getCreatedAt();
        tvCreatedAt.setText(p.format(date));
        post.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ParseUser user = object.getParseUser("user");
                user.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        ParseFile file = object.getParseFile("profilepic");
                        ivProfile.setParseFile(file);
                        ivProfile.loadInBackground();
                    }
                });
            }
        });
    }
}
