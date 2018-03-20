package geogram.radio.myownradio.ui.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;


import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import geogram.radio.myownradio.MyApplication;
import geogram.radio.myownradio.R;
import geogram.radio.myownradio.adapters.RadioItemsAdapters;
import geogram.radio.myownradio.models.StationModel;
import geogram.radio.myownradio.mvp.presenters.PlayerFragmentPresenter;
import geogram.radio.myownradio.mvp.view.PlayerItemView;


/**
 * Created by geogr on 05.03.2018.
 */

public class PlayerFragment extends MvpAppCompatFragment implements PlayerItemView.View, RadioItemsAdapters.onListClickedRowListner {


    @InjectPresenter
    PlayerFragmentPresenter presenter;


    RadioItemsAdapters adapter;

    List<StationModel> chanelsList = new ArrayList<>();

    @BindView(R.id.radioList)
    RecyclerView radioList;

    @BindView(R.id.radioImage)
    ImageView imageView;

    private ProgressDialog mProgressDialog;
    private ActionMode actionMode;
    private int currentPosition;
    private Boolean mPause;
    private Boolean showProgressbar;
    private final String siPosition="position";
    private final String siPause="pause";
    private final String siShowProgress="showprogress";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(siPosition, currentPosition);
        outState.putBoolean(siPause, mPause);
        outState.putBoolean(siShowProgress, showProgressbar);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setRetainInstance(true);

        if (savedInstanceState == null) {
           init();
            currentPosition = -1;
            mPause = false;
            showProgressbar = false;
        } else {
            init();
            mPause = savedInstanceState.getBoolean(siPause);
            currentPosition = savedInstanceState.getInt(siPosition);
            showProgressbar = savedInstanceState.getBoolean(siShowProgress);
            if (currentPosition != -1)
                setMainImgResource(chanelsList.get(currentPosition));
            overFlip();
            if (showProgressbar)
                loadStart();
            savedInstanceState.clear();
        }
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (actionMode != null) return;
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
                adapter.toggleSelection(radioList.getChildLayoutPosition(radioList.findChildViewUnder(e.getX(), e.getY())));
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (actionMode != null)
                    adapter.toggleSelection(radioList.getChildLayoutPosition(radioList.findChildViewUnder(e.getX(), e.getY())));
                return super.onSingleTapConfirmed(e);
            }
        });
        radioList.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });


    }

   private void init(){
        adapter = new RadioItemsAdapters(this);
        radioList.setAdapter(adapter);
        createRadioItemList();
        adapter.addAll(chanelsList);
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void createRadioItemList() {
        chanelsList.clear();
        chanelsList.addAll(presenter.getAllItemsFromDb());
        adapter.addAll(chanelsList);

    }


    @Override
    public void setMainImgResource(StationModel model) {
        if (!(String.valueOf(model.getChanelImageBitmap()).equals(""))) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(model.getChanelImageBitmap()));
        } else {
            imageView.setImageResource(model.getChanelImage());
        }

    }

    @Override
    public void loadStart() {
        showProgressbar = true;
        this.mProgressDialog.setMessage(getString(R.string.loading_data));
        this.mProgressDialog.show();
    }

    @Override
    public void loadError(String error) {
        makeToast(error);
        currentPosition = -1;
        createRadioItemList();
    }

    @Override
    public void loadEnd() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            showProgressbar = false;
        }
    }

    @Override
    public void buttonChange(Boolean play, Boolean stop) {
        if (stop) {
            mPause = true;
            createRadioItemList();
            currentPosition = -1;

        } else {
            if (play) {
                mPause = false;
                adapter.setPause(currentPosition);
            } else {
                mPause = true;
                adapter.setPlay(currentPosition);

            }
        }

    }

    @Override
    public void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onListSelected(int mPosition) {

        if (presenter != null) {
            if (currentPosition == mPosition) {
                if (mPause) {
                    presenter.play();
                    adapter.setPause(mPosition);
                    mPause = false;
                } else {
                    presenter.pause();
                    adapter.setPlay(mPosition);
                    mPause = true;
                }
            } else {
                currentPosition = mPosition;
                presenter.load(chanelsList.get(mPosition));
                makeToast(getString(R.string.startplay) + chanelsList.get(mPosition).getChanelName());
                createRadioItemList();
                adapter.setPause(mPosition);
                setMainImgResource(chanelsList.get(mPosition));
                mPause = false;
            }
        }
    }

    private void overFlip() {
        if (currentPosition != -1) {
                setMainImgResource(chanelsList.get(currentPosition));
            if (mPause) {
                adapter.setPlay(currentPosition);
            } else {
                adapter.setPause(currentPosition);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        createRadioItemList();
        overFlip();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.actionmode_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.confirm_remove)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    for (int i = adapter.getSelectedItems().size() - 1; i >= 0; i--)
                                        if (presenter.deleteItemFromDB(adapter.remove(adapter.getSelectedItems().get(i)))) {
                                            Toast.makeText(getActivity(), getString(R.string.chanel_removed), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), getString(R.string.error_chanel_removed), Toast.LENGTH_LONG).show();
                                        }
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    actionMode.finish();
                                }
                            })
                            .show();
                    return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSeclections();

        }
    };
}
