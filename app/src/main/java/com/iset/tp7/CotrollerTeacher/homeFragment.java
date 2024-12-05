package com.iset.tp7.CotrollerTeacher;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tp7.R;
import com.iset.tp7.Adapters.TeacherAdapter;
import com.iset.tp7.DataBaseSQLlite.DatabaseHelper;
import com.iset.tp7.entities.Teacher;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView rv;
    private TeacherAdapter adapter;
    private List<Teacher> teacherList;
    DatabaseHelper db;

    public homeFragment() {
        // Required empty public constructor
    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    private PopupMenu.OnMenuItemClickListener onPopupMenuClickListener() {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.show_hide_recycler_view) {
                    if (rv.getVisibility() == View.VISIBLE) {
                        rv.setVisibility(View.GONE);
                    } else {
                        rv.setVisibility(View.VISIBLE);
                    }
                    return true;
                } else if (item.getItemId() == R.id.change_background) {
                    TextView tx = getActivity().findViewById(R.id.header_list);
                    tx.setBackgroundColor(Color.WHITE);
                    tx.setTextColor(Color.BLACK);
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerView
        rv = view.findViewById(R.id.mRecyclerview);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);

        // Initialize the database helper
        db = new DatabaseHelper(getContext());

        // Fetch teachers from the database
        teacherList = db.getAllTeachers();

        // Initialize and set the adapter
        adapter = new TeacherAdapter(getContext(), teacherList);
        rv.setAdapter(adapter);
        TextView tx = view.findViewById(R.id.header_list);
        tx.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create the PopupMenu
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setOnMenuItemClickListener(onPopupMenuClickListener());
                        popupMenu.inflate(R.menu.menu_pop_up);  // Inflate the menu XML
                        popupMenu.show();  // Show the popup menu
                    }
                }
        );

        return view;  // Return the inflated view
    }

    public TeacherAdapter getAdapter() {
        return adapter;
    }
}