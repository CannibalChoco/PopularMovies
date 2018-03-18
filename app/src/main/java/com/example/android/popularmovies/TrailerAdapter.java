package com.example.android.popularmovies;


import android.content.Context;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<MovieTrailer> trailers;
    private Context context;

    private static ListItemListener onClickListener;

    public interface ListItemListener {
        void onListItemClick(int position);
    }

    public TrailerAdapter(Context context, List<MovieTrailer> trailers, ListItemListener listener){
        this.context = context;
        this.trailers = trailers;
        onClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieTrailer trailer = trailers.get(position);

        String trailerKey = trailer.getKey();

        if (trailerKey != null && !TextUtils.isEmpty(trailerKey)){
            String thumbnailUrl = NetworkUtils.buildUrlForMovieThumbnail(trailerKey);
            Picasso.with(context).load(thumbnailUrl).into(holder.thumbnailIv);
        }
    }

    @Override
    public int getItemCount() {
        return trailers != null ? trailers.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ivThumbnail) ImageView thumbnailIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onClickListener.onListItemClick(position);
        }
    }

    /**
     * Clear the adapter
     */
    public void clear() {
        int size = getItemCount();
        this.trailers.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * adds all reviews to the adapter
     */
    public void addAll(List<MovieTrailer> trailers) {
        this.trailers.addAll(trailers);
        notifyDataSetChanged();
    }
}