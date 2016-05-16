package net.kazav.gabi.remotedoorwear;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;

import java.util.List;

/**
 * Created by gabik on 5/16/16.
 * door list adapter
 */
public class door_list_adapter extends WearableListView.Adapter  {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private DataMap[] mDataset;

    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.door_name);
        }
    }

    public door_list_adapter(Context context, List<Integer> datasetkeys) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = new DataMap[datasetkeys.size()];
        int c = 0;
        Log.w("Dataset size", Integer.toString(datasetkeys.size()));
        for (Integer i : datasetkeys) {
            mDataset[c++] = prefs.get_door(context, i);
        }
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.up_door_widget, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.textView;
        view.setText(mDataset[position].getString("caption"));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
