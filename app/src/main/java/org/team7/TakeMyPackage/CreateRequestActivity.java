package org.team7.TakeMyPackage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.team7.sports.R;
import org.team7.TakeMyPackage.model.DeliveryPackage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CreateRequestActivity extends AppCompatActivity {

    private TextInputLayout packageNameTIL;
    private TextInputLayout packageDateTIL;
    private TextInputLayout packageTimeTIL;
    private TextInputLayout deliveryPhoneTIL;
    private TextInputLayout remarksTIL;
    private Button mSubmit;
    private Button mCancel;
    private ProgressDialog RegisterProgress;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Toolbar toolbar;

    private String packageName = "";
    private String packageDate = "";
    private String packageTime = "";
    private String remarks = "";
    private String deliveryPhone = "";
    private String requesterPhone = "";
    private String requesterAddress = "";


    public void createPackage(String packageName, String packageDate, String packageTime,
                           String remarks, String deliveryPhone, String requester,
                           String requesterAddress, String requesterPhone) {
        if (packageName.equals("") || packageDate.equals("") || packageTime.equals("")
                || deliveryPhone.equals("") || requesterAddress.equals("")
                || requesterPhone.equals("")) {
            Toast.makeText(CreateRequestActivity.this, "need to fill in all the field", Toast.LENGTH_LONG).show();
            return;
        }
        if (isTimeValid(packageTime) == false) {
            Toast.makeText(CreateRequestActivity.this, "wrong time format", Toast.LENGTH_LONG).show();
            return;
        }

        final DeliveryPackage newPackage = new DeliveryPackage(packageName, packageDate, packageTime, remarks, requester,
                deliveryPhone, requesterPhone, requesterAddress);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference().child("DeliveryPackage");
            String packageid = myRef.push().getKey();
            myRef = myRef.child(packageid);
            newPackage.setPackageId(packageid);

            myRef.setValue(newPackage).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateRequestActivity.this, "succeeded", Toast.LENGTH_LONG).show();

                        onBackPressed();
                        sendNotification(newPackage);
                    } else {
                        Toast.makeText(CreateRequestActivity.this, "failed to create", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void sendNotification(DeliveryPackage deliveryPackage) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String id = "new_package_01";
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "TakeMyPackage", NotificationManager.IMPORTANCE_HIGH);

        CharSequence name = getString(R.string.notificationTitle);

        String description = String.format(getString(R.string.notificationBody),
            deliveryPackage.getPackageName(),
                deliveryPackage.getTime(),
                deliveryPackage.getRequesterAddress(),
                deliveryPackage.getRequesterPhone());

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.sports_small_icon)
                        .setContentTitle(name)
                        .setChannelId(CHANNEL_ID)
                        .setContentText(description);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ViewAssignmentActivity.class);
        resultIntent.putExtra("this_package_id", deliveryPackage.getPackageId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ViewAssignmentActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.createNotificationChannel(mChannel);
        mNotificationManager.notify(new Random().nextInt(10000), mBuilder.build());
        Log.d("notification", "sent");
    }

    public boolean isTimeValid(String time) {
        if (time.charAt(2) != ':') return false;
        if ((time.charAt(0) - '0') * 10 + (time.charAt(1) - '0') > 24) return false;
        return (time.charAt(0) - '0') * 10 + (time.charAt(1) - '0') <= 59;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        packageNameTIL = findViewById(R.id.packageName_TIL);
        packageDateTIL = findViewById(R.id.packageDate_TIL);
        packageTimeTIL = findViewById(R.id.packageTime_TIL);
        remarksTIL = findViewById(R.id.remark_TIL);
        deliveryPhoneTIL = findViewById(R.id.deliveryPhone_TIL);
        mSubmit = findViewById(R.id.request_create_submit_B);
        mCancel = findViewById(R.id.game_create_cancel_B);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date today = new Date();
        packageDateTIL.getEditText().setText(dateFormat.format(today));

        RegisterProgress = new ProgressDialog(this);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {

                packageName = packageNameTIL.getEditText().getText().toString();
                packageDate = packageDateTIL.getEditText().getText().toString();
                packageTime = packageTimeTIL.getEditText().getText().toString();
                remarks = remarksTIL.getEditText().getText().toString();
                deliveryPhone = deliveryPhoneTIL.getEditText().getText().toString();

                FirebaseUser currentUse = FirebaseAuth.getInstance().getCurrentUser();
                final String uid = currentUse.getUid();

                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d(child.getKey(), child.getValue().toString());
                            if (child.getKey().equals("phone")) {
                                requesterPhone = (String) child.getValue();
                            } else if (child.getKey().equals("address")) {
                                requesterAddress = (String) child.getValue();
                            }
                            createPackage(packageName, packageDate, packageTime, remarks, deliveryPhone, uid, requesterAddress, requesterPhone);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }


        });
    }
}
