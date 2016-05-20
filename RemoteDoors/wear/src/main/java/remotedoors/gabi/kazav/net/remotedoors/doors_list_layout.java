package remotedoors.gabi.kazav.net.remotedoors;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gabik on 5/16/16.
 * Implement wear list view
 */
public class doors_list_layout extends LinearLayout implements WearableListView.OnCenterProximityListener {

    private ImageView door_bg;
    private TextView door_name;

    public doors_list_layout(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        door_bg = (ImageView) findViewById(R.id.houseIcon);
        door_name = (TextView) findViewById(R.id.door_name);
    }

    @Override
    public void onCenterPosition(boolean b) {
        door_bg.setAlpha(1f);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        door_bg.setAlpha(0.5f);
    }
}
