package jalal.muhlenberg.edu.bergbeta.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jalal.muhlenberg.edu.bergbeta.MenuActivity;
import jalal.muhlenberg.edu.bergbeta.R;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/20/2016.
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MIViewHolder> {
    private ArrayList<MenuItem> items;
    private MenuItemClickCallback callback;

    public MenuItemAdapter(MenuItemClickCallback callback, ArrayList<MenuItem> items) {
        this.callback = callback;
        this.items = items;
    }

    @Override
    public MIViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_menuitem,
                parent, false);

        return new MIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MIViewHolder holder, int position) {
        holder.bindMenuItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MIViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView name;
        String id;

        public MIViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            CardView card = (CardView) itemView.findViewById(R.id.menuitem_card);
            card.setOnClickListener(this);
        }

        public void bindMenuItem(MenuItem item) {
            name.setText(item.getName());
            id = item.getId();
        }

        @Override
        public void onClick(View v) {
            Log.d("ayy", "clicked item " + name.getText());
            callback.onClick(id);
        }
    }
}
