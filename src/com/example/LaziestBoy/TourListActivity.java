package com.example.LaziestBoy;

import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;

public class TourListActivity extends ListActivity {
    private static final String[] items = {"Official Campus Tour", "Sports Tour", "Dorms Tour", "Engineering Tour", "Fountain Run", "Select Destination"};
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tourlist);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

    }

    public void onListItemClick(ListView parent, View v, int position, long id)
    {
//        String sel;
//        sel = items[position];
        Intent intent = new Intent(TourListActivity.this, MapActivity.class);
        intent.putExtra("selection", position);
        finish();
        startActivity(intent);
    }

}
