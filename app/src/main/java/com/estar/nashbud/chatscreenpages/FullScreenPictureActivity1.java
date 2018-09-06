package com.estar.nashbud.chatscreenpages;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estar.nashbud.R;
import com.estar.nashbud.post.Post_Model;
import com.estar.nashbud.utils.SharedPreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

/**
 * Created by User on 12-12-2017.
 */

public class FullScreenPictureActivity1 extends DialogFragment {
    ImageView imageFullDp;
    Toolbar toolbar;
    Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @State
    String userUid;
    private Post_Model user;
    private FirebaseUser owner;
    SharedPreference sharedPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.full_screen_pic_activity, null);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(getActivity());
        context = getActivity();
        imageFullDp = v.findViewById(R.id.image_dp);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreference = new SharedPreference();

        if (savedInstanceState == null) {
            userUid = sharedPreference.getFullPicData(context);
            //userUid = getActivity().getIntent().getStringExtra("user_id");
            Log.e("user id full screen pic","" + userUid);
        }
        loadUserDetails();
        initializeAuthListener();

        return v;
    }

    private void loadUserDetails() {
        DatabaseReference userReference = mDatabase
                .child("posts")
                .child(userUid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Post_Model.class);
                //initializeMessagesRecycler();
                displayUserDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, R.string.error_loading_user, Toast.LENGTH_SHORT).show();
                //context.finish();
            }
        });
    }

    private void displayUserDetails() {
        String profileUrl = user.getProfilePic();
        try {
            if (!(profileUrl.equals("") || profileUrl.equals("null") || profileUrl.equals(null))) {
                Glide.with(context)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.profile)
                        .centerCrop()
                        .dontAnimate()
                        //.bitmapTransform(new CropCircleTransformation(context))
                        .into(imageFullDp);
            } else {
                Log.e("profile is", "null");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeAuthListener() {

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMMM-YYYY");
        String CurrentDate = simpleDateFormat.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH : mm");
        String CurrentTime = simpleTimeFormat.format(calendarTime.getTime());

        final String RandomSave = CurrentDate + CurrentTime;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                owner = firebaseAuth.getCurrentUser();
                if (owner != null) {
                    //initializeMessagesRecycler();

                    Log.d("@@@@", "thread:signed_in:" + owner.getUid()+RandomSave);
                } else {
                    Log.d("@@@@", "thread:signed_out");

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }


}
