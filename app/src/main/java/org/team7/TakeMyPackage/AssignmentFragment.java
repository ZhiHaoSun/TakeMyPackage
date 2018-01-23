package org.team7.TakeMyPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.team7.TakeMyPackage.model.DeliveryPackage;
import org.team7.sports.R;


/**
 * A simple {@link Fragment} subclass.
 */


public class AssignmentFragment extends Fragment {
    protected Query gameQuery;
    protected FirebaseRecyclerAdapter<DeliveryPackage, AssignmentListViewHolder> gameRecyclerViewAdapter;
    private RecyclerView packageList;
    private DatabaseReference packageDatabase;
    private FirebaseAuth mAuth;
    private View mainView;
    private FirebaseUser currentUse;

    public AssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_assignment, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUse = mAuth.getCurrentUser();

        packageList = mainView.findViewById(R.id.Package_RV);
        packageList.setHasFixedSize(true);
        packageList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        packageDatabase = FirebaseDatabase.getInstance().getReference().child("DeliveryPackage");


        return mainView;
    }


    @Override

    public void onStart() {
        super.onStart();

        //mCreateGame= (Button)GameListViewHolder.gView.findViewById(R.id.create_new_game_B);
        gameQuery = FirebaseDatabase.getInstance().getReference()
                .child("DeliveryPackage")
                .orderByChild("taken")
                .equalTo(false);; //GameThread
        gameQuery.keepSynced(true);

        FirebaseRecyclerOptions packageRecyclerOptions = new FirebaseRecyclerOptions.Builder<DeliveryPackage>()
                .setQuery(gameQuery, DeliveryPackage.class)
                .setLifecycleOwner(this)
                .build();


        gameRecyclerViewAdapter = new FirebaseRecyclerAdapter<DeliveryPackage, AssignmentListViewHolder>(packageRecyclerOptions) {

            public AssignmentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_assignment, parent, false);
                Log.d("ddd", "create adapter");
                return new AssignmentListViewHolder(view);
            }


            protected void onBindViewHolder(final AssignmentListViewHolder holder, final int position, DeliveryPackage model) { // int positon
                holder.setPackageAddress(model.getRequesterAddress());
                holder.setPackageName(model.getPackageName());
                holder.setPackageDate(model.getDate());
                holder.setPackageTime(model.getTime());

                DatabaseReference single_game_reference = packageDatabase.child(getRef(position).getKey());
                Log.d("haha", "item count is " + gameRecyclerViewAdapter.getItemCount());

                single_game_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String packageName = dataSnapshot.child("packageName").getValue().toString();
                        holder.setPackageName(packageName);


                        String packageAddress = dataSnapshot.child("requesterAddress").getValue().toString();
                        holder.setPackageAddress(packageAddress);

                        String packageDate = dataSnapshot.child("date").getValue().toString();
                        holder.setPackageDate(packageDate);

                        String packageTime = dataSnapshot.child("time").getValue().toString();
                        holder.setPackageTime(packageTime);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                holder.gView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent packageDetailIntent = new Intent(getActivity(), ViewAssignmentActivity.class);
                        packageDetailIntent.putExtra("this_package_id", getRef(position).getKey()); //single_game_reference
                        startActivity(packageDetailIntent);
                    }
                });
            }
        };
        Log.d("ddd", "onbind()");
        packageList.setAdapter(gameRecyclerViewAdapter);
    }


    public static class AssignmentListViewHolder extends RecyclerView.ViewHolder {
        public View gView;
        TextView packageNameView;
        TextView packageAddressView;
        TextView packageDateView;
        TextView packageTimeView;


        public AssignmentListViewHolder(View itemView) {
            super(itemView);
            gView = itemView;
            packageNameView = gView.findViewById(R.id.assign_package_single_name);
            packageAddressView = gView.findViewById(R.id.assign_package_single_address);
            packageDateView = gView.findViewById(R.id.assign_package_single_date);
            packageTimeView = gView.findViewById(R.id.assign_package_single_time);
        }

        public void setPackageName(String gName) {
            packageNameView.setText(StringUtils.abbreviate(gName, 26));
        }

        public void setPackageAddress(String gAddress) {
            packageAddressView.setText(StringUtils.abbreviate(gAddress, 26));
        }


        public void setPackageDate(String gDate) {

            packageDateView.setText(StringUtils.abbreviate(gDate, 26));
        }

        public void setPackageTime(String gTime) {

            packageTimeView.setText(StringUtils.abbreviate(gTime, 26));
        }
    }
}
