package geogram.radio.myownradio.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import geogram.radio.myownradio.R;
import geogram.radio.myownradio.models.RealmStationModel;
import geogram.radio.myownradio.models.SpinnerItem;
import geogram.radio.myownradio.mvp.view.AddActivity;
import io.realm.Realm;

/**
 * Created by geogr on 16.03.2018.
 */
@InjectViewState
public class AddNewChanelPresenter extends MvpPresenter<AddActivity> {
    public SpinnerItem[] generateSpinnerItems(){
        SpinnerItem[] spinnerItems={new SpinnerItem(R.drawable.ic_default_icon, "Set chanel image"),
                new SpinnerItem(R.drawable.ic_bycicle_driver, "Bycicle"),
                new SpinnerItem(R.drawable.ic_child_face, "Baby"),
                new SpinnerItem(R.drawable.ic_android_icon, "Angry Android"),
                new SpinnerItem(R.drawable.ic_hearing, "Good Hearing"),
                new SpinnerItem(R.drawable.ic_item_image, "Super car"),
                new SpinnerItem(R.drawable.ic_child_chanel, "Child chanel"),
                new SpinnerItem(R.drawable.ic_metro, "Metro train")
        };
        return spinnerItems;
    }

    public void addNewChanel(RealmStationModel model) {
        Realm realm=Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm.insert(model));
    }
}
