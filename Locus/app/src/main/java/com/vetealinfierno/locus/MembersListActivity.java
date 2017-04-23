package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.vetealinfierno.locus.HomeActivity.GROUP_ID;

///this activity will display a list of the members in the group
public class MembersListActivity extends AppCompatActivity {

    public void print(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //region Class Variables Region ##########################################################################################
    private DatabaseReference dBRef;
    ListView listViewUser;
    List<UserInfo> userList;
    //endregion

    //region Android Life Cycle Method Region ################################################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listViewUser = (ListView) findViewById(R.id.usersListView);
        userList = new ArrayList<>();
        dBRef = FirebaseDatabase.getInstance().getReference(GROUP_ID);
        //print(dBRef.toString());
        registerClickCallback();
    }

    @Override
    protected void onStart() {
        super.onStart();
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot userSnapShot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapShot.getValue(UserInfo.class);
                    userList.add(user);
                }
                ArrayAdapter<UserInfo> adapter = new MyListAdapter();
                listViewUser.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

    //region Methods for Members List View Region ############################################################################
    private void registerClickCallback() {
        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id){
                //look up which car was clicked then do whatever you want with that object
                UserInfo clickedUser = userList.get(position);
                String message = "You clicked position: " + position + " Which is user: "
                        + clickedUser.getEmail();
                Toast.makeText(MembersListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    //inner class to populate the list
    private class MyListAdapter extends ArrayAdapter<UserInfo> {
        ///constructor tells us what we are working with
        public MyListAdapter(){
            super(MembersListActivity.this, R.layout.item_view, userList);
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
            UserInfo currentUser = userList.get(position);

            //fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_Icon);
            imageView.setImageResource(R.drawable.ic_audiotrack_light);

            TextView name = (TextView) itemView.findViewById(R.id.item_userStatus);
            TextView location = (TextView) itemView.findViewById(R.id.item_LastSeen);

            String itemOne = currentUser.getStatus()+": "+currentUser.getEmail();
            String itemTwo = "Last Seen: "+ currentUser.getTime();

            name.setText(itemOne);
            location.setText(itemTwo);

            return itemView;
        }
    }

    //endregion

    //region Button Control Method Region ####################################################################################
    public void switchToHomeActivity(View view){
        startActivity(new Intent(this, HomeActivity.class));
    }
    //endregion

}
//finito jGAT