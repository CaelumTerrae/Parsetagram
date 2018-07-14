package me.caelumterrae.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import me.caelumterrae.parsetagram.models.Post;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>{

    List<Post> posts;
    Context context;

    public ProfilePostAdapter(List<Post> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public ProfilePostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.profilepost, parent, false);
        ProfilePostAdapter.ViewHolder viewHolder = new ProfilePostAdapter.ViewHolder(tweetView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final ProfilePostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);

        //TODO: set all of the properties of the post here.
        holder.ivPicture.setParseFile(post.getMedia());
        holder.ivPicture.loadInBackground();

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public com.parse.ParseImageView ivPicture;



        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            ivPicture = itemView.findViewById(R.id.ivPicture);
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



    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
