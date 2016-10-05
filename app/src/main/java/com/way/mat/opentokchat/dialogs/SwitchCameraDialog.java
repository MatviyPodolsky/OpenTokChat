package com.way.mat.opentokchat.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.way.mat.opentokchat.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Alexander on 26.05.2015.
 */

/*!
	\brief Dialog for choosing photo or video
*/
public class SwitchCameraDialog extends DialogFragment {

    protected Callback callback;

    Unbinder unbinder;

    /*!
    Override method, called when dialog created
    \param savedInstanceState - bundle
    \return new dialog
    */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_swich_camera, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);
        return builder.create();
    }

    /*!
    Method for created new instance of dialog
    \return new instance of dialog
    */
    public static SwitchCameraDialog newInstance() {
        SwitchCameraDialog fragment = new SwitchCameraDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    /*!
    Override method, called when dialog created
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*!
    Method for getting photo from camera
    */
    @OnClick(R.id.btn_take_photo)
    public void takePhoto() {
        callback.takePhoto();
        this.dismiss();
    }

    /*!
    Method for getting video from camera
    */
    @OnClick(R.id.btn_from_gallery)
    public void fromGalery() {
        callback.fromGallery();
        this.dismiss();
    }

    /*!
    Override method, called when dialog destroyed
    */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /*!
    Method for setting callback to current view
    \param callback - callback for view
    */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /*!
    \brief Interface for describing behavior of item after some actions
    */
    public interface Callback {
        void takePhoto();
        void fromGallery();
    }

}
