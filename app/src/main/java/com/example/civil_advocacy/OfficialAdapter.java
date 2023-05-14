package com.example.civil_advocacy;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "OfficialAdapter";
    private final List<OfficialModel> officialList;
    private final MainActivity mainActivity;

    public OfficialAdapter(List<OfficialModel> officialList, MainActivity mainActivity) {
        this.officialList = officialList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_official, parent, false);

        itemView.setOnClickListener(mainActivity);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {

        OfficialModel officialModel = officialList.get(position);

        holder.positionTextView.setText(officialModel.getPosition());
        String nameParty = String.format("%s (%s)", officialModel.getName(), officialModel.getPartyAffiliation());
        holder.namePartyTextView.setText(nameParty);

        String photoURL = officialModel.getPhotoURL();
        if (!TextUtils.isEmpty(photoURL)) {
            Picasso.get().load(photoURL).into(holder.officialImageView);
        } else {
            Picasso.get().load(R.drawable.missing).into(holder.officialImageView);
        }
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}

