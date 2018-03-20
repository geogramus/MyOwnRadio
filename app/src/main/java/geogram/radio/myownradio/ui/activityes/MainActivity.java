package geogram.radio.myownradio.ui.activityes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import geogram.radio.myownradio.models.RealmStationModel;

import geogram.radio.myownradio.ui.fragments.PlayerFragment;
import geogram.radio.myownradio.R;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends MvpAppCompatActivity {

    private static final String MY_SETTINGS = "my_settings";
    private final String Visited = "hasvisited";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.maincContainer, new PlayerFragment())
                    .commitAllowingStateLoss();
        }
        SharedPreferences firstrun = getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        boolean hasVisited =firstrun.getBoolean(Visited, false);
        if (!hasVisited) {
            addItemsToDatabase();
            SharedPreferences.Editor e = firstrun.edit();
            e.putBoolean(Visited, true);
            e.apply(); // не забудьте подтвердить изменения
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.city_change:
                Intent intent = new Intent(this, AddNewChanelActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void addItemsToDatabase() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmStationModel> removeItem = realm.where(RealmStationModel.class).findAll();
        realm.executeTransaction(realm1 -> removeItem.deleteAllFromRealm());
        List<RealmStationModel> stationModels = new ArrayList<>();
        RealmStationModel station1 = new RealmStationModel();
        station1.setStreamChanelUrl("http://ep256.hostingradio.ru:8052/europaplus256.mp3");
        station1.setChanelName("Europa Plus");
        station1.setChanelImage(R.drawable.eurplus);
        station1.setChanelImageBitmap("");
        station1.setMainButtonImage(R.drawable.ic_action_name);
        RealmStationModel station2 = new RealmStationModel();
        station2.setStreamChanelUrl("http://cast.radiogroup.com.ua:8000/avtoradio");
        station2.setChanelName("Avto Radio");
        station2.setChanelImageBitmap("");
        station2.setChanelImage(R.drawable.avto);
        station2.setMainButtonImage(R.drawable.ic_action_name);
        RealmStationModel station3 = new RealmStationModel();
        station3.setStreamChanelUrl("http://cast.radiogroup.com.ua:8000/retro");
        station3.setChanelName("RetroFM");
        station3.setChanelImageBitmap("");
        station3.setChanelImage(R.drawable.retro_fm);
        station3.setMainButtonImage(R.drawable.ic_action_name);
        RealmStationModel station4 = new RealmStationModel();
        station4.setStreamChanelUrl("http://radio02-cn03.akadostream.ru:8112/nashe128.mp3");
        station4.setChanelName("Nashe Radio");
        station4.setChanelImageBitmap("");
        station4.setChanelImage(R.drawable.nasheradio);
        station4.setMainButtonImage(R.drawable.ic_action_name);
        RealmStationModel station5 = new RealmStationModel();
        station5.setStreamChanelUrl("http://radio02-cn03.akadostream.ru:8114/jumorfm128.mp3");
        station5.setChanelName("JumorFM");
        station5.setChanelImageBitmap("");
        station5.setChanelImage(R.drawable.humor);
        station5.setMainButtonImage(R.drawable.ic_action_name);



        stationModels.add(station1);
        stationModels.add(station2);
        stationModels.add(station3);
        stationModels.add(station4);
        stationModels.add(station5);
        for (RealmStationModel st: stationModels ) {
            realm.executeTransaction(realm1 -> realm.insert(st));
        }
    }

}
