package com.vetealinfierno.locus;
//***** 3/24/17 jGAT
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class UserList extends ArrayAdapter<UserInfo> {

    private Activity context;
    private List<UserInfo> userList;

    public UserList(Activity context, List<UserInfo> userList){
        super(context, R.layout.list_layout, userList);
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView name = (TextView) listViewItem.findViewById(R.id.name_TxtV);
        TextView location = (TextView) listViewItem.findViewById(R.id.LatLng_TxtV);

        UserInfo user = userList.get(position);
        String itemOne = user.getStatus()+": "+user.getEmail();
        String itemTwo = "Location: "+user.getUserLocation();
        name.setText(itemOne);
        location.setText(itemTwo);
        return listViewItem;
    }
}
//finito jGAT
