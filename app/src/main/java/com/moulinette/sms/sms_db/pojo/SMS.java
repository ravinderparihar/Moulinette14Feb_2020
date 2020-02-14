package com.moulinette.sms.sms_db.pojo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity (tableName = "sms")
public class SMS {

    @PrimaryKey (autoGenerate = true)
    private int uid;

    @ColumnInfo (name = "number")
    private String number;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "time")
    private String time;


    public int getUid ( ){
        return uid;
    }

    public void setUid (int uid){
        this.uid = uid;
    }

    public String getNumber ( ){
        return number;
    }

    public void setNumber (String number){
        this.number = number;
    }

    public String getBody ( ){
        return body;
    }

    public void setBody (String body){
        this.body = body;
    }

    public String getTime ( ){
        return time;
    }

    public void setTime (String time){
        this.time = time;
    }
}
