package jalal.muhlenberg.edu.bergbeta.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jalal.muhlenberg.edu.bergbeta.MenuActivity;
import jalal.muhlenberg.edu.bergbeta.R;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * Created by Jalal on 4/20/2016.
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MIViewHolder> {
    private ArrayList<MenuItem> items;
    private MenuActivity activity;

    public MenuItemAdapter(MenuActivity activity, ArrayList<MenuItem> items) {
        this.activity = activity;
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
        ImageView img;
        String id;

        public MIViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.menuitem_name);
            img = (ImageView) itemView.findViewById(R.id.menuitem_image);

            Picasso.with(activity)
                    .load(R.drawable.pizza)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(img);

            CardView card = (CardView) itemView.findViewById(R.id.menuitem_card);
            card.setOnClickListener(this);
        }

        public void bindMenuItem(MenuItem item) {
            name.setText(item.getName());
            id = item.getId();
        }

        @Override
        public void onClick(View v) {
            activity.onClick(id);
        }
    }
}
