package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.adapters.PopupAdapter;
import com.way.mat.opentokchat.adapters.RoomsAdapter;
import com.way.mat.opentokchat.items.PopupItem;
import com.way.mat.opentokchat.rest.client.RestClient;
import com.way.mat.opentokchat.rest.models.Room;
import com.way.mat.opentokchat.rest.responses.RoomsResponse;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by matviy on 19.09.16.
 */
public class RoomsActivity extends BaseActivity {

    //popup
    private List<PopupItem> popupItems = new ArrayList<>();
    private ListPopupWindow mPopup;

    @BindView(R.id.settings)
    ImageButton btnSettings;
    //popup end

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
        initPopup();

        loadRooms();
    }

    @OnClick(R.id.settings)
    public void settingsClick(View v) {
        if (mPopup != null) {
            mPopup.show();//showing popup menu
        }
    }

    @OnClick(R.id.fab_create_room)
    public void createRoom() {
        startActivity(new Intent(RoomsActivity.this, CreateRoomActivity.class));
    }

    private void initPopup() {
        popupItems.add(new PopupItem(R.drawable.ic_submenu_username, getString(R.string.action_login), PopupItem.Type.USERNAME));
        popupItems.add(new PopupItem(R.drawable.ic_submenu_about, getString(R.string.action_about), PopupItem.Type.ABOUT));

        mPopup = new ListPopupWindow(this);

        final PopupAdapter adapter = new PopupAdapter(this, popupItems);

        mPopup.setAnchorView(btnSettings);
        mPopup.setAdapter(adapter);
        mPopup.setWidth(getResources().getDimensionPixelSize(R.dimen.popup_width)); // note: don't use pixels, use a dimen resource
        mPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchActivity(popupItems.get(i).getType());
                if (mPopup != null) {
                    mPopup.dismiss();
                }
            }
        }); // the callback for when a list item is selected

    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_rooms;
    }

    private void initRecycler() {
        mAdapter = new RoomsAdapter(this, mRooms);
        mAdapter.setCallback(new RoomsAdapter.Callback() {
            @Override
            public void onRoomSelected(Room room) {
                if (!isLoading) {
                    Prefs.putString(PrefKeys.SESSION_ID, room.getSessionId());
//                    Prefs.putString(PrefKeys.TOKEN, room.getToken());
                    Intent intent = new Intent(RoomsActivity.this, ConferenceActivity.class);
                    intent.putExtra("room", room.serialize());
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

        Call<RoomsResponse> call = RestClient.getApiService().getRooms();
        call.enqueue(new Callback<RoomsResponse>() {
            @Override
            public void onResponse(Call<RoomsResponse> call, Response<RoomsResponse> response) {
                if (mRooms != null && response != null && response.body() != null && response.body().isSuccessful() && !response.body().getResponse().isEmpty()) {
                    mRooms.clear();
                    mRooms.addAll(response.body().getResponse());
                    if (mRooms.size() > 0) {
                        hideNoRooms();
                    } else {
                        showNoRooms();
                    }
                    mAdapter.setItems(mRooms);
                } else {
                    Toast.makeText(RoomsActivity.this, "Empty response", Toast.LENGTH_SHORT).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<RoomsResponse> call, Throwable t) {
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

    private void launchActivity(PopupItem.Type type) {
        if (type != null) {
            switch (type) {
                case USERNAME:
                    startActivity(new Intent(RoomsActivity.this, LoginActivity.class));
                    break;
                case ABOUT:
                    startActivity(new Intent(RoomsActivity.this, AboutActivity.class));
                    break;
            }
        }
    }

}
