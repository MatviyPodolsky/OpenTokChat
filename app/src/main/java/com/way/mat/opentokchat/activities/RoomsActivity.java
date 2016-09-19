package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.adapters.PopupAdapter;
import com.way.mat.opentokchat.adapters.RoomsAdapter;
import com.way.mat.opentokchat.items.PopupItem;
import com.way.mat.opentokchat.items.Room;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by matviy on 19.09.16.
 */
public class RoomsActivity extends BaseActivity {

    private RoomsAdapter mAdapter;
    private List<Room> mRooms = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    private ListPopupWindow mPopup;
    private List<PopupItem> popupItems = new ArrayList<>();

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.settings)
    ImageButton btnSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_rooms);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        initRecycler();
        initPopup();
    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_rooms;
    }

    @OnClick(R.id.settings)
    public void settingsClick(View v) {
        if (mPopup != null) {
            mPopup.show();//showing popup menu
        }
    }

    private void initPopup() {
        popupItems.add(new PopupItem(R.drawable.ic_menu_edit_normal, getString(R.string.action_login), PopupItem.Type.USERNAME));
        popupItems.add(new PopupItem(R.drawable.ic_menu_list_normal, getString(R.string.action_about), PopupItem.Type.ABOUT));

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

    private void initRecycler() {
        mRooms.add(new Room("Room 1", "Description 1", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQyMTY0OH5weXlBRU5LS2pkdVc0aWxQM2dxNGtNTjV-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9MzMxZDc1Mjg2ZTE2YjcyYmZhMGU3NmJlODEyNjJlYTYyZDllZWYwMTpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRReU1UWTBPSDV3ZVhsQlJVNUxTMnBrZFZjMGFXeFFNMmR4Tkd0TlRqVi1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDI5Jm5vbmNlPTAuNzQwOTQ2ODU5ODE5ODE0NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDI5"));
        mRooms.add(new Room("Room 2", "Description 2", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQ0MjkzMH5BMm1LTHFwelkyT0NPQUY3Qm9JQi8xSVF-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9NTQ4MGFjNzU4NDZiMWZiZTNkMGViNDMzMjVlODJlNmRjM2RiOTdmMTpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRRME1qa3pNSDVCTW0xTFRIRndlbGt5VDBOUFFVWTNRbTlKUWk4eFNWRi1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDQ5Jm5vbmNlPTAuMTMyMjU3NzcwNTE0MTE1NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDQ4"));
        mRooms.add(new Room("Room 3", "Description 3", "1_MX40NTY0NDY5Mn5-MTQ3NDI5NDQ2MTY5Mn5JVWF4MTIxd2xHczdyZXJnTmVUaDQyYkh-fg", "T1==cGFydG5lcl9pZD00NTY0NDY5MiZzaWc9ZGRkNDgyYTNkYzBkOGU4NjU2NzBkMDVlZDg5ZjhlYWE0MTA2Y2IyZDpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORFk1TW41LU1UUTNOREk1TkRRMk1UWTVNbjVKVldGNE1USXhkMnhIY3pkeVpYSm5UbVZVYURReVlraC1mZyZjcmVhdGVfdGltZT0xNDc0Mjk0NDY4Jm5vbmNlPTAuNjMzOTM1MjM3NTM2MjA2OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDc2ODg2NDY3"));

        mAdapter = new RoomsAdapter(mRooms);
        mAdapter.setCallback(new RoomsAdapter.Callback() {
            @Override
            public void onRoomSelected(Room room) {
                Prefs.putString(PrefKeys.SESSION_ID, room.getSessionId());
                Prefs.putString(PrefKeys.TOKEN, room.getToken());
                Intent intent = new Intent(RoomsActivity.this, ConferenceActivity.class);
                startActivity(intent);
            }
        });
        mLayoutManager = new LinearLayoutManager(this);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));

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
