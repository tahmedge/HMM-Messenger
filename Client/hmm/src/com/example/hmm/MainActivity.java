package com.example.hmm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	EditText userT, passT;
	Button login;
	static String x="2";
	TextView invalid;
	TextView signUp;
	CheckBox remember;
	//private OnMessageReceived mMessageListener = null;
	public static Socket s;
	public static final int TCP_SERVER_PORT = 4444;
	public static InetAddress serverAddr;
	public static String userName;
	public static SQLiteDatabase hmmDB;
	Thread listen;
	ProgressDialog pDialog;
	public boolean loginWait =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.app.ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(28, 43, 34)));
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
        setTitle("HMM");
        setContentView(R.layout.activity_main);
        
        userT = (EditText) findViewById(R.id.userT);
        passT = (EditText) findViewById(R.id.passT);
        login = (Button) findViewById(R.id.loginButton);
        invalid = (TextView) findViewById(R.id.invalidView);
        signUp = (TextView) findViewById(R.id.signUp);
        remember = (CheckBox)findViewById(R.id.remember);
        signUp.setOnClickListener(this);
        
        invalid.setTextColor(Color.RED);
        invalid.setText("");
        
        login.setOnClickListener(this);
        
        hmmDB=this.openOrCreateDatabase("hmmDB",MODE_WORLD_WRITEABLE , null);
		hmmDB.setVersion(2);
		hmmDB.setLocale(Locale.getDefault());
		hmmDB.setLockingEnabled(true);
		
		//String sql = "DROP TABLE remember";
		//hmmDB.execSQL(sql);
		
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS remember (no Text, remember_me Text, user Text, pass Text)";
        hmmDB.execSQL(CREATE_TABLE);
        
        String q="Select * from remember where no= '1'";
		
		Cursor c = hmmDB.rawQuery(q, null);
		int cSize = c.getCount();
			
		if(cSize==1)
		{
			Log.i("Remember", "found");
			c.moveToFirst();
			String usr = c.getString(c.getColumnIndex("user"));
			String pas = c.getString(c.getColumnIndex("pass"));
			remember.setChecked(true);
			userT.setText(usr);
			passT.setText(pas);
		}
		
        
        try {
        	//serverAddr= InetAddress.getByName("192.168.139.1");
        	serverAddr= InetAddress.getByName("10.220.53.216");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.signUp)
		{
			Intent myIntent = new Intent(MainActivity.this, SignUp.class);
			//myIntent.putExtra("key", value); //Optional parameters
			startActivity(myIntent);
		}
		else if(arg0.getId() == R.id.loginButton)		
		{
			if(remember.isChecked()==true)
			{
				String sql1 = "DELETE from remember";
				hmmDB.execSQL(sql1);
		        
				String u = userT.getText().toString();
				String p = passT.getText().toString();
				String sql = "INSERT INTO remember VALUES('1', '1', '"+u+"', '"+p+"')";
				hmmDB.execSQL(sql);
				Log.i("Remember", "data inserted");
			}
			else
			{
				String sql = "DELETE from remember";
				hmmDB.execSQL(sql);
				Log.i("Remember", "table dropped");
			}
			new Thread(new Runnable() { 
		
	            public void run(){
	            	try {
	            		
	        			s = new Socket(serverAddr, TCP_SERVER_PORT);
	        	        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        	        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
	        	        //send output msg
	        	        String user = userT.getText().toString(); 
	        	        String pass = passT.getText().toString(); 
	        	        Log.e("User", user);
	        	        userName = user;
	        	        out.println("*#hmmlogin#*");
	        	        out.flush();
	        	        out.println(user);
	        	        out.flush();
	        	        out.println(pass);
	        	        out.flush();
	        	        Log.i("TcpClient", "sent: " + user);
	        	        String inMsg = null;
	        	        loginWait = true;
	        	        //accept server response
	        	        while (loginWait) {
	
	        	        	inMsg = in.readLine();
	
	                        if (inMsg != null ) {
	                            
	                        	 break;
	                        }
	
	                    }
	        	        
	        	        //String inMsg = in.readLine();
	        	        //Log.i("TcpClient", "received: " + inMsg);
	        	        //Toast.makeText(getApplicationContext(),inMsg, Toast.LENGTH_LONG).show();
	        	        //close connection
	        	       // setContentView(R.layout.);
	        	        //setContentView(R.layout.chatscreen);
	        	        if(inMsg.equals("valid")==true)
	        	        {
	        	        		x="1";
	        	        }
	        	        else if(inMsg.equals("invalid")==true){
	        	        	x="0";
	        	        }
	        	        else x="2";
	        	        Log.i("TcpClient x= ", x);
	        	        
	        	        
	        	    } catch (Exception e) {
	        	        e.printStackTrace();
	        	    } finally {
	                    //the socket must be closed. It is not possible to reconnect to this socket
	                    // after it is closed, which means a new socket instance has to be created.
	            	
	                }
	        
	            }
			}).start();
			//Log.i("TcpClient After Thread x = ", x);
			new Login().execute();
			
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
	    //Toast.makeText(context,"Thanks for using application!!",Toast.LENGTH_LONG).show()l
		
		System.exit(1);
		return;
	    
	}
	
	class Login extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(Html.fromHtml("<b>HMM Login</b><br/>Loging In..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stu
			int time = 150000000;
			int count = 0;
			int b=0;
			x="2";
			while(count<time)
			{
				if(x.equals("1")==true || x.equals("0")==true)
				{
					b=1;
					break;
				}
				count++;
			}
			if(b==0)
			{
				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					
					if(x.equals("1")==true)
					{
						Log.i("TcpClient ", "I am a thread2");
						Intent myIntent = new Intent(MainActivity.this, MainMenu.class);
						//myIntent.putExtra("key", value); //Optional parameters
						startActivity(myIntent);
						x="2";
						invalid.setText("");
						//setContentView(R.layout.chatscreen);
								
					}
					else if(x.equals("0")==true)
					{
						Log.i("TcpClient ", "I am a thread2");
						//setContentView(R.layout.fail);
											        
					    userT.setText("");
						invalid.setText("Invalid ID");
						x="2";
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					        
					}
					
					else if(x.equals("2")==true)
					{
						//Log.i("TcpClient ", "I am a thread2");
						//setContentView(R.layout.fail);
											        
					    //userT.setText("");
						invalid.setText("Connection Time Out");
						loginWait=false;
						//x="2";
						  
					}
				}
					
				
				
			});
		}
	}

}
