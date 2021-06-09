package com.example.texting.mainModule.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.texting.R;
import com.example.texting.common.model.pojo.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;
    private OnItemClickListener mListener;


    public RequestAdapter(List<User> mUsers, OnItemClickListener onItemClickListener) {
        this.mUsers = mUsers;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request,
                parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.setOnclickListener(user , mListener );

        holder.tvName.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(options)
                .into(holder.imgPhoto);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void add(User user){
        if(!mUsers.contains(user)){
            mUsers.add(user);
            notifyItemInserted(mUsers.size()-1);
        }else{
            update(user);
        }
    }

    public void update(User user) {
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.set(index , user);
            notifyItemChanged(index);
        }
    }
    public void remove(User user){
        if(mUsers.contains(user)){
            int index = mUsers.indexOf(user);
            mUsers.remove(index);
            notifyItemRemoved(index);
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnDeny)
        ImageButton btnDeny;
        @BindView(R.id.btnAccept)
        ImageButton btnAccept;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvEmail)
        TextView tvEmail;
        @BindView(R.id.imgPhoto)
        CircleImageView imgPhoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }

        void setOnclickListener(User user, OnItemClickListener listener){
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptRequest(user);
                }
            });

            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDenyRequest(user);
                }
            });
        }


    }
}
