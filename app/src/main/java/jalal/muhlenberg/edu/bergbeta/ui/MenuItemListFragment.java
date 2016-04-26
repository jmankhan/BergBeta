package jalal.muhlenberg.edu.bergbeta.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class MenuItemListFragment extends Fragment {

    private static final String DAYMEAL = "day-meal";
    private int daymeal = -1;
    private ArrayList<MenuItem> menuItems;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MenuItemListFragment() {
    }

    public static MenuItemListFragment newInstance(int daymeal) {
        MenuItemListFragment fragment = new MenuItemListFragment();
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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.menuItemRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(new MenuItemAdapter((MenuActivity) getActivity(), menuItems));
        recyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    private ArrayList<MenuItem> getCurrentMeal(ArrayList<MenuItem> menuItems) {
        int meal = daymeal % 3;
        int day = daymeal / 3;

        String[] days = getActivity().getResources().getStringArray(R.array.days);
        String currentMeal;
        if(meal == 0)
            currentMeal = "Breakfast";
        else if(meal == 1)
            currentMeal = "Lunch";
        else
            currentMeal = "Dinner";

        ArrayList<MenuItem> currentMenuItems = new ArrayList<>();
        for(MenuItem m : menuItems) {
            Log.d("ayy", "station: " + m.getStation());
            if(m.getDay() == day && m.getMeal().equalsIgnoreCase(currentMeal)
                    && m.getStation().contains("Magellan")
                    || m.getStation().equalsIgnoreCase("Chef"))
                currentMenuItems.add(m);
        }

        return currentMenuItems;
    }
}
