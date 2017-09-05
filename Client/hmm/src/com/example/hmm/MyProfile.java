package com.example.hmm;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfile extends Activity{
	
	TextView fnTv, unTv, phTv, passTv;
	Button edit;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        android.app.ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
        setTitle("My Profile");
        
        fnTv = (TextView)findViewById(R.id.profFullnm);
        unTv = (TextView)findViewById(R.id.profUn);
        phTv = (TextView)findViewById(R.id.profPhn);
        passTv = (TextView)findViewById(R.id.profPass);
        
        edit = (Button)findViewById(R.id.editProfileButton);
        
        String q="Select * from internalUserInfo Where userName = '"+MainActivity.userName+"'";
		
		Cursor c = MainActivity.hmmDB.rawQuery(q, null);
		c.moveToFirst();
		
			
		String fName = c.getString(c.getColumnIndex("fullName"));
		String phn = c.getString(c.getColumnIndex("phone"));
		String pass = c.getString(c.getColumnIndex("pass"));
		
		Log.i("full name", fName);
		Log.i("user name", MainActivity.userName);
		Log.i("phone", phn);
		Log.i("pass", pass);
		
		fnTv.setText(fName);
		unTv.setText(MainActivity.userName);
		phTv.setText(phn);
		passTv.setText(pass);
		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
		
				//Log.i("DB History", msg);
				//viewHistory(msg, sd, tag);
				
				
			
        
	}

}
