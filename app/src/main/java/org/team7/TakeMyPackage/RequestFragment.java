package org.team7.TakeMyPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.team7.sports.R;
import org.team7.TakeMyPackage.model.DeliveryPackage;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    protected Query gameQuery;
    protected FirebaseRecyclerAdapter<DeliveryPackage, RequestListViewHolder> gameRecyclerViewAdapter;
    private RecyclerView requestList;
    private DatabaseReference packageDatabase;
    private FirebaseAuth mAuth;
    private View mainView;
    private FirebaseUser currentUser;
    private Button mCreateRequest;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_request, container, false);
        mCreateRequest = mainView.findViewById(R.id.create_new_request);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        requestList = mainView.findViewById(R.id.Request_RV);
        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        packageDatabase = FirebaseDatabase.getInstance().getReference().child("DeliveryPackage");
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        gameQuery = FirebaseDatabase.getInstance().getReference()
                .child("DeliveryPackage")
                .orderByChild("requester")
                .equalTo(currentUser.getUid()); //DeliveryPackage
        gameQuery.keepSynced(true);

        mCreateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent create_game_intent = new Intent(getActivity(), CreateRequestActivity.class);
                startActivity(create_game_intent);
            }
        });

        FirebaseRecyclerOptions packageRecyclerOptions = new FirebaseRecyclerOptions.Builder<DeliveryPackage>()
                .setQuery(gameQuery, DeliveryPackage.class)
                .setLifecycleOwner(this)
                .build();


        gameRecyclerViewAdapter = new FirebaseRecyclerAdapter<DeliveryPackage, RequestListViewHolder>(packageRecyclerOptions) {

            public RequestListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_request, parent, false);
                Log.d("ddd", "create adapter");
                return new RequestListViewHolder(view);
            }


            protected void onBindViewHolder(final RequestListViewHolder holder, final int position, DeliveryPackage model) { // int positon
                holder.setPackageName(model.getPackageName());
                holder.setTaken(model.isTaken());
                holder.setPackageDate(model.getDate());
                holder.setPackageTime(model.getTime());

                DatabaseReference single_game_reference = packageDatabase.child(getRef(position).getKey());
                Log.d("haha", "item count is " + gameRecyclerViewAdapter.getItemCount());

                single_game_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String packageName = dataSnapshot.child("packageName").getValue().toString();
                        holder.setPackageName(packageName);


                        boolean taken = (boolean) dataSnapshot.child("taken").getValue();
                        holder.setTaken(taken);
                        if (taken) {
                            holder.gView.setBackgroundColor(Color.parseColor("#ACFA58"));
                            holder.gView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent requestDetailIntent = new Intent(getActivity(), RequestDetailActivity.class);
                                    requestDetailIntent.putExtra("this_package_id", getRef(position).getKey()); //single_game_reference
                                    startActivity(requestDetailIntent);
                                }
                            });
                        }

                        String packageDate = dataSnapshot.child("date").getValue().toString();
                        holder.setPackageDate(packageDate);

                        String packageTime = dataSnapshot.child("time").getValue().toString();
                        holder.setPackageTime(packageTime);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        Log.d("ddd", "onbind()");
        requestList.setAdapter(gameRecyclerViewAdapter);
    }


    public static class RequestListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View gView;
        TextView packageNameView;
        TextView packageTakenView;
        TextView packageDateView;
        TextView packageTimeView;
        private boolean requestTaken = false;


        public RequestListViewHolder(View itemView) {
            super(itemView);
            gView = itemView;
            packageNameView = gView.findViewById(R.id.package_single_name);
            packageTakenView = gView.findViewById(R.id.package_taken);
            packageDateView = gView.findViewById(R.id.package_single_date);
            packageTimeView = gView.findViewById(R.id.package_single_time);
        }

        public void setPackageName(String gName) {

            packageNameView.setText(StringUtils.abbreviate(gName, 26));
        }

        public void setTaken(boolean taken) {
            if (taken) {
                requestTaken = taken;
                packageTakenView.setText(StringUtils.abbreviate("Taken", 26));
            } else {
                packageTakenView.setText(StringUtils.abbreviate("No", 26));
            }
        }

        public void setPackageDate(String gDate) {
            packageDateView.setText(StringUtils.abbreviate(gDate, 26));
        }

        public void setPackageTime(String gTime) {
            packageTimeView.setText(StringUtils.abbreviate(gTime, 26));
        }

        @Override
        public void onClick(View view) {
            if (requestTaken) {

            }
        }
    }

}
