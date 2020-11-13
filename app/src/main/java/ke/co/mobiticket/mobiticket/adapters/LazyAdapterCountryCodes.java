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
import ke.co.mobiticket.mobiticket.pojos.Country;
import ke.co.mobiticket.mobiticket.pojos.Stop;

public class LazyAdapterCountryCodes extends BaseAdapter {
    private Activity activity;

    private LayoutInflater inflater = null;
    private int lastPosition = -1;
    private List<Country> countryList;

    public LazyAdapterCountryCodes(Activity a, List<Country> str) {
        activity = a;
        countryList = str;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countryList.size();
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

            if (countryList.size() < 0) {
                tv_id.setText("Sorry, No stops to show.");
            } else {
                countryList.size();

                tv_id.setText(Html.fromHtml(countryList.get(position).getCode()));
            }
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return vi;
    }

}

