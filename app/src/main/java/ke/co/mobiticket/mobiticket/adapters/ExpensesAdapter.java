package ke.co.mobiticket.mobiticket.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.utilities.Tools;

public class ExpensesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Expense> items = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    Double amount=0.00;
    private Context ctx;



    public interface OnItemClickListener {
        void onItemClick(View view, Expense obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ExpensesAdapter(Context context, List<Expense> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tvExpenseDescription,tvChargeChannel,tvAmount, tvStatus;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            tvAmount = (TextView) v.findViewById(R.id.tvAmount);
            tvStatus = (TextView) v.findViewById(R.id.tvStatus);
            tvExpenseDescription = (TextView) v.findViewById(R.id.tvExpenseDescription);
            tvChargeChannel = (TextView) v.findViewById(R.id.tvChargeChannel);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_expense, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Expense p = items.get(position);
            view.tvExpenseDescription.setText(p.getDescription());
            view.tvStatus.setText(p.getStatus());
            view.tvChargeChannel.setText(p.getReference_number());
            try {
                amount= Double.valueOf(p.getAmount());
                view.tvAmount.setText("KES "+ String.format("%.2f",amount));
            }catch (Exception e){

            }

            Tools.displayImageRound(ctx, view.image, ctx.getDrawable(R.drawable.coin_etc));
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
        return items.size();
    }

}
