package net.kazav.gabi.remotedoor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DoorConf extends Activity {

    int doorid = AppWidgetManager.INVALID_APPWIDGET_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(prefs.TAG, "doors widget starting conf");

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_door_conf);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            doorid = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (doorid == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    public void save(View v) {
        final Context context = DoorConf.this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.up_door_widget);
        appWidgetManager.updateAppWidget(doorid, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, doorid);
        setResult(RESULT_OK, resultValue);

        EditText caption = (EditText)findViewById(R.id.txtCaption);
        EditText name = (EditText)findViewById(R.id.txtDoor);
        EditText secret = (EditText)findViewById(R.id.txtSecret);
        prefs.save_widget(context, doorid, caption.getText().toString(), name.getText().toString(), secret.getText().toString());

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, upDoor.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {doorid});
        sendBroadcast(intent);

        finish();
    }

}
