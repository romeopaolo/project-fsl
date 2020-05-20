package projects.fantasysoccerauction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// TODO: implement, once the server is working
public class LiveAuctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_auction);

        // schedule job
        //scheduleJob();

        Button btn_raise = findViewById(R.id.a_live_auction_btn_raise);
        btn_raise.setOnClickListener(v -> {
            Toast.makeText(this, R.string.t_not_yet_implemented, Toast.LENGTH_LONG).show();
        });
        /*
        submitButton.setOnClickListener(v -> {
            JSONObject postData = new JSONObject();
            try {
                postData.put("name", name.getText().toString());
                postData.put("address", address.getText().toString());
                postData.put("manufacturer", manufacturer.getText().toString());
                postData.put("location", location.getText().toString());
                postData.put("type", type.getText().toString());
                postData.put("deviceID", deviceID.getText().toString());

                new SendDeviceDetails().execute("http://52.88.194.67:8080/IOTProjectServer/registerDevice", postData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
         */
    }
    /*
    private void scheduleJob() {
        ComponentName component = new ComponentName(getApplicationContext(), UpdateViewLiveAuction.class);
        JobInfo jobInfo = new JobInfo.Builder(0, component)
                .setRequiresCharging(false)
                .build();

        JobScheduler scheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        int resCode = 0;

        if (scheduler != null) {
            resCode = scheduler.schedule(jobInfo);
        }
        if (resCode == JobScheduler.RESULT_SUCCESS) {
            Log.i("JOBSCHEDULER", "ok");
        }
    }
    */
}
