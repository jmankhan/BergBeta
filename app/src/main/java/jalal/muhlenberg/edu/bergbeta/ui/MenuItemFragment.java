package jalal.muhlenberg.edu.bergbeta.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jalal.muhlenberg.edu.bergbeta.MenuActivity;
import jalal.muhlenberg.edu.bergbeta.R;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class MenuItemFragment extends Fragment {

    private static final String DAYMEAL = "day-meal";
    private int daymeal = -1;
    private ArrayList<MenuItem> menuItems;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MenuItemFragment() {
    }

    public static MenuItemFragment newInstance(int daymeal) {
        MenuItemFragment fragment = new MenuItemFragment();
        Bundle args = new Bundle();
        args.putInt(DAYMEAL, daymeal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            daymeal = getArguments().getInt(DAYMEAL);
            menuItems = getCurrentMeal(((MenuActivity) getActivity()).getMenuItems());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menuitem_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setAdapter(new MenuItemAdapter(menuItems));
        return view;
    }

    private ArrayList<MenuItem> getCurrentMeal(ArrayList<MenuItem> menuItems) {
        ArrayList<MenuItem> items = new ArrayList<>();
        for(MenuItem item : menuItems) {
            if(item.getDaymeal() == daymeal)
                items.add(item);
        }
        return items;
    }
}
