package ke.co.mobiticket.mobiticket.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.PaymentMethod;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.utilities.AppController;

public class TicketsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<Ticket> items = new ArrayList<>();
    private List<Ticket> itemsFiltered = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString().toLowerCase();

                if (charString.isEmpty()) {

                    itemsFiltered = items;
                } else {

                    ArrayList<Ticket> filteredList = new ArrayList<>();

                    for (Ticket ticket : items) {

                        if (ticket.getMsisdn().toLowerCase().contains(charString)|| ticket.getFirst_name().toLowerCase().contains(charString) || ticket.getLast_name().toLowerCase().contains(charString) || ticket.getMiddle_name().toLowerCase().contains(charString)) {

                            filteredList.add(ticket);
                        }
                    }

                    itemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<Ticket>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Ticket obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public TicketsAdapter(Context context, List<Ticket> items) {
        this.items = items;
        this.itemsFiltered = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPhone,tvAmount,tvPaymentStatus,tvTravelDate,tvTravelTime;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);

            tvTravelDate = (TextView) v.findViewById(R.id.tvTravelDate);
            tvTravelTime = (TextView) v.findViewById(R.id.tvTravelTime);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvAmount = (TextView) v.findViewById(R.id.tvAmount);
            tvPaymentStatus = (TextView) v.findViewById(R.id.tvPaymentStatus);
            tvPhone = (TextView) v.findViewById(R.id.tvPhone);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_layout, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Ticket p = items.get(position);
            view.tvName.setText(AppController.getInstance().camelCase(p.getFirst_name()) + " " + AppController.getInstance().camelCase(p.getMiddle_name()) + " " + AppController.getInstance().camelCase(p.getLast_name()));
            view.tvPhone.setText(p.getMsisdn());
            if (p.getTotal_fare().equals("")){
                view.tvAmount.setText(String.format("%.2f",0.00));
            }else {
                view.tvAmount.setText(String.format("%.2f",Double.valueOf(p.getTotal_fare())));
            }

            view.tvPaymentStatus.setText(p.getStatus());
            if (p.getStatus().equals("Pending")){
                view.tvPaymentStatus.setTextColor(Color.RED);
            }else  if (p.getStatus().equals("Confirmed")){
                view.tvPaymentStatus.setTextColor(Color.GREEN);
            }
            view.tvTravelDate.setText(p.getTravel_date());
            view.tvTravelTime.setText(p.getTravel_time());
//            Tools.displayImageRound(ctx, view.image, p.image);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
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
        return itemsFiltered.size();
    }

}