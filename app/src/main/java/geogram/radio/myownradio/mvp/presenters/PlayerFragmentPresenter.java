package geogram.radio.myownradio.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import geogram.radio.myownradio.MyApplication;
import geogram.radio.myownradio.playerpackage.PlayerService;
import geogram.radio.myownradio.models.RealmStationModel;
import geogram.radio.myownradio.models.StationModel;
import geogram.radio.myownradio.mvp.view.PlayerItemView;
import io.reactivex.Completable;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by geogr on 06.03.2018.
 */
@InjectViewState
public class PlayerFragmentPresenter extends MvpPresenter<PlayerItemView.View> implements PlayerItemView.Action, PlayerService.eNdloadListener {
    @Inject
    Context context;

    private PlayerService.PlayerServiceBinder playerServiceBinder;
    private MediaControllerCompat mediaController;

    public PlayerFragmentPresenter() {
        MyApplication.getsApplicationComponent().inject(this);
        context.bindService(new Intent(context, PlayerService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                playerServiceBinder = (PlayerService.PlayerServiceBinder) service;
                try {
                    mediaController = new MediaControllerCompat(
                            context, playerServiceBinder.getMediaSessionToken());
                    mediaController.registerCallback(
                            new MediaControllerCompat.Callback() {
                                @Override
                                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                                    if (state == null)
                                        return;
                                    boolean playing =
                                            state.getState() == PlaybackStateCompat.STATE_PLAYING;
                                }
                            }
                    );
                } catch (RemoteException e) {
                    mediaController = null;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerServiceBinder = null;
                mediaController = null;
            }
        }, BIND_AUTO_CREATE);

    }

    public List<StationModel> getAllItemsFromDb() {
        Realm realm=Realm.getDefaultInstance();
        RealmResults<RealmStationModel> removeItem = realm.where(RealmStationModel.class).findAll();
        List<RealmStationModel> realmList = realm.copyFromRealm(removeItem);
        List<StationModel> list = new ArrayList<>();
        for (int i = 0; i < realmList.size(); i++) {
            list.add(new StationModel(realmList.get(i).getStreamChanelUrl(), realmList.get(i).getChanelName(),
                    realmList.get(i).getChanelImageBitmap(),
                    realmList.get(i).getChanelImage(), realmList.get(i).getMainButtonImage()));
        }
        return list;

    }

    public void play() {
        if (mediaController != null)
            mediaController.getTransportControls().play();
    }

    public void pause() {
        if (mediaController != null)
            mediaController.getTransportControls().pause();
    }

    public void load(StationModel model) {
        Intent intent = new Intent(context, PlayerService.class);
        PlayerService.setStationModel(model);
        PlayerService.setListener(this);
        context.startService(intent);
        if (mediaController != null)
            Completable.fromAction(this::startload)
                    .subscribe(this::loadDialogStart);
    }

    private void startload() {
        mediaController.getTransportControls().prepare();
    }

    public void loadDialogEnd() {
        getViewState().loadEnd();
    }

    public void loadDialogStart() {
        getViewState().loadStart();
    }


    @Override
    public void onLoadEndSelected() {
        loadDialogEnd();
    }


    @Override
    public void onEror(String text) {
     errorToast(text);
    }

    @Override
    public void buttonResourceChange(Boolean play, Boolean stop) {
       getViewState().buttonChange(play, stop);
    }

    public void errorToast(String text){
        getViewState().loadError(text);
    }

    public boolean deleteItemFromDB(StationModel remove) {
        RealmStationModel model=new RealmStationModel();
        model.setMainButtonImage(remove.getMainButtonImage());
        model.setStreamChanelUrl(remove.getStreamChanelUrl());
        model.setChanelName(remove.getChanelName());
        model.setChanelImage(remove.getChanelImage());
        Realm realm=Realm.getDefaultInstance();
        RealmResults<RealmStationModel> removeItem = realm.where(RealmStationModel.class)
                .equalTo("streamChanelUrl", model.getStreamChanelUrl()).findAll();

        try {
            realm.executeTransaction(realm1 -> removeItem.deleteAllFromRealm());
            return true;
        }
        catch(Exception e){
            return false;
        }


    }
}
