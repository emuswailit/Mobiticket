package ke.co.mobiticket.mobiticket.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;
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
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mCtx, myList.getRegistration_number(), Toast.LENGTH_SHORT).show();

                ((MyVehiclesActivity)mCtx).customMethod(myList);

//                //creating a popup menu
//                PopupMenu popup = new PopupMenu(mCtx, view);
//                //inflating menu from xml resource
//                popup.inflate(R.menu.options_menu);
//                //adding click listener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.mnActivitySummary:
//                                //handle menu1 click
//                                ((MyVehiclesActivity)mCtx).customMethod(myList);
//                                break;
//                            case R.id.itemCharges:
//                                //handle menu2 click
//                                break;
//                            case R.id.itemExpenses:
//                                //handle menu3 click
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                //displaying the popup
//                popup.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvVehicleRegistration;
        public LinearLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);

            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
            tvVehicleRegistration = (TextView) itemView.findViewById(R.id.tvVehicleRegistration);

        }
    }



}