package geogram.radio.myownradio.ui.activityes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.FilePickerProvider;
import geogram.radio.myownradio.R;
import geogram.radio.myownradio.adapters.CustomSpinnerAdapter;
import geogram.radio.myownradio.models.RealmStationModel;
import geogram.radio.myownradio.models.SpinnerItem;
import geogram.radio.myownradio.mvp.presenters.AddNewChanelPresenter;
import geogram.radio.myownradio.mvp.view.AddActivity;

public class AddNewChanelActivity extends MvpAppCompatActivity implements AddActivity {

    @InjectPresenter
    AddNewChanelPresenter presenter;

    @BindView(R.id.chanelSpinner)
    Spinner spinner;
    @BindView(R.id.loadImage)
    ImageView loadImage;
    @BindView(R.id.chanelName)
    TextView chanelName;
    @BindView(R.id.chanelUrl)
    TextView chanelURL;
    @BindView(R.id.addButton)
    Button addButton;
    @BindView(R.id.photo_set)
    TextView photo_set;
    RealmStationModel model;
    List<SpinnerItem> list = new ArrayList<>();
    CustomSpinnerAdapter customSpinnerAdapter;
    ArrayList<String> imagePath = new ArrayList<>();
    List<String> photoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_chanel);
        ButterKnife.bind(this);
        list = Arrays.asList(presenter.generateSpinnerItems());
        customSpinnerAdapter = new CustomSpinnerAdapter(this, list);
        spinner.setAdapter(customSpinnerAdapter);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(chanelName.getText()).equals("") || String.valueOf(chanelURL.getText()).equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.chell_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    RealmStationModel model = new RealmStationModel();
                    if (photoPaths !=null) {
                        model.setChanelImageBitmap(photoPaths.get(0));
                    }
                    model.setChanelImage(list.get(spinner.getSelectedItemPosition()).getImage());
                    model.setChanelName(String.valueOf(chanelName.getText()));
                    model.setStreamChanelUrl(String.valueOf(chanelURL.getText()));
                    model.setMainButtonImage(R.drawable.ic_action_name);
                    presenter.addNewChanel(model);
                    finish();
                }

            }
        });
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddNewChanelActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddNewChanelActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(AddNewChanelActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                123);
                    }
                } else {
                    startCheck();
                }
            }

        });

    }

    public void startCheck() {

        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(imagePath)
                .showFolderView(false)
                .setActivityTheme(R.style.AppTheme)
                .pickPhoto(AddNewChanelActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            photoPaths = new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            photo_set.setText(R.string.file_selected);
        }
    }
}
