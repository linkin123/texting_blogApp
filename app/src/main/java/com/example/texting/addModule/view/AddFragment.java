package com.example.texting.addModule.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.texting.addModule.AddPresenter;
import com.example.texting.addModule.AddPresenterClass;
import com.example.texting.R;
import com.example.texting.common.Constants;
import com.example.texting.common.model.utils.UtilsCommon;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends DialogFragment implements DialogInterface.OnShowListener, AddView {


    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contentMain)
    FrameLayout contentMain;


    private Button positiveButton;
    private AddPresenter mPresenter;

    Unbinder unbinder;

    private FirebaseAnalytics mFirebaseAnalytics;


    public AddFragment() {
        // Required empty public constructor
        mPresenter = new AddPresenterClass(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.addFriend_title)
                .setPositiveButton(R.string.common_label_accept, null)
                .setNegativeButton(R.string.common_label_cancel, null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null){
            positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UtilsCommon.validateEmail(getActivity() , etEmail))
                    mPresenter.addFriend(etEmail.getText().toString().trim());

                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        mPresenter.onShow();

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }


    @Override
    public void enabledUIElements() {
        etEmail.setEnabled(true);
        positiveButton.setEnabled(true);
    }

    @Override
    public void disabledUIElements() {
        etEmail.setEnabled(false);
        positiveButton.setEnabled(false);
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
    public void friendAdded() {
        Toast.makeText(getActivity(), R.string.addFriend_message_request_dispatched, Toast.LENGTH_SHORT)
                .show();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.PARAM_CONTEXT , AddFragment.class.getName());
        mFirebaseAnalytics.logEvent(Constants.EVENTS_ADD_FRIENDS, bundle);
        dismiss();
    }

    @Override
    public void friendNotAdded() {
        etEmail.setError(getString(R.string.addFriend_error_message));
        etEmail.requestFocus();
    }
}
