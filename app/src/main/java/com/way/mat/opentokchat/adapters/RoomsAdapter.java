package com.way.mat.opentokchat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.items.Room;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by matviy on 12.09.16.
 */
public class RoomsAdapter extends RecyclerView.Adapter<PeerHolder> {

    private List<Room> rooms;
    private Callback mCallback;

    public interface Callback {
        void onRoomSelected(Room room);
    }

    public RoomsAdapter(List<Room> rooms) {
        this.rooms = new ArrayList<>();
        if (rooms != null) {
            this.rooms.addAll(rooms);
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setItems(List<Room> rooms) {
        if (rooms != null) {
            if (this.rooms != null) {
                this.rooms.clear();
            } else {
                this.rooms = new ArrayList<>();
            }
            this.rooms.addAll(rooms);
            notifyDataSetChanged();
        }
    }

    @Override
    public PeerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeerHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false));
    }

    @Override
    public void onBindViewHolder(final PeerHolder holder, final int position) {
        final Room room = this.rooms.get(position);
        if (room != null) {
            holder.name.setText(room.getName());
            holder.description.setText(room.getDescription());
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onRoomSelected(room);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms != null ? rooms.size() : 0;
    }

}

class PeerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.icon)
    ImageView icon;


    public PeerHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
