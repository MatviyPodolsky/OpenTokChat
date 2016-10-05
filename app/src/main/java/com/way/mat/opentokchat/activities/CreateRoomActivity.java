package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.dialogs.SwitchCameraDialog;
import com.way.mat.opentokchat.rest.client.RestClient;
import com.way.mat.opentokchat.rest.models.Room;
import com.way.mat.opentokchat.rest.requests.CreateRoomRequest;
import com.way.mat.opentokchat.rest.responses.CreateRoomResponse;
import com.way.mat.opentokchat.rest.responses.FileUploadResponse;
import com.way.mat.opentokchat.utils.LocalFilesUtil;
import com.way.mat.opentokchat.utils.PathUtil;
import com.way.mat.opentokchat.utils.PrefKeys;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matviy on 05.10.16.
 */
public class CreateRoomActivity extends BaseActivity {

    public static final int REQUEST_SELECT_PHOTO = 100;
    public static final int REQUEST_PICK_PHOTO = 101;

    @BindView(R.id.create)
    ImageButton actionCreate;
    @BindView(R.id.btn_upload)
    AppCompatButton btnUpload;
    @BindView(R.id.et_name)
    AppCompatEditText etName;
    @BindView(R.id.et_description)
    AppCompatEditText etDescription;
    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.til_description)
    TextInputLayout tilDescription;
    @BindView(R.id.preview)
    ImageView imgPreview;

    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;
    @BindView(R.id.rl_uploading_progress)
    RelativeLayout rlUploadingProgress;

    private Uri selectedImage;
    private Uri mMakePhotoUri;
    private String imageLink;
    private boolean isUploaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_create_room);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName.addTextChangedListener(new CustomTextWatcher(tilName));
        etDescription.addTextChangedListener(new CustomTextWatcher(tilDescription));

    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_create_room;
    }

    @OnClick(R.id.create)
    public void create() {
        if (isValidData()) {
            showProgress();

            CreateRoomRequest request = new CreateRoomRequest(etName.getText().toString(), etDescription.getText().toString(), imageLink);
            Call<CreateRoomResponse> call = RestClient.getApiService().createRoom(request);
            call.enqueue(new Callback<CreateRoomResponse>() {
                @Override
                public void onResponse(Call<CreateRoomResponse> call, Response<CreateRoomResponse> response) {
                    if (response != null && response.body() != null) {
                        Room room = response.body().getResponse();
                        Prefs.putString(PrefKeys.SESSION_ID, room.getSessionId());
                        Intent intent = new Intent(CreateRoomActivity.this, ConferenceActivity.class);
                        intent.putExtra("room", room.serialize());
                        startActivity(intent);
                        Toast.makeText(CreateRoomActivity.this, "Room created", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateRoomActivity.this, "Empty response", Toast.LENGTH_SHORT).show();
                    }
                    hideProgress();
                }

                @Override
                public void onFailure(Call<CreateRoomResponse> call, Throwable t) {
                    Toast.makeText(CreateRoomActivity.this, "Error creating room", Toast.LENGTH_SHORT).show();
                    hideProgress();
                }
            });
        }
    }

    @OnClick(R.id.btn_upload)
    public void selectFile() {
        showCameraDialog();
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_PHOTO:
                    selectedImage = data.getData();
                    break;
                case REQUEST_PICK_PHOTO:
                    if (data != null && data.getData() != null) {
                        selectedImage = data.getData();
                    } else {
                        selectedImage = mMakePhotoUri;
                        mMakePhotoUri = null;
                    }
                    break;
            }
            Glide.with(this).load(selectedImage).fitCenter().into(imgPreview);
//            Picasso.with(CreateRoomActivity.this).load(selectedImage).centerInside().fit().into(imgPreview);
            uploadFile(selectedImage);
        }
    }

    private boolean isValidData() {
        boolean result = true;
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        if (TextUtils.isEmpty(name)) {
            result = false;
            tilName.setError(getString(R.string.empty_name));
        }
        if (TextUtils.isEmpty(description)) {
            result = false;
            tilDescription.setError(getString(R.string.empty_description));
        }
        if (selectedImage == null) {
            result = false;
            Toast.makeText(CreateRoomActivity.this, "Please, select room logo", Toast.LENGTH_SHORT).show();
        } else if (!isUploaded) {
            result = false;
            Toast.makeText(CreateRoomActivity.this, "Please, wait until the file will be uploaded", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(imageLink)) {
            result = false;
            Toast.makeText(CreateRoomActivity.this, "Invalid uploaded image url. Please try to upload image again.", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void showProgress() {
        if (rlProgress != null) {
            rlProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (rlProgress != null) {
            rlProgress.setVisibility(View.GONE);
        }
    }

    private void showUploadingProgress() {
        if (rlUploadingProgress != null) {
            rlUploadingProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideUploadingProgress() {
        if (rlUploadingProgress != null) {
            rlUploadingProgress.setVisibility(View.GONE);
        }
    }

    private void enableButtons() {
        btnUpload.setEnabled(true);
    }

    private void disableButtons() {
        btnUpload.setEnabled(false);
    }

    private void uploadFile(Uri fileUri) {
        imageLink = "";
        isUploaded = false;
        disableButtons();
        showUploadingProgress();

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = PathUtil.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "description";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
//        Call<List<Room>> call = RestClient.getApiService().getRooms();
        Call<FileUploadResponse> call = RestClient.getApiService().upload(description, body);
        call.enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(Call<FileUploadResponse> call,
                                   Response<FileUploadResponse> response) {
                if (response != null && response.body() != null && response.body().isSuccessful()) {
                    imageLink = response.body().getResponse().getImageUrl();
                    Log.v("Upload", "success");
                    isUploaded = true;
                } else {
                    if (response != null && response.body() != null && !response.body().isSuccessful()) {
                        Toast.makeText(CreateRoomActivity.this, "Error: " + response.body().getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateRoomActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                    }
                    isUploaded = false;
                }
                enableButtons();
                hideUploadingProgress();
            }

            @Override
            public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                Toast.makeText(CreateRoomActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                Log.e("Upload error:", t.getMessage());
                enableButtons();
                hideUploadingProgress();
            }
        });
    }

    private void showCameraDialog() {
        SwitchCameraDialog dialog = SwitchCameraDialog.newInstance();
        dialog.setCallback(new SwitchCameraDialog.Callback() {

            @Override
            public void takePhoto() {
                Intent toPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = null;
                try {
                    f = LocalFilesUtil.createAlbumImageFile();
                } catch (Exception e) {

                }
                if (f != null) {
                    mMakePhotoUri = Uri.fromFile(f);
                    toPhoto.putExtra(MediaStore.EXTRA_OUTPUT, mMakePhotoUri);
                    startActivityForResult(toPhoto, REQUEST_PICK_PHOTO);
                } else {
                    Toast.makeText(CreateRoomActivity.this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void fromGallery() {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_SELECT_PHOTO);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private class CustomTextWatcher implements TextWatcher {

        private TextInputLayout mTil;

        public CustomTextWatcher(TextInputLayout til) {
            mTil = til;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mTil.getError())) {
                mTil.setError("");
                mTil.setErrorEnabled(false);
            }
        }
    }

}
