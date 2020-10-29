package ke.co.mobiticket.mobiticket.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Route;


public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Route> items = new ArrayList<>();


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Route obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public RouteAdapter(Context context, List<Route> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRouteName, tvStops, tvCurrentFare, tvRoutes;
        public LinearLayout llDynamicContent;
        public MaterialCardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            tvRouteName = v.findViewById(R.id.tvRouteName);
            tvStops = v.findViewById(R.id.tvStops);
            tvCurrentFare = v.findViewById(R.id.tvCurrentFare);
            cardView = v.findViewById(R.id.card);
            llDynamicContent = v.findViewById(R.id.llDynamicContent);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String str = "";
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final Route p = items.get(position);
            view.tvRouteName.setText(p.getName());
            int distance=3;
            for (String stop : p.getStop()) {
                str += stop + " >>> ";
                View view1 = LayoutInflater.from(ctx).inflate(R.layout.item_bus_stop_layout, view.llDynamicContent, false);
                final TextView tvBusStopName = view1.findViewById(R.id.tvBusStopName);
                final TextView tvBusStopDetail = view1.findViewById(R.id.tvBusStopDetail);
                final TextView tvBusStopDistance = view1.findViewById(R.id.tvBusStopDistance);
                tvBusStopName.setText(stop);
                tvBusStopDetail.setText("Nairobi County");
                tvBusStopDistance.setText(String.valueOf(distance=distance+2)+ " km");
                view.llDynamicContent.addView(view1);
            }
//            view.tvStops.setText(str);
//            view.tvCurrentFare.setText("KES "+p.getCurrent_fare());

//            Tools.displayImageOriginal(ctx, view.image, p.image);
            view.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });


        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}
