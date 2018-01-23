package org.team7.TakeMyPackage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.team7.sports.R;
import org.team7.TakeMyPackage.model.DeliveryPackage;

import java.util.Random;

public class ViewAssignmentActivity extends AppCompatActivity {

    private TextView mPackageName;
    private TextView mPackageAddress;
    private TextView mDate;
    private TextView mTime;
    private TextView mRequestContact;
    private TextView mRemarks;
    private TextView mDeliveryContact;
    private String packageName;
    private String requesterAddress;
    private String requesterPhone;
    private String deliveryPhone;
    private String remarks;
    private String date;
    private String time;
    private String requesterId;
    private DeliveryPackage deliveryPackage;
    private Button mAccept;
    private Button mReject;
    private String packageId;
    private FirebaseDatabase database;
    private DatabaseReference myref;
    private String usrid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment);

        mPackageName = findViewById(R.id.package_name_TV);
        mPackageAddress = findViewById(R.id.address_TV);
        mDate = findViewById(R.id.date_TV);
        mTime = findViewById(R.id.time_TV);
        mRequestContact = findViewById(R.id.requester_phone_TV);
        mDeliveryContact = findViewById(R.id.delivery_phone_TV);
        mRemarks = findViewById(R.id.remarks_TV);

        mAccept = findViewById(R.id.accept_assignment);
        mReject = findViewById(R.id.reject_assignment);

        toolbar = findViewById(R.id.create_game_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Assignment Details");

        packageId = getIntent().getStringExtra("this_package_id");

        if (packageId == null) {
            return;
        }

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
                Toast.makeText(ViewAssignmentActivity.this, "Assignment Rejected", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        mAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updatePackageStatus();
            }
        });
    }

    private void updatePackageStatus() {
        myref.child("assigner").setValue(usrid).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Assigner Updated", usrid);
            }
        });
        myref.child("taken").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ViewAssignmentActivity.this, "Assignment Accepted", Toast.LENGTH_LONG).show();
                onBackPressed();
                sendNotification();
                String smsText = String.format(getString(R.string.smsMessage), requesterPhone, requesterAddress, time);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + deliveryPhone)).putExtra("sms_body", smsText));
            }
        });
    }

    private void updatePackage(DataSnapshot dataSnapshot, String s) {
        switch (dataSnapshot.getKey()) {
            case "date": {
                date = dataSnapshot.getValue().toString();
                mDate.setText(date);
                break;
            }
            case "time": {
                time = dataSnapshot.getValue().toString();
                mTime.setText(time);
                break;
            }
            case "packageName": {
                packageName = dataSnapshot.getValue().toString();
                mPackageName.setText(packageName);
                break;
            }
            case "requesterAddress": {
                requesterAddress = dataSnapshot.getValue().toString();
                mPackageAddress.setText(requesterAddress);
                break;
            }
            case "requesterPhone": {
                requesterPhone = dataSnapshot.getValue().toString();
                mRequestContact.setText(requesterPhone);
                break;
            }
            case "remark": {
                remarks = dataSnapshot.getValue().toString();
                mRemarks.setText(remarks);
                break;
            }
            case "requester": {
                requesterId = dataSnapshot.getValue().toString();
                break;
            }
            case "deliveryPhone": {
                deliveryPhone = dataSnapshot.getValue().toString();
                mDeliveryContact.setText(deliveryPhone);
                break;
            }
        }
        deliveryPackage = new DeliveryPackage(packageName, date, time,
                remarks, requesterId, deliveryPhone, requesterPhone, requesterAddress);
    }

    private void sendNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String id = "accept_package_01";
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "TakeMyPackage", NotificationManager.IMPORTANCE_HIGH);


        CharSequence name = getString(R.string.acceptNotificationTitle);

        String description = String.format(getString(R.string.acceptNotificationBody),
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
        Intent resultIntent = new Intent(this, RequestDetailActivity.class);
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
        // notification.
        mNotificationManager.createNotificationChannel(mChannel);
        mNotificationManager.notify(new Random().nextInt(10000), mBuilder.build());
        Log.d("notification", "sent");
    }
}
