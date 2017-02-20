package com.vetealinfierno.locus;
//***** 2/18/17 jGAT
/**
 * Created by johntoland on 2/19/17.
 * Store information about whatever you want
 */

public class Car {
    private String make;
    private int year;
    private int iconID;
    private String condition;

    public Car(String make, int year, int iconID, String condition){
        super();
        this.make = make;
        this.year = year;
        this.iconID = iconID;
        this.condition = condition;
    }

    public String getMake(){
        return make;
    }

    public int getYear(){
        return year;
    }

    public int getIconID(){
        return iconID;
    }

    public String getCondition(){
        return condition;
    }

}
