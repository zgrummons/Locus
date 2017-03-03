package com.vetealinfierno.locus.Models;

import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Created by zgrum on 2/27/2017.
 * singleton class to handle database crap
 */

public class DBModel
{
    //the only instance of the class we'll allow in the project
    private static DBModel instance = null;
    private static String connString = "jdbc:jtds:sqlserver://99.64.48.184:1433;DatabaseName=locus";
    private static String name = null;
    private static int userId = -1;
    private static int groupId = -1;


    //block off constructor
    private DBModel(){}

    public static DBModel getInstance()
    {
        if (instance == null)
            instance = new DBModel();
        return instance;
    }

    protected int getUserId()
    {
        return userId;
    }

    protected void setUserId(int value)
    {
        if (userId == -1)
            userId = value;
    }

    protected static void getNewUserId()
    {
        Connection conn = null;
        CallableStatement call = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(connString, "locus", "locus");
            String query = "EXEC getNewUserId NULL";
            call = conn.prepareCall(query);

            if (call.execute())
            {
                try (ResultSet rs = call.getResultSet())
                {
                    userId = rs.getInt(0);
                }
            }
            call.close();
            conn.close();
        }
        catch (Exception e)
        {
          //  Toast.makeText(this, e.getStackTrace().toString(), Toast.LENGTH_LONG);
        }

        //saveConfig();
    }

    protected static void getGroupId()
    {
        //asdfasdf
    }
}
