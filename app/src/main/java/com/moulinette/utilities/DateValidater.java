package com.moulinette.utilities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidater {
    public static boolean isValidDate(String pDateString) throws ParseException{
        Date date = new SimpleDateFormat("dd MMMM, yyyy").parse(pDateString);
        return new Date().before(date);
    }



}


