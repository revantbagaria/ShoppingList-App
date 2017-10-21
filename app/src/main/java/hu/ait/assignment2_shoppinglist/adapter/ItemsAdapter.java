package hu.ait.assignment2_shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.ait.assignment2_shoppinglist.data.Item;
import hu.ait.assignment2_shoppinglist.MainActivity;
import hu.ait.assignment2_shoppinglist.R;
import io.realm.Realm;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvItem;
        public TextView tvEstim_Price;
        public CheckBox cbDone;
        public Button btnDelete;
        public Button btnViewEditDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvEstim_Price = (TextView) itemView.findViewById(R.id.tvEstim_Price);
            cbDone = (CheckBox) itemView.findViewById(R.id.cbDone);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            btnViewEditDetails = (Button) itemView.findViewById(R.id.btnViewEditDetails);
        }
    }

    private List<Item> itemsList;
    private Context context;
    private int lastPosition = -1;
    private Realm realm;

    public ItemsAdapter(List<Item> itemsList, Context context,
                        Realm realm) {
        this.itemsList = itemsList;
        this.context = context;
        this.realm = realm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tvItem.setText(itemsList.get(position).getItemName());
        viewHolder.tvEstim_Price.setText(itemsList.get(position).getEstim_price());
        viewHolder.cbDone.setChecked(itemsList.get(position).isDone());

        viewHolder.cbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                itemsList.get(viewHolder.getAdapterPosition()).setDone(viewHolder.cbDone.isChecked());
                realm.commitTransaction();
            }
        });

        viewHolder.ivIcon.setImageResource(
                itemsList.get(position).getItemType().getIconId());

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.btnViewEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).showEditItemActivity(
                        itemsList.get(viewHolder.getAdapterPosition()).getItemID(),
                        viewHolder.getAdapterPosition());
            }
        });

        setAnimation(viewHolder.itemView, position);
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void addItem(Item item) {
        itemsList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(int index, Item item) {
        itemsList.set(index, item);

        notifyItemChanged(index);

    }

    public void removeItem(int index) {
        ((MainActivity)context).deleteItem(itemsList.get(index));
        itemsList.remove(index);
        notifyItemRemoved(index);
    }

    public void removeAll(){
        while (!itemsList.isEmpty()){
            removeItem(0);
        }
    }

    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(itemsList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(itemsList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }

    public Item getItem(int i) {
        return itemsList.get(i);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
