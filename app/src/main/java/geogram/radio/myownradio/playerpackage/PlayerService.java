package geogram.radio.myownradio.playerpackage;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;

import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.io.IOException;

import javax.inject.Inject;

import geogram.radio.myownradio.MyApplication;
import geogram.radio.myownradio.R;
import geogram.radio.myownradio.models.StationModel;
import geogram.radio.myownradio.ui.activityes.MainActivity;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlayerService extends Service {

    @Inject
    Context context;

    private MediaPlayer mediaPlayer;
    private Boolean error = false;
    private String streamURL;
    private static StationModel stationModel1;
    AudioManager audioManager;
    MediaSessionCompat mediaSession;
    public static int NOTIFICATION_ID = 111;
    private Boolean focusLoose;
    private static eNdloadListener listner;

    public interface eNdloadListener {
        void onLoadEndSelected();
        void onEror(String text);
        void buttonResourceChange(Boolean play, Boolean stop);
    }


    final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

    final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
            .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_STOP
                            | PlaybackStateCompat.ACTION_PAUSE
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            );


    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerServiceBinder();
    }

    public class PlayerServiceBinder extends Binder {
        public MediaSessionCompat.Token getMediaSessionToken() {
            return mediaSession.getSessionToken();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.getsApplicationComponent().inject(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        focusLoose=false;
        mediaSession = new MediaSessionCompat(this, "PlayerService");
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | Notification.FLAG_ONGOING_EVENT);
        mediaSession.setCallback(mediaSessionCallback);

        Intent activityIntent = new Intent(context, MainActivity.class);
        mediaSession.setSessionActivity(
                PendingIntent.getActivity(context, 0, activityIntent, 0));

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Intent mediaButtonIntent = new Intent(
                Intent.ACTION_MEDIA_BUTTON, null, context, MediaButtonReceiver.class);
        mediaSession.setMediaButtonReceiver(
                PendingIntent.getBroadcast(context, 0, mediaButtonIntent, 0));
    }

    public void onLoadError() {
        listner.onLoadEndSelected();
        listner.onEror(getString(R.string.loading_error));

    }


    MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPrepare() {
            super.onPrepare();

            mediaPlayer.stop();
            mediaPlayer.release();
            streamURL = stationModel1.getStreamChanelUrl();
            mediaPlayer = new MediaPlayer();
            Completable.fromAction(() -> prepareChanel(streamURL))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onPlay);
            if (!error) {
                MediaMetadataCompat metadata = metadataBuilder
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
                                BitmapFactory.decodeResource(getResources(), stationModel1.getChanelImage()))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, stationModel1.getChanelName())
                        .build();
                mediaSession.setMetadata(metadata);
                mediaSession.setActive(true);
                mediaSession.setPlaybackState(
                        stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());

            }

        }

        private void prepareChanel(String streamURL) {
            try {
                mediaPlayer.setDataSource(streamURL);
                mediaPlayer.prepare();
                error = false;
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        }

        @Override
        public void onPlay() {
            super.onPlay();
            if (error) {
                onLoadError();
            } else {
                focusLoose=false;
                listner.buttonResourceChange(true, false);
                startService(new Intent(getApplicationContext(), PlayerService.class));
                listner.onLoadEndSelected();
                int audioFocusResult = audioManager.requestAudioFocus(
                        audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    return;
                mediaSession.setActive(true);

                mediaPlayer.start();
                mediaSession.setPlaybackState(
                        stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
                refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PLAYING);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            mediaPlayer.pause();
            focusLoose=true;
            listner.buttonResourceChange(false, false);
            mediaSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PAUSED);
        }

        @Override
        public void onStop() {
            super.onStop();
            stopSelf();
            listner.buttonResourceChange(false, true);
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            mediaPlayer.stop();
            mediaSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_STOPPED);
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }


    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            if (!focusLoose) {
                                mediaSessionCallback.onPlay();
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            break;
                        default:
                            focusLoose = true;
                            mediaSessionCallback.onPause();
                            break;
                    }
                }
            };


    void refreshNotificationAndForegroundStatus(int playbackState) {
        switch (playbackState) {
            case PlaybackStateCompat.STATE_PLAYING: {
                startForeground(NOTIFICATION_ID, getNotification(playbackState));
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                // На паузе мы перестаем быть foreground, однако оставляем уведомление,
                // чтобы пользователь мог play нажать
                NotificationManagerCompat.from(PlayerService.this)
                        .notify(NOTIFICATION_ID, getNotification(playbackState));
                stopForeground(false);
                break;
            }
            default: {
                // Все, можно прятать уведомление
                stopForeground(true);
                break;
            }
        }
    }

    Notification getNotification(int playbackState) {
        // MediaStyleHelper заполняет уведомление метаданными трека.
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mediaSession);
        builder.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_previous, getString(R.string.string_previous),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            builder.addAction(
                    new NotificationCompat.Action(
                            android.R.drawable.ic_media_pause, getString(R.string.string_pause),
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        else
            builder.addAction(
                    new NotificationCompat.Action(
                            android.R.drawable.ic_media_play, getString(R.string.string_play),
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.addAction(
                new NotificationCompat.Action(android.R.drawable.ic_media_next, getString(R.string.strin_next),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder.setStyle(new NotificationCompat.MediaStyle()
                // В компактном варианте показывать Action с данным порядковым номером.
                // В нашем случае это play/pause.
                .setShowActionsInCompactView(1)
                // Отображать крестик в углу уведомления для его закрытия.
                // На API >= 21 крестик не отображается, там просто смахиваем уведомление.
                .setShowCancelButton(true)
                // Указываем, что делать при нажатии на крестик или смахивании
                .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                this,
                                PlaybackStateCompat.ACTION_STOP))
                // Передаем токен. Это важно для Android Wear. Если токен не передать,
                // кнопка на Android Wear будет отображаться, но не будет ничего делать
                .setMediaSession(mediaSession.getSessionToken()));

        builder.setSmallIcon(stationModel1.getChanelImage());
        builder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Не отображать время создания уведомления. В нашем случае это не имеет смысла
        builder.setShowWhen(false);


        // Это важно. Без этой строчки уведомления не отображаются на Android Wear
        // и криво отображаются на самом телефоне.
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Не надо каждый раз вываливать уведомление на пользователя
        builder.setOnlyAlertOnce(true);

        return builder.build();
    }

    @Override
    public void onDestroy() {

    }


    public static void setStationModel(StationModel stationModel) {
        stationModel1 = stationModel;
    }

    public static void setListener(eNdloadListener mlistner) {
        listner = mlistner;
    }
}
