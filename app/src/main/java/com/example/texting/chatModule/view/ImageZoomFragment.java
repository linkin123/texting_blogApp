package com.example.texting.chatModule.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.texting.R;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImageZoomFragment extends DialogFragment implements DialogInterface.OnShowListener {

    @BindView(R.id.pZoom)
    PhotoView pZoom;
    @BindView(R.id.contentMain)
    RelativeLayout contentMain;
    Unbinder unbinder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_image_zoom,
                null, false);

        unbinder = ButterKnife.bind(this  ,view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmentTheme);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if(getActivity() != null){
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_timer_sand_160);

            Glide.with(getActivity())
                    .load(((OnImageZoom)getActivity()).getMessageSelected().getPhotoUrl())
                    .apply(options)
                    .into(pZoom);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if(window != null){
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }
        contentMain.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
