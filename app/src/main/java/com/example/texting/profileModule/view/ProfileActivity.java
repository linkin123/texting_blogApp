package com.example.texting.profileModule.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.texting.R;
import com.example.texting.common.model.pojo.User;
import com.example.texting.common.model.utils.UtilsCommon;
import com.example.texting.common.model.utils.UtilsImage;
import com.example.texting.profileModule.ProfilePresenter;
import com.example.texting.profileModule.ProfilePresenterClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    public static final int RC_PHOTO_PICKER = 22;
    @BindView(R.id.btnEditPhoto)
    ImageButton btnEditPhoto;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progressBarImage)
    ProgressBar progressBarImage;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.etUsername)
    TextInputEditText etUsername;
    @BindView(R.id.contentMain)
    LinearLayout contentMain;

    private MenuItem mCurrentMenuItem;
    private ProfilePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mPresenter = new ProfilePresenterClass(this);
        mPresenter.onCreate();
        mPresenter.setupUser(getIntent().getStringExtra(User.USERNAME),
                getIntent().getStringExtra(User.EMAIL),
                getIntent().getStringExtra(User.PHOTO_URL));
        configActionBar();
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setImageProfile(String photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_timer_sand)
                .error(R.drawable.ic_emoticon_sad)
                .centerCrop();

        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        hideProgressImage();
                        imgProfile.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this,
                                R.drawable.ic_upload));

                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        imgProfile.setImageBitmap(resource);
                        hideProgressImage();
                        return true;
                    }
                }).into(imgProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_profile:
                mCurrentMenuItem = item;
                if (etUsername.getText() != null) {
                    mPresenter.updateUsername(etUsername.getText().toString().trim());
                }
                break;
            case android.R.id.home:
                if (UtilsCommon.hasMaterialDesign()) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    @OnClick({R.id.btnEditPhoto, R.id.imgProfile})
    public void onSelectPhoto(View view) {
        mPresenter.checkMode();
    }

    /*
     *   ProfileView
     * */

    @Override
    public void enabledUIElements() {
        setInput(true);

    }


    @Override
    public void disabledUIElements() {
        setInput(false);
    }

    private void setInput(boolean enabled) {
        etUsername.setEnabled(enabled);
        btnEditPhoto.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (mCurrentMenuItem != null) {
            mCurrentMenuItem.setEnabled(enabled);
        }
    }


    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressImage() {
        progressBarImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressImage() {
        progressBarImage.setVisibility(View.GONE);
    }

    @Override
    public void showUserData(String username, String email, String photoUrl) {
        setImageProfile(photoUrl);
        etUsername.setText(username);
        etEmail.setText(email);
    }

    @Override
    public void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_PHOTO_PICKER);
    }

    @Override
    public void openDialogPreview(Intent data) {
        final String urlLocal = data.getDataString();
        final ViewGroup nullParent = null;
        View view = getLayoutInflater().inflate(R.layout.dialog_image_upload_preview, nullParent);
        final ImageView imgDialog = view.findViewById(R.id.imgDialog);
        final TextView tvMessage = view.findViewById(R.id.tv_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.profile_dialog_title)
                .setPositiveButton(R.string.profile_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateImage(ProfileActivity.this, Uri.parse(urlLocal));
                        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_imageUploading, Snackbar.LENGTH_LONG);
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int sizeImagePreview = getResources().getDimensionPixelSize(R.dimen.chat_size_img_preview);
                Bitmap bitmap = UtilsImage.reduceBitmap(ProfileActivity.this, contentMain,
                        urlLocal, sizeImagePreview, sizeImagePreview);

                if (bitmap != null) {
                    imgDialog.setImageBitmap(bitmap);
                }
                tvMessage.setText(R.string.profile_dialog_message);
            }
        });
        alertDialog.show();

    }

    @Override
    public void menuEditMode() {
        mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check));
    }

    @Override
    public void menuNormalMode() {
        if (mCurrentMenuItem != null) {
            mCurrentMenuItem.setEnabled(true);
            mCurrentMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pencil));
        }
    }

    @Override
    public void saveUsernameSuccess() {
        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_userUpdated);
    }

    @Override
    public void updateImageSuccess(String photoUrl) {
        setImageProfile(photoUrl);
        UtilsCommon.showSnackbar(contentMain, R.string.profile_message_imageUpdated);
    }

    @Override
    public void setResultOK(String username, String photoUrl) {
        Intent data = new Intent();
        data.putExtra(User.USERNAME, username);
        data.putExtra(User.PHOTO_URL, photoUrl);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onErrorUpload(int resMgs) {
        UtilsCommon.showSnackbar(contentMain, resMgs);
    }

    @Override
    public void onError(int resMgs) {
        etUsername.requestFocus();
        etUsername.setError(getString(resMgs));
    }


}
