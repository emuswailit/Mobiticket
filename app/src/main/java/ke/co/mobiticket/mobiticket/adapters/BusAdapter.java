package ke.co.mobiticket.mobiticket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Vehicle;


public class BusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Vehicle> items = new ArrayList<>();


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Vehicle obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public BusAdapter(Context context, List<Vehicle> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOperatorName, tvVehicleType,tvRegistrationNumber, tvCurrentFare, tvRoutes;
        public MaterialCardView cardView;
        public OriginalViewHolder(View v) {
            super(v);
            tvOperatorName =  v.findViewById(R.id.tvRouteName);
            tvVehicleType =  v.findViewById(R.id.tvVehicleType);
            tvRegistrationNumber =  v.findViewById(R.id.tvRegistrationNumber);
            tvCurrentFare =  v.findViewById(R.id.tvCurrentFare);
            cardView =  v.findViewById(R.id.card);

    }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_bus, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final Vehicle p = items.get(position);
            view.tvOperatorName.setText(p.getOperator_name());
            view.tvVehicleType.setText( p.getVehicle_type());
            view.tvRegistrationNumber.setText(p.getRegistration_number());
            view.tvCurrentFare.setText("KES "+String.format("%.2f", Double.valueOf(p.getCurrent_fare())));

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
