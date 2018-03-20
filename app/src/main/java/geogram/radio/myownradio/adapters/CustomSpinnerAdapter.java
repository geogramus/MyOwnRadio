package geogram.radio.myownradio.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.inject.Inject;

import butterknife.ButterKnife;
import geogram.radio.myownradio.R;
import geogram.radio.myownradio.models.SpinnerItem;

/**
 * Created by geogr on 14.03.2018.
 */

public class CustomSpinnerAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<SpinnerItem> list = new ArrayList<>();

    Context context;

    public CustomSpinnerAdapter(Context context, List<SpinnerItem> list) {

        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void recreatList(List<SpinnerItem> createList) {
        list.clear();
        list.addAll(createList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getCustomView(i, view, viewGroup);
    }


    private View getCustomView(int position, View convertView,
                               ViewGroup parent) {

         convertView = inflater.inflate(R.layout.spinner_row, parent, false);
        TextView label = (TextView) convertView.findViewById(R.id.imageName);
        label.setText(list.get(position).getText());

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        icon.setImageResource(list.get(position).getImage());

        return convertView;
    }
}
