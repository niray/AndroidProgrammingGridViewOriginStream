package actionbar.com.huway.actionbardemo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PollService extends IntentService {

    private static final String TAG = PollService.class.getSimpleName();


    public PollService() {
        super(TAG);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 15 * 1000, pi);
        } else {
            am.cancel(pi);
            pi.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: " + System.currentTimeMillis());

        Notification notification = new Notification.Builder(this)
                .setTicker("Ticker")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("ContentTitle")
                .setContentText("ContontText")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, CameraActivity.class), 0))
                .setAutoCancel(true)
                .build();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }
}
