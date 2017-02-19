package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.app.ListActivity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

///this activity will display a list of the members in the group
///needs a method that receives data from the DB table

//TODO: add code that will update a list with data received from the DB table
public class MembersListActivity extends AppCompatActivity {
    private List<Car> myCars = new ArrayList<Car>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        populateCarList();
        populateListView();
        registerClickCallback();
    }

    ///mock up list for the members list page
    private void populateCarList(){
        myCars.add(new Car("Ford", 1940, R.drawable.ic_1, "Needs work"));
        myCars.add(new Car("Chevy", 1950, R.mipmap.ic_2, "Good"));
        myCars.add(new Car("Toyota", 1980, R.mipmap.ic_3, "Fair"));
        myCars.add(new Car("Lincoln", 1960, R.mipmap.ic_4, "Poor"));
        myCars.add(new Car("Subaru", 1990, R.mipmap.ic_5, "Excellent"));
        myCars.add(new Car("Ford", 1940, R.mipmap.ic_6, "Needs work"));
        myCars.add(new Car("Chevy", 1950, R.mipmap.ic_7, "Good"));
        myCars.add(new Car("Toyota", 1980, R.mipmap.ic_8, "Fair"));
        myCars.add(new Car("Lincoln", 1960, R.mipmap.ic_9, "Poor"));
        myCars.add(new Car("Subaru", 1990, R.mipmap.ic_10, "Excellent"));
        myCars.add(new Car("Ford", 1940, R.drawable.ic_1, "Needs work"));
        myCars.add(new Car("Chevy", 1950, R.mipmap.ic_2, "Good"));
        myCars.add(new Car("Toyota", 1980, R.mipmap.ic_3, "Fair"));
        myCars.add(new Car("Lincoln", 1960, R.mipmap.ic_4, "Poor"));
        myCars.add(new Car("Subaru", 1990, R.mipmap.ic_5, "Excellent"));
        myCars.add(new Car("Ford", 1940, R.mipmap.ic_6, "Needs work"));
        myCars.add(new Car("Chevy", 1950, R.mipmap.ic_7, "Good"));
        myCars.add(new Car("Toyota", 1980, R.mipmap.ic_8, "Fair"));
        myCars.add(new Car("Lincoln", 1960, R.mipmap.ic_9, "Poor"));
        myCars.add(new Car("Subaru", 1990, R.mipmap.ic_10, "Excellent"));

    }

    ///populating the view with myListAdapter
    private void populateListView(){
        ArrayAdapter<Car> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.carsListView);
        list.setAdapter(adapter);
    }

    ///tells when an item in the list is clicked on
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.carsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id){
                //look up which car was clicked then do whatever you want with that object
                Car clickedCar = myCars.get(position);
                String message = "You clicked position: " + position + " Which is car make: "
                        + clickedCar.getMake();
                Toast.makeText(MembersListActivity.this, message, Toast.LENGTH_LONG).show();

            }
        });
    }

    //inner class to populate the list
    private class MyListAdapter extends ArrayAdapter<Car> {
        ///constructor tells us what we are working with
        public MyListAdapter(){
            super(MembersListActivity.this, R.layout.item_view, myCars);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //making sure we have a view to work with
            //makes view to populate the listView
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }
            //find item to work with (pull information from) the MODEL!
            Car currentCar = myCars.get(position);
            //fill in all the pieces

            //fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_Icon);
            imageView.setImageResource(currentCar.getIconID());
            //make
            TextView makeTxt = (TextView) itemView.findViewById(R.id.item_mkTxt);
            makeTxt.setText(currentCar.getMake());
            //year
            TextView yearTxt = (TextView) itemView.findViewById(R.id.item_yrTxt);
            yearTxt.setText(Integer.toString(currentCar.getYear()));
            //condition
            TextView conditionTxt = (TextView) itemView.findViewById(R.id.item_conText);
            conditionTxt.setText(currentCar.getCondition());
            return itemView;

        }


    }

}
//finito