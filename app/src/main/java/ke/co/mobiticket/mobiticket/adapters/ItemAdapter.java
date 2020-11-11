package ke.co.mobiticket.mobiticket.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Payment;
import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    List<Ticket> items;
    Double fare =0.00;
    public ItemAdapter(List<Ticket> items) {
        this.items = items;
    }
    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_list_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Ticket itemName = items.get(position);
        holder.bind(itemName);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName,tvPaymentMethod, tvTravelTime, tvPrice, tvFromTo,tvStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
