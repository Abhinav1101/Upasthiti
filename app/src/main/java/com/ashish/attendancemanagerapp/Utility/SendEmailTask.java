package com.ashish.attendancemanagerapp.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class SendEmailTask extends Activity implements Runnable {

    private static final String TAG = "SendEmailTask";

    private ArrayList<String> emailIds;
    private String subject;
    private String message;
    private Context context;

    public SendEmailTask(Context context, ArrayList<String> emailIds, String subject, String message){
        this.context = context;
        this.emailIds = emailIds;
        this.subject = subject;
        this.message = message;
    }

    @Override
    public void run() {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);

        for(int i = 0; i < emailIds.size(); i++){
            Log.d(TAG, "run: ");
            email.putExtra(android.content.Intent.EXTRA_EMAIL,
                    emailIds.toArray(new String[emailIds.size()]));
        }
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        email.putExtra(android.content.Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822"); //or email.setType("text/plain");

        context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }
}
