package com.moulinette.sms.sms_db.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.moulinette.sms.sms_db.pojo.SMS;

import java.util.List;

@Dao
public interface SMSDao {

    @Query ("SELECT * FROM SMS")
    List <SMS> getSms ( );

    @Query("SELECT * FROM SMS where number LIKE  :number AND time LIKE :time")
    SMS findsmsByNumber (String number, String time);

    @Query("SELECT COUNT(*) from SMS")
    int countsms ( );

    @Insert
    void insertsms(SMS... sms);

    @Delete
    void deletesms (SMS sms);
}
