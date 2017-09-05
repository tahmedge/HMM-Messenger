package com.example.hmm;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.hmm.MainActivity.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends Activity{
	
	EditText fullName, uname, phone, pass;
	Button submit;
	TextView ack;
	ProgressDialog pDialog;
	//String x="2";
	String incoming;
	public Socket s;
	public InetAddress serverAddr;
	final int TCP_SERVER_PORT = 4444;
	public boolean signUpWait = true;
	public String x = "2";
	
	
	String un;
	String ph;
	String pas;
	String fullNm;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		android.app.ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
		 
	     setContentView(R.layout.sign_up);
	     
	     fullName = (EditText)findViewById(R.id.fullName);
	     uname = (EditText)findViewById(R.id.unameField);
	     phone = (EditText)findViewById(R.id.phoneField);
	     pass = (EditText)findViewById(R.id.passField);
	     submit = (Button)findViewById(R.id.submit);
	     ack = (TextView)findViewById(R.id.ack2);
	     ack.setTextColor(Color.RED);
	     
	     String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS internalUserInfo (fullName Text, userName Text, phone Text, pass Text)";
	        MainActivity.hmmDB.execSQL(CREATE_TABLE);
	     
	    /* try {
			//serverAddr= InetAddress.getByName("192.168.139.1");
			serverAddr= InetAddress.getByName(MainActivity.serverAddr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	     
	     submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				un = uname.getText().toString();
				ph = phone.getText().toString();
				pas = pass.getText().toString();
				fullNm = fullName.getText().toString();
				
				int emptOK = checkEmpty(fullNm, un, ph, pas);
				if(emptOK==0)
				{
					ack.setText("Please fill all the fields");
				}
				else
				{
					new Thread(new Runnable() { 
						
			            public void run(){
			            	try {
			            		
			        			s = new Socket(MainActivity.serverAddr, TCP_SERVER_PORT);
			        			Log.i("SignUp", "sock");
			        	        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			        	        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
			        	        //send output msg
			        	       
			        	        out.println("*#hmmSignUp#*");
			        	        out.flush();
			        	        out.println(fullNm);
			        	        out.flush();
			        	        out.println(un);
			        	        out.flush();
			        	        out.println(ph);
			        	        out.flush();
			        	        out.println(pas);
			        	        out.flush();
			        	        //Log.i("TcpClient", "sent: " + user);
			        	        String inMsg = null;
			        	        signUpWait = true;
			        	        //accept server response
			        	        while (signUpWait) {
			
			        	        	inMsg = in.readLine();
			
			                        if (inMsg != null ) {
			                            
			                        	 break;
			                        }
			
			                    }
			        	        if(inMsg.equals("valid")==true)
			        	        {
			        	        		x="1";
			        	        }
			        	        else if(inMsg.equals("invalid1")==true){
			        	        	x="01";
			        	        }
			        	        else if(inMsg.equals("invalid2")==true){
			        	        	x="02";
			        	        }
			        	        else if(inMsg.equals("invalid3")==true){
			        	        	x="03";
			        	        }
			        	        else x="2";
			        	        Log.i("TcpClient x= ", x);
			        	        
			        	        
			        	    } catch (Exception e) {
			        	        e.printStackTrace();
			        	    } finally {
			                    
			                }
			        
			            }
					}).start();
					new SignUpAction().execute();
				}
			}
		});
	     
	}
	private int checkEmpty(String fullNm, String un, String ph, String pas)
	{
		
		//Log.i("uname", un);
		if(fullNm.equals("")==true)
		{
			return 0;
		}
		if(un.equals("")==true)
		{
			return 0;
		}
		if(ph.equals("")==true)
		{
			return 0;
		}
		if(pas.equals("")==true)
		{
			return 0;
		}
		return 1;
	}
	
	
	
	class SignUpAction extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUp.this);
			pDialog.setMessage(Html.fromHtml("<b>HMM SignUp</b><br/>Signing Up..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int time = 150000000;
			int count = 0;
			int b=0;
			x="2";
			while(count<time)
			{
				if(x.equals("1")==true || x.equals("01")==true || x.equals("02")==true || x.equals("03")==true)
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
				
					ack.setTextColor(Color.RED);
					if(x.equals("1")==true)
					{
						fullName.setText("");
						uname.setText("");
						phone.setText("");
						pass.setText("");
						ack.setTextColor(Color.GREEN);
						ack.setText("SignUp Complete!!");
						x="2";
						
						String sql_insert_right = "INSERT INTO internalUserInfo VALUES('"+fullNm+"', '"+un+"', '"+ph+"', '"+pas+"')";
						Log.i("Internal DB", "Info Inserted");
						MainActivity.hmmDB.execSQL(sql_insert_right);
						
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(x.equals("01")==true)
					{
						uname.setText("");
						//phone.setText("");
						pass.setText("");
						ack.setText("UserName already Exists");
						x="2";
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(x.equals("02")==true)
					{
						//uname.setText("");
						phone.setText("");
						pass.setText("");
						ack.setText("Phone No. already Exists");
						x="2";
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(x.equals("03")==true)
					{
						uname.setText("");
						phone.setText("");
						pass.setText("");
						ack.setText("UserName and Phone No. both already Exists");
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
						ack.setText("Connection Time Out");
						signUpWait=false;
						//setContentView(R.layout.chatscreen);
								
					}
				}
			});
		}
		
	}
	
}
