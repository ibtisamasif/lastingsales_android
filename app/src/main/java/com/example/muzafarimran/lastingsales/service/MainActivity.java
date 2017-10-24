///*
// * Copyright 2013 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.example.muzafarimran.lastingsales.service;
//
//import android.app.Activity;
//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Messenger;
//import android.os.PersistableBundle;
//import android.support.annotation.ColorRes;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.muzafarimran.lastingsales.BuildConfig;
//import com.example.muzafarimran.lastingsales.R;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
//
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.MESSENGER_INTENT_KEY;
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.MSG_COLOR_START;
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.MSG_COLOR_STOP;
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.MSG_UNCOLOR_START;
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.MSG_UNCOLOR_STOP;
//import static com.example.muzafarimran.lastingsales.service.ProfileFetchService.WORK_DURATION_KEY;
//
//
///**
// * Schedules and configures jobs to be executed by a {@link JobScheduler}.
// * <p>
// * {@link ProfileFetchService} can send messages to this via a {@link Messenger}
// * that is sent in the Intent that starts the Service.
// */
//public class MainActivity extends Activity {
//
//    private static final String TAG = "ProfileFetch";
////    private static final String TAG = MainActivity.class.getSimpleName();
//
////    private EditText mDelayEditText;
////    private EditText mDeadlineEditText;
////    private EditText mDurationTimeEditText;
////    private RadioButton mWiFiConnectivityRadioButton;
////    private RadioButton mAnyConnectivityRadioButton;
////    private CheckBox mRequiresChargingCheckBox;
////    private CheckBox mRequiresIdleCheckbox;
//
//    private ComponentName mServiceComponent;
//
//    private int mJobId = 0;
//
//    // Handler for incoming messages from the service.
//    private IncomingMessageHandler mHandler;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sample_main);
//
//        // Set up UI.
////        mDelayEditText = (EditText) findViewById(R.id.delay_time);
////        mDurationTimeEditText = (EditText) findViewById(R.id.duration_time);
////        mDeadlineEditText = (EditText) findViewById(R.id.deadline_time);
////        mWiFiConnectivityRadioButton = (RadioButton) findViewById(R.id.checkbox_unmetered);
////        mAnyConnectivityRadioButton = (RadioButton) findViewById(R.id.checkbox_any);
////        mRequiresChargingCheckBox = (CheckBox) findViewById(R.id.checkbox_charging);
////        mRequiresIdleCheckbox = (CheckBox) findViewById(R.id.checkbox_idle);
//        mServiceComponent = new ComponentName(this, ProfileFetchService.class);
//
//        mHandler = new IncomingMessageHandler(this);
//    }
//
//    @Override
//    protected void onStop() {
//        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
//        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
//        // to stopService() won't prevent scheduled jobs to be processed. However, failing
//        // to call stopService() would keep it alive indefinitely.
//        stopService(new Intent(this, ProfileFetchService.class));
//        super.onStop();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Start service and provide it a way to communicate with this class.
//        Intent startServiceIntent = new Intent(this, ProfileFetchService.class);
//        Messenger messengerIncoming = new Messenger(mHandler);
//        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
//        startService(startServiceIntent);
//    }
//
//    /**
//     * Executed when user clicks on SCHEDULE JOB.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void scheduleJob(View v) {
//        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, mServiceComponent);
//
////        String delay = mDelayEditText.getText().toString();
////        if (!TextUtils.isEmpty(delay)) {
//            builder.setMinimumLatency(Long.valueOf(2) * 1000);
////        }
////        String deadline = mDeadlineEditText.getText().toString();
////        if (!TextUtils.isEmpty(deadline)) {
//            builder.setOverrideDeadline(Long.valueOf(5) * 1000);
////        }
////        boolean requiresUnmetered = mWiFiConnectivityRadioButton.isChecked();
////        boolean requiresAnyConnectivity = mAnyConnectivityRadioButton.isChecked();
////        if (requiresUnmetered) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
////        } else if (requiresAnyConnectivity) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
////        }
//        builder.setRequiresDeviceIdle(false);
//        builder.setRequiresCharging(true);
//
//        // Extras, work duration.
//        PersistableBundle extras = new PersistableBundle();
////        String workDuration = mDurationTimeEditText.getText().toString();
////        if (TextUtils.isEmpty(workDuration)) {
////            workDuration = "1";
////        }
//        extras.putLong(WORK_DURATION_KEY, Long.valueOf(20) * 1000);
//
//        builder.setExtras(extras);
//
//        // Schedule job
//        Log.d(TAG, "Scheduling job");
//        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        tm.schedule(builder.build());
//    }
//
//    /**
//     * Executed when user clicks on CANCEL ALL.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void cancelAllJobs(View v) {
//        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        tm.cancelAll();
//        Toast.makeText(MainActivity.this, R.string.all_jobs_cancelled, Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * Executed when user clicks on FINISH LAST TASK.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void finishJob(View v) {
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
//        if (allPendingJobs.size() > 0) {
//            // Finish the last one
//            int jobId = allPendingJobs.get(0).getId();
//            jobScheduler.cancel(jobId);
//            Toast.makeText(
//                    MainActivity.this, String.format(getString(R.string.cancelled_job), jobId),
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(
//                    MainActivity.this, getString(R.string.no_jobs_to_cancel),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * A {@link Handler} allows you to send messages associated with a thread. A {@link Messenger}
//     * uses this handler to communicate from {@link ProfileFetchService}. It's also used to make
//     * the start and stop views blink for a short period of time.
//     */
//    private static class IncomingMessageHandler extends Handler {
//
//        // Prevent possible leaks with a weak reference.
//        private WeakReference<MainActivity> mActivity;
//
//        IncomingMessageHandler(MainActivity activity) {
//            super(/* default looper */);
//            this.mActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            MainActivity mainActivity = mActivity.get();
//            if (mainActivity == null) {
//                // Activity is no longer available, exit.
//                return;
//            }
//            View showStartView = mainActivity.findViewById(R.id.onstart_textview);
//            View showStopView = mainActivity.findViewById(R.id.onstop_textview);
//            Message m;
//            switch (msg.what) {
//                /*
//                 * Receives callback from the service when a job has landed
//                 * on the app. Turns on indicator and sends a message to turn it off after
//                 * a second.
//                 */
//                case MSG_COLOR_START:
//                    // Start received, turn on the indicator and show text.
//                    showStartView.setBackgroundColor(getColor(R.color.start_received));
//                    updateParamsTextView(msg.obj, "started");
//
//                    // Send message to turn it off after a second.
//                    m = Message.obtain(this, MSG_UNCOLOR_START);
//                    sendMessageDelayed(m, 1000L);
//                    break;
//                /*
//                 * Receives callback from the service when a job that previously landed on the
//                 * app must stop executing. Turns on indicator and sends a message to turn it
//                 * off after two seconds.
//                 */
//                case MSG_COLOR_STOP:
//                    // Stop received, turn on the indicator and show text.
//                    showStopView.setBackgroundColor(getColor(R.color.stop_received));
//                    updateParamsTextView(msg.obj, "stopped");
//
//                    // Send message to turn it off after a second.
//                    m = obtainMessage(MSG_UNCOLOR_STOP);
//                    sendMessageDelayed(m, 2000L);
//                    break;
//                case MSG_UNCOLOR_START:
//                    showStartView.setBackgroundColor(getColor(R.color.none_received));
//                    updateParamsTextView(null, "");
//                    break;
//                case MSG_UNCOLOR_STOP:
//                    showStopView.setBackgroundColor(getColor(R.color.none_received));
//                    updateParamsTextView(null, "");
//                    break;
//            }
//        }
//
//        private void updateParamsTextView(@Nullable Object jobId, String action) {
//            TextView paramsTextView = (TextView) mActivity.get().findViewById(R.id.task_params);
//            if (jobId == null) {
//                paramsTextView.setText("");
//                return;
//            }
//            String jobIdText = String.valueOf(jobId);
//            paramsTextView.setText(String.format("Job ID %s %s", jobIdText, action));
//        }
//
//        private int getColor(@ColorRes int color) {
//            return mActivity.get().getResources().getColor(color);
//        }
//    }
//}