package com.example.hmm;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;

public class OnlineUsers extends ActionBarActivity{
	
	private ListView lv;
	EditText inputSearch;
	public static ArrayList<String> onlineUsers =  new ArrayList<String>();
    
    // Listview Adapter
    ArrayAdapter<String> arrayAdapter;
	
    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		onlineUsers.clear();
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
		setTitle("Online Users");
		 
	     setContentView(R.layout.online_users);
	     
	     
	     try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(MainActivity.s.getOutputStream())), true);
				out.println("load#online#users");
		        out.flush();
		        
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     	
	     
	        lv = (ListView) findViewById(R.id.list_view2);
	        inputSearch = (EditText) findViewById(R.id.inputSearch2);

	        // Adding items to listview
	        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, onlineUsers);
	        lv.setAdapter(arrayAdapter);
	        //arrayAdapter.add("xn");
	        //onlineUsers.add("xn");
	        
	        inputSearch.addTextChangedListener(new TextWatcher() {
	            
	            @Override
	            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
	                // When user changed the Text
	                OnlineUsers.this.arrayAdapter.getFilter().filter(cs);   
	            }
	             
	            @Override
	            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	                    int arg3) {
	                // TODO Auto-generated method stub
	                 
	            }             
	          
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
	        });
	        
	        lv.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	                 
	                // selected item 
	                String him = onlineUsers.get(position);
	                
	                // Launching new Activity on selecting single List Item
	                Intent i = new Intent(getApplicationContext(), ChatWindow.class);
	                MainMenu.intentTable.put(him, i);
	                // sending data to new activity
	                Log.i("Him ", him);
	                i.putExtra("him", him);
	                startActivity(i);
	                
	                //check and add user to my_chated_user
	                
	                String q="Select him from myChatUsers where me = '"+MainActivity.userName+"' AND him = '"+him+"'";
	        		
	        		Cursor c = MainActivity.hmmDB.rawQuery(q, null);
	        		
	        		int cursorSize = c.getCount();
	        		if(cursorSize==0)
	        		{
		                String chated_user = "INSERT INTO myChatUsers VALUES('"+MainActivity.userName+"','"+him+"')";
		        		MainActivity.hmmDB.execSQL(chated_user);
	        		}
	        		else
	        		{
	        			Log.i("chated users", "already inserted");
	        		}
	               
	            }
	          });
	     
	     
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);

		// Associate searchable configuration with the SearchView
		
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// refresh
			
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
