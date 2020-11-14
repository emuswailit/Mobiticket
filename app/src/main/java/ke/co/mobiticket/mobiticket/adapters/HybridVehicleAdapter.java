package ke.co.mobiticket.mobiticket.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.MyVehiclesActivity;
import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.HybridVehicle;
import ke.co.mobiticket.mobiticket.utilities.Tools;
public class HybridVehicleAdapter extends RecyclerView.Adapter<HybridVehicleAdapter.ViewHolder> {

    private List<HybridVehicle> list;
    private Context mCtx;


    public HybridVehicleAdapter(List<HybridVehicle> list, Context mCtx) {
        this.list = list;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hybrid_vehicle, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HybridVehicleAdapter.ViewHolder holder, int position) {
        final HybridVehicle myList = list.get(position);
        holder.tvVehicleRegistration.setText(myList.getRegistration_number());
        holder.tvVehicleOperator.setText(myList.getRoutes());

        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mCtx, view);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnActivitySummary:
                                //handle menu1 click
                                ((MyVehiclesActivity)mCtx).customMethod(myList);
                                break;
                            case R.id.menu2:
                                //handle menu2 click
                                break;
                            case R.id.menu3:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvVehicleRegistration;
        public TextView tvVehicleOperator;
        public TextView btnOptions;

        public ViewHolder(View itemView) {
            super(itemView);

            tvVehicleRegistration = (TextView) itemView.findViewById(R.id.tvVehicleRegistration);
            tvVehicleOperator = (TextView) itemView.findViewById(R.id.tvVehicleOperator);
            btnOptions = (Button) itemView.findViewById(R.id.btnOptions);
        }
    }



}