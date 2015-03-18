package manuele.bryan.lolwinrate.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import manuele.bryan.lolwinrate.Adapters.DrawerAdapter;
import manuele.bryan.lolwinrate.Databases.PreferencesDataBase;
import manuele.bryan.lolwinrate.Items.DrawerItem;
import manuele.bryan.lolwinrate.R;

public class NavigationDrawerFragment extends Fragment {
    public NavigationDrawerListener navigationDrawerListener;

    public interface NavigationDrawerListener {
        public void updateFragmentHolder();
    }

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private View view;
    private ActionBarDrawerToggle mDrawerToggle;


    public Context context;

    List<DrawerItem> dataList;
    DrawerAdapter drawerAdapter;
    DrawerLayout mDrawerLayout;

    public ImageView champBanner;
    public TextView userName;
    public ListView listView;

    public static NavigationDrawerFragment newInstance() {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (getArguments() != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        champBanner = (ImageView) view.findViewById(R.id.champBanner);
        userName = (TextView) view.findViewById(R.id.accountName);
        listView = (ListView) view.findViewById(R.id.drawer_list);

        updateDrawer();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                selectItem(position);
            }
        });

        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(view);
    }

    public void updateDrawer() {
        ArrayList<String> drawerStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.drawer_items)));
        TypedArray drawerImages = getResources().obtainTypedArray(R.array.drawer_drawings);

        dataList = new ArrayList<>();

        PreferencesDataBase preferences = new PreferencesDataBase(context);
        int lastOpenedTab = preferences.getLastOpenedTab();

        for (int i = 0; i < drawerStrings.size(); i++) {
            DrawerItem drawerItem = new DrawerItem(drawerStrings.get(i), drawerImages.getResourceId(i, -1));
            if (i == lastOpenedTab) {
                drawerItem.currentlySelected = true;
            } else {
                drawerItem.currentlySelected = false;
            }

            dataList.add(drawerItem);
        }

        drawerImages.recycle();


        drawerAdapter = new DrawerAdapter(context, dataList);
        listView.setAdapter(drawerAdapter);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (listView != null) {
            listView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(view);
        }

        PreferencesDataBase preferences = new PreferencesDataBase(context);
        preferences.updateLastOpenedTab(position);

        if (navigationDrawerListener != null) {
            navigationDrawerListener.updateFragmentHolder();
        }

        updateDrawer();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.menu_main, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

//        if (item.getItemId() == R.id.) {
//            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            navigationDrawerListener = (NavigationDrawerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


}
