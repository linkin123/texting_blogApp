package com.example.texting.mainModule.view;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.texting.addModule.view.AddFragment;
import com.example.texting.R;
import com.example.texting.chatModule.view.ChatActivity;
import com.example.texting.common.Constants;
import com.example.texting.common.model.pojo.User;
import com.example.texting.common.model.utils.UtilsCommon;
import com.example.texting.loginModule.view.LoginActivity;
import com.example.texting.mainModule.MainPresenter;
import com.example.texting.mainModule.MainPresenterClass;
import com.example.texting.mainModule.view.adapters.OnItemClickListener;
import com.example.texting.mainModule.view.adapters.RequestAdapter;
import com.example.texting.mainModule.view.adapters.UserAdapter;
import com.example.texting.profileModule.view.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, MainView {

    private static final int RC_PROFILE = 23;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rvRequests)
    RecyclerView rvRequests;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.content_main)
    CoordinatorLayout contentMain;

    private UserAdapter mUserAdapter;
    private RequestAdapter mRequestAdapter;

    private MainPresenter mPresenter;

    private User mUser;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenterClass(this);
        mPresenter.onCreate();
        mUser = mPresenter.getCurrentUser();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        configToolbar();
        configAdapter();
        configRecyclerView();
        configTutorial();
    }

    private void configTutorial() {
        new MaterialShowcaseView.Builder(this)
                .setTitleTextColor(ContextCompat.getColor(this , R.color.colorAccent))
                .setContentTextColor(ContextCompat.getColor(this , R.color.blue_a100))
                .setDismissTextColor(ContextCompat.getColor(this , android.R.color.white))
                .setMaskColour(ContextCompat.getColor(this , R.color.gray_900_t))
                .setTarget(fab)
                .setTargetTouchable(true)
                .setTitleText(R.string.app_name)
                .setContentText(R.string.main_tuto_message)
                .setDismissText(R.string.main_tuto_ok)
                .setDismissStyle(Typeface.create(Typeface.SANS_SERIF , Typeface.BOLD_ITALIC))
                .singleUse(getString(R.string.main_tuto_fab_Add))
                .setDelay(2000)
                .setFadeDuration(600)
                .setDismissOnTouch(true)
                .setDismissOnTouch(true)
                .show();
    }

    private void configToolbar() {
        toolbar.setTitle(mUser.getUsernmeValid());
        UtilsCommon.loadImage(this, mUser.getPhotoUrl(), imgProfile);
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        mUserAdapter = new UserAdapter(new ArrayList<>(), this);
        mRequestAdapter = new RequestAdapter(new ArrayList<>(), this);
    }


    private void configRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(mUserAdapter);

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(mRequestAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mPresenter.signOff();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.action_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                intentProfile.putExtra(User.USERNAME, mUser.getUsername());
                intentProfile.putExtra(User.EMAIL, mUser.getEmail());
                intentProfile.putExtra(User.PHOTO_URL, mUser.getPhotoUrl());

                if(UtilsCommon.hasMaterialDesign()){
                    startActivityForResult(intentProfile, RC_PROFILE,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }else{
                    startActivityForResult(intentProfile, RC_PROFILE);
                }
                break;
            case R.id.action_about:
                openAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RC_PROFILE:
                    if(data != null){
                        mUser.setUsername(data.getStringExtra(User.USERNAME));
                        mUser.setPhotoUrl(data.getStringExtra(User.PHOTO_URL));
                        configToolbar();
                    }
            }
        }
    }

    private void openAbout() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogFragmentTheme)
                .setTitle(R.string.main_menu_about)
                .setView(view)
                .setPositiveButton(R.string.common_label_ok, null)
                .setNeutralButton(R.string.about_privacy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://policity.cursos-android-ant.com"));
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    @OnClick(R.id.fab)
    public void onAddClicked() {
        new AddFragment().show(getSupportFragmentManager() , getString(R.string.addFriend_title));
    }

    /*
     * MainVIew
     * */

    @Override
    public void friendAdded(User user) {
        mUserAdapter.add(user);
    }

    @Override
    public void friendUpdated(User user) {
        mUserAdapter.update(user);
    }

    @Override
    public void friendRemoved(User user) {
        mUserAdapter.remove(user);
    }

    @Override
    public void requestAdded(User user) {
        mRequestAdapter.add(user);
    }

    @Override
    public void requestUpdated(User user) {
        mRequestAdapter.update(user);
    }

    @Override
    public void requestRemoved(User user) {
        mRequestAdapter.remove(user);
    }

    @Override
    public void showRequestAccepted(String username) {
        Snackbar.make(contentMain, getString(R.string.main_message_request_accepted, username), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRequestDenied() {
        Snackbar.make(contentMain, getString(R.string.main_message_request_denied), Snackbar.LENGTH_SHORT).show();

        mFirebaseAnalytics.logEvent(Constants.EVENT_REQUEST_DENIED , null);
    }

    @Override
    public void showFriendRemoved() {
        Snackbar.make(contentMain, getString(R.string.main_message_user_removed), Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void showError(int resMsg) {
        Snackbar.make(contentMain, resMsg, Snackbar.LENGTH_SHORT).show();
    }



    /*
     *   OnItemClickListener
     * */

    @Override
    public void onItemClick(User user) {
        Intent intent = new Intent(this , ChatActivity.class);
        intent.putExtra(User.UID , user.getUid());
        intent.putExtra(User.USERNAME , user.getUsername());
        intent.putExtra(User.EMAIL, user.getEmail());
        intent.putExtra(User.PHOTO_URL, user.getPhotoUrl());

        if (UtilsCommon.hasMaterialDesign()) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }else{
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final User user) {
        new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(getString(R.string.main_dialog_title_confirmDelete))
                .setMessage(String.format(Locale.ROOT, getString(R.string.main_dialog_message_confirmDelete),
                        user.getUsernmeValid()))
                .setPositiveButton(R.string.main_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.removeFriend(user.getUid());
                    }
                }).setNegativeButton(R.string.common_label_cancel, null)
                .show();
    }

    @Override
    public void onAcceptRequest(User user) {
        mPresenter.acceptRequest(user);
    }

    @Override
    public void onDenyRequest(User user) {
        mPresenter.denyRequest(user);
    }



}
