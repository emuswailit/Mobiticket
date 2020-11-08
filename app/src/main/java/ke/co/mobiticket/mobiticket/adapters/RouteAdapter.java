package ke.co.mobiticket.mobiticket.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Route;
import ke.co.mobiticket.mobiticket.pojos.Stop;
import ke.co.mobiticket.mobiticket.utilities.Tools;
import ke.co.mobiticket.mobiticket.utilities.ViewAnimation;

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
        public ImageView image;
        public TextView tvRouteName;
        public TextView tvStops;
        public ImageButton bt_expand;
        public View lyt_expand;
        public View lyt_parent;
        public LinearLayout llDynamicContent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            llDynamicContent = (LinearLayout) v.findViewById(R.id.llDynamicContent);
            tvRouteName = (TextView) v.findViewById(R.id.tvRouteName);
            tvStops = (TextView) v.findViewById(R.id.tvStops);
            bt_expand = (ImageButton) v.findViewById(R.id.bt_expand);
            lyt_expand = (View) v.findViewById(R.id.lyt_expand);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_expand, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final Route p = items.get(position);
            view.tvRouteName.setText(p.getName());
            view.tvStops.setText("Listed stops on this route: "+String.valueOf(p.getStop().size()));




            try {
                Location originStop = new Location("");
                Location destinationStop = new Location("");

                for (int i=0;i<p.getStop().size();i++){
                    originStop.setLatitude(Double.valueOf(p.getStop().get(0).getLat()));
                    originStop.setLongitude(Double.valueOf(p.getStop().get(0).getLng()));

                    destinationStop.setLatitude(Double.valueOf(p.getStop().get(i).getLat()));
                    destinationStop.setLongitude(Double.valueOf(p.getStop().get(i).getLng()));


                    View view1 = LayoutInflater.from(ctx).inflate(R.layout.item_bus_stop_layout, view.llDynamicContent, false);
                    final TextView tvBusStopName = view1.findViewById(R.id.tvBusStopName);
                    final TextView tvBusStopDetail = view1.findViewById(R.id.tvBusStopDetail);
                    final TextView tvBusStopDistance = view1.findViewById(R.id.tvBusStopDistance);
                    tvBusStopName.setText(p.getStop().get(i).getName());
//                    tvBusStopDetail.setText("Nairobi County");
                    float distanceTo = originStop.distanceTo(destinationStop);
                    tvBusStopDistance.setText(String.format("%.2f",distanceTo/1000) + " km");
                    view.llDynamicContent.addView(view1);
                }
//                for (Stop stop : p.getStop()) {
//
//                    View view1 = LayoutInflater.from(ctx).inflate(R.layout.item_bus_stop_layout, view.llDynamicContent, false);
//                    final TextView tvBusStopName = view1.findViewById(R.id.tvBusStopName);
//                    final TextView tvBusStopDetail = view1.findViewById(R.id.tvBusStopDetail);
//                    final TextView tvBusStopDistance = view1.findViewById(R.id.tvBusStopDistance);
//                    tvBusStopName.setText(stop.getName());
//                    tvBusStopDetail.setText("Nairobi County");
//                    tvBusStopDistance.setText(String.valueOf(distance = distance + 2) + " km");
//                    view.llDynamicContent.addView(view1);
//                }
            }catch (Exception e){
                Log.e("stops",e.toString());
            }
            Tools.displayImageOriginal(ctx, view.image, R.drawable.route);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });

            view.bt_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean show = toggleLayoutExpand(!p.expanded, v, view.lyt_expand);
                    items.get(position).expanded = show;
                }
            });


            // void recycling view
            if(p.expanded){
                view.lyt_expand.setVisibility(View.VISIBLE);
            } else {
                view.lyt_expand.setVisibility(View.GONE);
            }
            Tools.toggleArrow(p.expanded, view.bt_expand, false);

        }
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}