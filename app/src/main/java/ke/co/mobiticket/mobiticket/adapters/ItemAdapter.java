package ke.co.mobiticket.mobiticket.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.VehicleActivity;
import ke.co.mobiticket.mobiticket.pojos.Payment;
import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    List<Ticket> items;
    private OnItemClickListener mOnItemClickListener;
    Double fare =0.00;
    private Context ctx;
    public ItemAdapter(Context ctx, List<Ticket> items) {
        this.items = items;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_list_item, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Ticket obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, final int position) {
        final Ticket ticket = items.get(position);
        holder.bind(ticket);

        holder.llTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.e("Omaria", new Gson().toJson(ticket));
                if(ctx instanceof VehicleActivity){
                    ((VehicleActivity)ctx).showTicketDialog(ticket);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName,tvPaymentMethod, tvTravelTime, tvPrice, tvFromTo,tvStatus;
        private LinearLayout llTicket;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llTicket = itemView.findViewById(R.id.llTicket);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            itemName = itemView.findViewById(R.id.list_item_text_view);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvFromTo = itemView.findViewById(R.id.tvFromTo);
            tvTravelTime = itemView.findViewById(R.id.tvTravelTime);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
        }
        public void bind(Ticket item) {
            Gson gson=new Gson();

            Log.e("item", gson.toJson(item));

        try{
            if (item.getPayment().size() > 0) {
                for (Payment payment : item.getPayment()) {
                        if (payment.getId()!=null){
                            tvPaymentMethod.setText(payment.getChannel_name());
                        }


                }
            }
        }catch (Exception e) {

        }


            tvStatus.setText(item.getStatus());
            tvTravelTime.setText(item.getTravel_time());

            itemName.setText(item.getReference_number());
            fare=Double.valueOf(item.getTotal_fare());
            tvPrice.setText("KES "+String.format("%.2f", fare));
            tvFromTo.setText(item.getPickup_point()+" - "+item.getDropoff_point());
        }
    }
}
