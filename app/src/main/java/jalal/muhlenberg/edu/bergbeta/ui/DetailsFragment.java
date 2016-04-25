package jalal.muhlenberg.edu.bergbeta.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jalal.muhlenberg.edu.bergbeta.R;
import jalal.muhlenberg.edu.bergbeta.db.MenuItem;
import jalal.muhlenberg.edu.bergbeta.db.RealmInstance;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    private static final String MENU_ITEM_ID = "menu_item_id";

    // TODO: Rename and change types of parameters
    private String menuitemId;


    public DetailsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id id of menu_item to display.
     * @return A new instance of fragment DetailsFragment.
     */
    public static DetailsFragment newInstance(String id) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(MENU_ITEM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuitemId = getArguments().getString(MENU_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        RealmInstance instance = new RealmInstance(getActivity());
        MenuItem item = instance.getMenuItem(menuitemId);

        ((TextView) view.findViewById(R.id.calories_view)).setText(item.getCalories());
        ((TextView) view.findViewById(R.id.fat_calories_view)).setText(item.getFatCalories());
        ((TextView) view.findViewById(R.id.fat_view)).setText(item.getFat());
        ((TextView) view.findViewById(R.id.sugar_view)).setText(item.getSugar());
        ((TextView) view.findViewById(R.id.protein_view)).setText(item.getProtein());

        return view;
    }


}
