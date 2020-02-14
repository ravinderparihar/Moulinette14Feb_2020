package com.moulinette.sms.sms_db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.moulinette.sms.sms_db.interfaces.SMSDao;
import com.moulinette.sms.sms_db.pojo.SMS;

@Database (entities = { SMS.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SMSDao taskDao();
}
