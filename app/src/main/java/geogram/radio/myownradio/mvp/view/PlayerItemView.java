package geogram.radio.myownradio.mvp.view;

import android.support.v7.view.menu.MenuView;

import com.arellomobile.mvp.MvpView;

import geogram.radio.myownradio.models.StationModel;

/**
 * Created by geogr on 06.03.2018.
 */

public interface PlayerItemView extends MvpView {
    public interface View extends MvpView {
        void createRadioItemList();
        void setMainImgResource(StationModel model);
        void loadStart();
        void loadEnd();
        void loadError(String error);
        void buttonChange(Boolean play, Boolean stop);
        void makeToast(String text);
    }
    public interface Action{
        void play();
        void pause();
        void load(StationModel model);
        void loadDialogEnd();
        void loadDialogStart();
    }
}
