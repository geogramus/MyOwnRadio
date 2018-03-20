package geogram.radio.myownradio.adapters;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import geogram.radio.myownradio.R;
import geogram.radio.myownradio.models.StationModel;

/**
 * Created by geogr on 06.03.2018.
 */

public class RadioItemsAdapters extends RecyclerView.Adapter<RadioItemsAdapters.ItemViewHolder> {

    private List<StationModel> station = new ArrayList<>();
    private Boolean loading = false;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    onListClickedRowListner listner;

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }
    public void clearSeclections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public StationModel remove(int pos) {
        final StationModel stationModel = station.remove(pos);
        notifyItemRemoved(pos);
        return stationModel;
    }
    public interface onListClickedRowListner {
        void onListSelected(int mPosition);
    }


    public RadioItemsAdapters(onListClickedRowListner listner) {
        this.listner = listner;
    }



    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_item, null));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        StationModel stationModel = station.get(position);

        if (stationModel != null) {
            holder.textTimer.setText(stationModel.getChanelName());
            if(!(String.valueOf(stationModel.getChanelImageBitmap()).equals(""))){holder.
                    itemImage.setImageBitmap(BitmapFactory.decodeFile(stationModel.getChanelImageBitmap()));}
            else {holder.itemImage.setImageResource(stationModel.getChanelImage());}
            holder.mainButton.setImageResource(stationModel.getMainButtonImage());
            holder.container.setActivated(selectedItems.get(position, false));
            holder.mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.onListSelected(position);
                }
            });
        } else {
            holder.textTimer.setText(R.string.loading_error);
        }
    }
    public void setPause(int mPosition){
        if(mPosition!=-1)
        station.get(mPosition).setMainButtonImage(R.drawable.ic_pause);
        notifyDataSetChanged();
    }
    public void setPlay(int mPosition){
        if(mPosition!=-1)
        station.get(mPosition).setMainButtonImage(R.drawable.ic_action_name);
        notifyDataSetChanged();
    }
    public void addAll(List<StationModel> list) {
        station.clear();
        station.addAll(list);
        notifyDataSetChanged();

    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    @Override
    public int getItemCount() {
        return station.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mainButton)
        ImageButton mainButton;
        @BindView(R.id.itemImage)
        ImageView itemImage;
        @BindView(R.id.textTimer)
        TextView textTimer;
        @BindView(R.id.redioItemContainer)
        RelativeLayout container;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
