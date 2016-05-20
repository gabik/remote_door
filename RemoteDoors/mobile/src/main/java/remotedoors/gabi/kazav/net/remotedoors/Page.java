package remotedoors.gabi.kazav.net.remotedoors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

public class Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
    }

//    public void go(View view) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("caption", "T1");
//        map.put("name", "T2");
//        map.put("secret", "T3");
//        upDoor.connect_to_data(this, map, 1);
//
//    }
}
