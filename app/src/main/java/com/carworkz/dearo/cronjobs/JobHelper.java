package com.carworkz.dearo.cronjobs;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.carworkz.dearo.cronjobs.forceupdatestatus.ForceUpdateService;
import com.carworkz.dearo.cronjobs.userconfig.UserConfigService;
import com.carworkz.dearo.cronjobs.imageupload.ImageUploadJob;
import com.carworkz.dearo.notification.deviceregistration.DeviceRegistrarJob;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Farhan on 8/11/17.
 */

public final class JobHelper {

    public static final String JOB_FORCE_UPDATE = "job_force_update";
    public static final String JOB_USER_CONFIG = "job_user_config";
    public static final String JOB_FILE_UPLOAD = "job_file_upload";
    public static final String JOB_DEVICE_REGISTRAR = "job_device_registrar";


    public static final int JOB_ID_FORCE_UPDATE = 1;
    public static final int JOB_ID_USER_CONFIG = 2;
    public static final int JOB_ID_FILE_UPLOAD = 3;
    public static final int JOB_ID_DEVICE_REGISTRAR = 4;

    private static final long intervalTime = TimeUnit.MINUTES.toMillis(20);
    private static final long maxExecutionDelay = TimeUnit.MINUTES.toMillis(5);

    public static void scheduleJob(Context context, String jobType) {
        ComponentName componentName;
        JobInfo.Builder jobBuilder = null;
        switch (jobType) {
            case JOB_FORCE_UPDATE: {
                componentName = new ComponentName(context, ForceUpdateService.class);
                jobBuilder = new JobInfo.Builder(JOB_ID_FORCE_UPDATE, componentName);
                jobBuilder.setPeriodic(intervalTime);
                break;
            }
            case JOB_USER_CONFIG: {
                componentName = new ComponentName(context, UserConfigService.class);
                jobBuilder = new JobInfo.Builder(JOB_ID_USER_CONFIG, componentName);
                jobBuilder.setPeriodic(intervalTime);
                break;
            }
            case JOB_FILE_UPLOAD: {
                componentName = new ComponentName(context, ImageUploadJob.class);
                jobBuilder = new JobInfo.Builder(JOB_ID_FILE_UPLOAD, componentName);
                jobBuilder.setPeriodic(intervalTime);
                break;
            }
            case JOB_DEVICE_REGISTRAR: {
                componentName = new ComponentName(context, DeviceRegistrarJob.class);
                jobBuilder = new JobInfo.Builder(JOB_ID_DEVICE_REGISTRAR, componentName);
                break;
            }
        }
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobBuilder != null && jobScheduler != null) {
            JobInfo jobInfo = jobBuilder
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Timber.d("Job scheduled!");
            } else {
                Timber.d("Job not scheduled");
            }
        }

    }

    public static void scheduleAllMandatoryJobs(Context context) {
        scheduleJob(context, JOB_FORCE_UPDATE);
        scheduleJob(context, JOB_USER_CONFIG);
        scheduleJob(context, JOB_FILE_UPLOAD);
    }
}
