package ke.co.mobiticket.mobiticket.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Stop;

public class LazyAdapterBusStops extends BaseAdapter {
    private Activity activity;

    private LayoutInflater inflater = null;
    private int lastPosition = -1;
    private List<Stop> listOfStops;

    public LazyAdapterBusStops(Activity a, List<Stop> str) {
        activity = a;
        listOfStops = str;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listOfStops.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.single_spinner_item_view, null);
        }
        try {

            TextView tv_id = (TextView) vi.findViewById(R.id.tvId);

            if (listOfStops.size() < 0) {
                tv_id.setText("Sorry, No stops to show.");
            } else {
                listOfStops.size();

                tv_id.setText(Html.fromHtml(listOfStops.get(position).getName()));
            }
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return vi;
    }

}

