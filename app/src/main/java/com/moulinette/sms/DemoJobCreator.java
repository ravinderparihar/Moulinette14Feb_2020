package com.moulinette.sms;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

public class DemoJobCreator implements JobCreator {

    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case DemoSyncJob.TAG:
                return new DemoSyncJob();
            default:
                return null;
        }
    }

    public static final class AddReceiver extends AddJobCreatorReceiver {
        @Override
        protected void addJobCreator(@NonNull Context context, @NonNull JobManager manager) {
        }
    }
}