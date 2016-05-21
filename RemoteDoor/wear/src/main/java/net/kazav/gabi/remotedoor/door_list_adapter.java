package net.kazav.gabi.remotedoor;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gabik on 5/16/16.
 * door list adapter
 */
public class door_list_adapter extends WearableListView.Adapter  {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private DataMap[] mDoorMap;

    public DataMap get_by_pos(int pos) { return mDoorMap[pos]; }

    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;
        private String doorid;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.door_name);
        }
        public void setId(String doorid) {
            this.doorid = doorid;
        }
    }

    public door_list_adapter(Context context, List<Integer> datasetkeys) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDoorMap = new DataMap[datasetkeys.size()];
        int c = 0;
        Log.w(dataLayer.TAG, "Dataset size: " + Integer.toString(datasetkeys.size()));
        for (Integer i : datasetkeys) {
            DataMap dm = prefs.get_door(context, i);
            dm.putString("doorid", Integer.toString(i));
            mDoorMap[c++] = dm;
        }
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.up_door_widget, null));
    }

    @Override
    public void onBindViewHolder(final WearableListView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.textView;
        view.setText(mDoorMap[position].getString("caption"));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDoorMap.length;
    }
}
