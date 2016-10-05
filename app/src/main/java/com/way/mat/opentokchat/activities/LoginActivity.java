package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.utils.PrefKeys;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by oleh on 01.09.16.
 */
public class LoginActivity extends BaseActivity {

    private static final String LOGTAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.saveLogin)
    ImageButton saveLogin;
    @BindView(R.id.edLogin)
    EditText login;

    private boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isFirstTime = extras.getBoolean("first", false);
        }

        if (isFirstTime) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }

        setTitle(R.string.title_login);

        login.setText(Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING));

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_login;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.saveLogin)
    public void saveLogin() {
        if (!isFinishing()) {
            final String prevLogin = Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING);
            final String currentLogin = String.valueOf(login.getText());
            Log.d(LOGTAG, "prevLogin: " + prevLogin);
            Log.d(LOGTAG, "currentLogin: " + currentLogin);

            if (!prevLogin.equals(currentLogin)) {
                Prefs.putString(PrefKeys.LOGIN_USER, currentLogin);
            }

            Toast.makeText(this, "Login saved", Toast.LENGTH_SHORT).show();
            if (isFirstTime) {
                startActivity(new Intent(LoginActivity.this, RoomsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
