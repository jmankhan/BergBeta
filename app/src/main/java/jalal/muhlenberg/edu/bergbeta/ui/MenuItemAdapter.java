package jalal.muhlenberg.edu.bergbeta.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jalal.muhlenberg.edu.bergbeta.R;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/20/2016.
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MIViewHolder> {
    private ArrayList<MenuItem> items;

    public MenuItemAdapter(ArrayList<MenuItem> items) {
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
        holder.name.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MIViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        MenuItem item;

        public MIViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        public void bindMenuItem(MenuItem item) {
            this.item = item;
            name.setText(item.getName());
        }
    }
}
