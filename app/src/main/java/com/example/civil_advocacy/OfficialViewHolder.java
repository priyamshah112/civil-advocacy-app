package com.example.civil_advocacy;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    protected final TextView positionTextView;
    protected final TextView namePartyTextView;
    protected final ImageView officialImageView;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        positionTextView = itemView.findViewById(R.id.positionTextView);
        namePartyTextView = itemView.findViewById(R.id.namePartyTextView);
        officialImageView = itemView.findViewById(R.id.officialImageView);
    }

    public void bind(OfficialModel officialModel) {
        positionTextView.setText(officialModel.getPosition());
        String nameParty = String.format("%s (%s)", officialModel.getName(), officialModel.getPartyAffiliation());
        namePartyTextView.setText(nameParty);

        String photoURL = officialModel.getPhotoURL();
        if (!TextUtils.isEmpty(photoURL)) {
            Picasso.get().load(photoURL).into(officialImageView);
        } else {
            Picasso.get().load(R.drawable.missing).into(officialImageView);
        }
    }
}
