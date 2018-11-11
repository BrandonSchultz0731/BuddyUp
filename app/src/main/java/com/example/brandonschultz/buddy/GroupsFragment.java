package com.example.brandonschultz.buddy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private DatabaseReference GroupRef;
    private DatabaseReference UserCourseRef;
    private ArrayList<String> userCourses = new ArrayList<>();

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


//        if(currentUser == null){
//            GroupRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        }
//        else{
//            GroupRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
//
//        }
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        if(currentUser == null){
            UserCourseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        } else {
            UserCourseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        }



        //.child("Brandon");

        InitializeFields();

        InitializeArray();

        RetrieveAndDisplayGroups();


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);

            }
        });


        return groupFragmentView;

    }

    private void InitializeArray() {


        UserCourseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    userCourses.add(((DataSnapshot)iterator.next()).getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        list_view.setAdapter(arrayAdapter);


    }

    private void RetrieveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                //ArrayList<String> selectedCourses = new ArrayList<>();

                System.out.println("YOYOY IYIOYIYOY THE SIZE IS: " + userCourses.size() + "\n");
                //CourseActivity.selectedCourses.size() IS GIVING AN ERROR

                //SUPPOSED TO GO THROUGH EACH CLASS IN COURSES TABLE AND CHECK IF MATCHES USERCOURSES

//                while(iterator.hasNext()){
//                    for(int i = 0; i < userCourses.size(); i++){
//                        if((((DataSnapshot)iterator.next()).getValue().toString()).equals(userCourses.get(i))){
//                            set.add(((DataSnapshot)iterator.next()).getValue().toString());
//                        }
//                    }
//
//                }
                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getValue().toString());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
