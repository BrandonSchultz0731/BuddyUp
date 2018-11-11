package com.example.brandonschultz.buddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    MyCustomAdapter dataAdapter = null;
    static ArrayList<String> selectedCourses;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef,userRef,courseRef;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_layout);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        dbRef = FirebaseDatabase.getInstance().getReference();
        courseRef = dbRef.child("Courses");
        courseRef.setValue("All Courses"); //NEED TO SETVALUE TO SHOW CHILD ON DATABASE

        if(currentUser == null){
            userRef = dbRef.child("Users");
            System.out.println("BLAHSHS HDUSHIDB YOYOYOO HOPE THIS ISNT CALLED");
        }
        else{
            userRef = dbRef.child("Users").child(currentUser.getUid());
        }
                //.child("Brandon");

        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();



    }

    private void displayListView()
    {

        //Array list of countries
        ArrayList<Courses> courseList = new ArrayList<Courses>();
        ArrayList<String> nameOfCourses = new ArrayList<>();

        Courses course = new Courses("...","Calculus 1",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Calculus 2",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Calculus 3",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Physics 1",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Physics 2",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Eng. Entrepreneurship",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Soft. Fund.",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());
        course = new Courses("...","Object Oriented Prog",false);
        courseList.add(course);
        nameOfCourses.add(course.getName());

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,R.layout.state_info, courseList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        courseRef = dbRef.child("Courses");
        courseRef.setValue(nameOfCourses);
//        for(int i = 0; i < nameOfCourses.size(); i++){
//            courseRef.child(nameOfCourses.get(i));
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // When clicked, show a toast with the TextView text
                Courses course = (Courses) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Clicked on Row: " + course.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Courses>
    {

        private ArrayList<Courses> courseList;

        public MyCustomAdapter(Context context, int textViewResourceId,

                               ArrayList<Courses> courseList)
        {
            super(context, textViewResourceId, courseList);
            this.courseList = new ArrayList<Courses>();
            this.courseList.addAll(courseList);
        }

        private class ViewHolder
        {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder holder = null;

            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null)
            {

                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.state_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        CheckBox cb = (CheckBox) v;
                        Courses _state = (Courses) cb.getTag();

                        Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();

                        _state.setSelected(cb.isChecked());
                    }
                });

            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            Courses course = courseList.get(position);

            holder.code.setText(" (" + course.getCode() + ")");
            holder.name.setText(course.getName());
            holder.name.setChecked(course.isSelected());

            holder.name.setTag(course);

            return convertView;
        }

    }

    private void checkButtonClick()
    {

        Button myButton = (Button) findViewById(R.id.findSelected);

        myButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Courses> courseList = dataAdapter.courseList;
                selectedCourses = new ArrayList<>();

                for(int i=0;i<courseList.size();i++)
                {
                    Courses course = courseList.get(i);
                    //get a reference to "Courses" table, add all the courses to "course" list
                    //course.getName() currently holds the current course from checklist

//                    courseRef = dbRef.child("Courses").child(course.getName());
//                    courseRef.setValue("TEST");
                    //HERES THE PROBLEM

                    if(course.isSelected())
                    {
                        //Current course is selected, add it to group
                        responseText.append("\n" + course.getName());
                        selectedCourses.add(course.getName()); //adds the selected course to an ArrayList
                    }
                }
                CreateUserGroups(); //Create Groups Unique to the User


                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();
                SendUserToMainActivity();
            }


        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(CourseActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
    private void CreateUserGroups() {
        //DO NOT WANT TO SET VALUE
        userRef.setValue(selectedCourses);
    }
}
