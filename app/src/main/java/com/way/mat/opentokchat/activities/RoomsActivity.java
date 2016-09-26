package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.adapters.RoomsAdapter;
import com.way.mat.opentokchat.rest.client.RestClient;
import com.way.mat.opentokchat.rest.models.Room;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matviy on 19.09.16.
 */
public class RoomsActivity extends BaseActivity {

    private RoomsAdapter mAdapter;
    private List<Room> mRooms = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.no_rooms)
    TextView tvNoRooms;

    private boolean isLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_rooms);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        initRecycler();

        loadRooms();
    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_rooms;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_login:
                startActivity(new Intent(RoomsActivity.this, LoginActivity.class));
                return true;

            case R.id.action_about:
                startActivity(new Intent(RoomsActivity.this, AboutActivity.class));
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void initRecycler() {
//        mRooms.add(new Room("Room 1", "Description 1", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQyMTY0OH5weXlBRU5LS2pkdVc0aWxQM2dxNGtNTjV-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9MzMxZDc1Mjg2ZTE2YjcyYmZhMGU3NmJlODEyNjJlYTYyZDllZWYwMTpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRReU1UWTBPSDV3ZVhsQlJVNUxTMnBrZFZjMGFXeFFNMmR4Tkd0TlRqVi1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDI5Jm5vbmNlPTAuNzQwOTQ2ODU5ODE5ODE0NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDI5", "http://www.designofsignage.com/application/symbol/building/image/600x600/meeting-room.jpg"));
//        mRooms.add(new Room("Room 2", "Description 2", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQ0MjkzMH5BMm1LTHFwelkyT0NPQUY3Qm9JQi8xSVF-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9NTQ4MGFjNzU4NDZiMWZiZTNkMGViNDMzMjVlODJlNmRjM2RiOTdmMTpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRRME1qa3pNSDVCTW0xTFRIRndlbGt5VDBOUFFVWTNRbTlKUWk4eFNWRi1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDQ5Jm5vbmNlPTAuMTMyMjU3NzcwNTE0MTE1NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDQ4", "http://www.digitaldeskapp.com/wp-content/uploads/2013/10/DD-Icon-Video.png"));
//        mRooms.add(new Room("Room 3", "Description 3", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQ2MTY5Mn5JVWF4MTIxd2xHczdyZXJnTmVUaDQyYkh-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9ZGRkNDgyYTNkYzBkOGU4NjU2NzBkMDVlZDg5ZjhlYWE0MTA2Y2IyZDpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRRMk1UWTVNbjVKVldGNE1USXhkMnhIY3pkeVpYSm5UbVZVYURReVlraC1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDY4Jm5vbmNlPTAuNjMzOTM1MjM3NTM2MjA2OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDY3", "http://files.softicons.com/download/application-icons/ichat-emoticon-icons-by-taylor-carrigan/png/512x512/iChat%20Video%20Bubble.png"));

        mAdapter = new RoomsAdapter(this, mRooms);
        mAdapter.setCallback(new RoomsAdapter.Callback() {
            @Override
            public void onRoomSelected(Room room) {
                if (!isLoading) {
                    Prefs.putString(PrefKeys.SESSION_ID, room.getSessionId());
//                    Prefs.putString(PrefKeys.TOKEN, room.getToken());
                    Intent intent = new Intent(RoomsActivity.this, ConferenceActivity.class);
                    startActivity(intent);
                }
            }
        });
        mLayoutManager = new LinearLayoutManager(this);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRooms();
            }
        });
    }

    private void loadRooms() {
        showLoading();

        Call<List<Room>> call = RestClient.getApiService().getRooms();
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (mRooms != null && response != null && response.body() != null && !response.body().isEmpty()) {
                    mRooms.clear();
                    mRooms.addAll(response.body());
                    if (mRooms.size() > 0) {
                        hideNoRooms();
                    } else {
                        showNoRooms();
                    }
                    mAdapter.setItems(mRooms);
                } else {
                    Toast.makeText(RoomsActivity.this, "empty response", Toast.LENGTH_SHORT).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(RoomsActivity.this, "Error loading rooms", Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

    private void showNoRooms() {
        if (tvNoRooms != null) {
            tvNoRooms.setVisibility(View.VISIBLE);
        }
    }

    private void hideNoRooms() {
        if (tvNoRooms != null) {
            tvNoRooms.setVisibility(View.GONE);
        }
    }

    private void showLoading() {
        isLoading = true;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (mRecycler != null) {
            mRecycler.setEnabled(false);
        }
    }

    private void hideLoading() {
        isLoading = false;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (mRecycler != null) {
            mRecycler.setEnabled(true);
        }
    }

}
