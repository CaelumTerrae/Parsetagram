package me.caelumterrae.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.ocpsoft.prettytime.PrettyTime;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.caelumterrae.parsetagram.models.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;

    public PostAdapter(List<Post> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.post, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Post post = posts.get(position);

        //TODO: set all of the properties of the post here.
        //e.g. update the images and text and everything
        String username = post.getUsername();
        holder.tvUsername.setText(username);
        holder.tvDescription.setText("@" + username + " " + post.getDescription().toString());
        holder.ivPicture.setParseFile(post.getMedia());
        holder.ivPicture.loadInBackground();


        PrettyTime p = new PrettyTime();
        Date date = post.getCreatedAt();
        holder.tvCreatedAt.setText(p.format(date));

        //get the profile pictures by loading users
        post.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    ParseUser user = (ParseUser) object.get("user");
                    user.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null){
                                ParseFile profilepic = object.getParseFile("profilepic");
                                holder.ivProfile.setParseFile(profilepic);
                                holder.ivProfile.loadInBackground();
                            }else{
                                Toast.makeText(context, "failed to get profile picture", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(context, "failed to get user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public com.parse.ParseImageView ivProfile;
        public com.parse.ParseImageView ivPicture;
        public TextView tvUsername;
        public TextView tvDescription;
        public TextView tvCreatedAt;


        public ViewHolder(View itemView){
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPicture = itemView.findViewById(R.id.ivPicture);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, DetailsActivity.class);
            int position = getAdapterPosition();
            Post post = posts.get(position);
            i.putExtra("post", Parcels.wrap(post));
            context.startActivity(i);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

}

