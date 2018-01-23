package org.team7.TakeMyPackage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.team7.sports.R;
import org.team7.TakeMyPackage.model.DeliveryPackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by sunzhihao on 20/1/18.
 */

public class RequestDetailActivity extends AppCompatActivity {
    private TextView mPackageName;
    private TextView mAssignerAddress;
    private TextView mAssignerContact;
    private TextView mTime;
    private TextView mRemarks;

    private Button mAccept;
    private Button mReject;

    private String packageName;
    private String requesterAddress;
    private String assignerAddress;
    private String assignerPhone;
    private String requesterPhone;
    private String deliveryPhone;
    private String remarks;
    private String date;
    private String time;
    private String requesterId;
    private String assignerId;
    private String packageId;
    private DeliveryPackage deliveryPackage;

    private FirebaseDatabase database;
    private DatabaseReference myref;
    private String usrid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        mPackageName = findViewById(R.id.package_name_RD);
        mAssignerAddress = findViewById(R.id.assigner_address_RD);
        mAssignerContact = findViewById(R.id.assigner_contact_RD);
        mTime = findViewById(R.id.time_RD);
        mRemarks = findViewById(R.id.remarks_RD);
        mAccept = findViewById(R.id.accept_request);
        mReject = findViewById(R.id.reject_request);

        packageId = getIntent().getStringExtra("this_package_id");
        if (packageId == null) {
            return;
        }

        toolbar = findViewById(R.id.create_game_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Assigner Details");

        myref = FirebaseDatabase.getInstance().getReference().child("DeliveryPackage").child(packageId);
        Log.d("ddd", packageId);
        deliveryPackage = new DeliveryPackage();
        FirebaseUser currentUse = FirebaseAuth.getInstance().getCurrentUser();
        usrid = currentUse.getUid();

        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updatePackage(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updatePackage(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RequestDetailActivity.this, "You claimed a loss", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendNotification();
                onBackPressed();
            }
        });
    }

    private void updatePackage(DataSnapshot dataSnapshot, String s) {
        switch (dataSnapshot.getKey()) {
            case "date": {
                date = dataSnapshot.getValue().toString();
                deliveryPackage.setDate(date);
                break;
            }
            case "time": {
                time = dataSnapshot.getValue().toString();
                mTime.setText(time);
                deliveryPackage.setTime(time);
                break;
            }
            case "packageName": {
                packageName = dataSnapshot.getValue().toString();
                mPackageName.setText(packageName);
                deliveryPackage.setPackageName(packageName);
                break;
            }
            case "assigner": {
                assignerId = dataSnapshot.getValue().toString();
                deliveryPackage.setAssigner(assignerId);
                Log.i("assignerId: ", assignerId);
                updateAssigner(assignerId);
                break;
            }
            case "requesterPhone": {
                requesterPhone = dataSnapshot.getValue().toString();
                deliveryPackage.setRequesterPhone(requesterPhone);
                break;
            }
            case "requesterAddress": {
                requesterAddress = dataSnapshot.getValue().toString();
                deliveryPackage.setRequesterAddress(requesterAddress);
                break;
            }
            case "remark": {
                remarks = dataSnapshot.getValue().toString();
                mRemarks.setText(remarks);
                deliveryPackage.setRemark(remarks);
                break;
            }
            case "deliveryPhone": {
                deliveryPhone = dataSnapshot.getValue().toString();
                deliveryPackage.setDeliveryPhone(deliveryPhone);
                break;
            }
        }
    }

    private void updateAssigner(String assignerId) {
        DatabaseReference assigner = FirebaseDatabase.getInstance().getReference().child("Users").child(assignerId);
        assigner.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                processAssignerData(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                processAssignerData(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void processAssignerData(DataSnapshot dataSnapshot, String s) {
        Log.d(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
        switch (dataSnapshot.getKey()) {
            case "address": {
                assignerAddress = dataSnapshot.getValue().toString();
                mAssignerAddress.setText(assignerAddress);
                break;
            }
            case "phone": {
                assignerPhone = dataSnapshot.getValue().toString();
                mAssignerContact.setText(assignerPhone);
                break;
            }
        }
    }

    private void sendNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String id = "receive_package_01";
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "TakeMyPackage", NotificationManager.IMPORTANCE_HIGH);


        CharSequence name = getString(R.string.acceptNotificationTitle);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String description = String.format(getString(R.string.acceptNotificationBody),
                deliveryPackage.getPackageName(),
                timeFormat.format(new Date()),
                assignerAddress,
                deliveryPackage.getRequesterPhone());

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.sports_small_icon)
                        .setContentTitle("Package is received!")
                        .setChannelId(CHANNEL_ID)
                        .setContentText(description);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ViewAssignmentActivity.class);
        resultIntent.putExtra("this_package_id", packageId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RequestDetailActivity.class);
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
}
