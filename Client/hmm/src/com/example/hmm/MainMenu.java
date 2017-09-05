package com.example.hmm;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;


import java.util.StringTokenizer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class MainMenu extends Activity implements
ActionBar.OnNavigationListener{
	private ActionBar actionBar;

	// Title navigation Spinner data
	private ArrayList<SpinnerNavItem> navSpinner;

	// Navigation adapter
	private TitleNavigationAdapter adapter;
	
	public static Hashtable<String, Intent> intentTable = new Hashtable<String, Intent>();
	private int notificationIdOne = 111;
	private NotificationManager myNotificationManager;
	// List view
    private ListView lv;
    private ArrayList<String> chatedUsers =  new ArrayList<String>();
    public static ArrayList<String> unreadMsg =  new ArrayList<String>(); 
    // Listview Adapter
    ArrayAdapter<String> arrayAdapter;
    
    private MenuItem refreshMenuItem;
     
    // Search EditText
    EditText inputSearch;
    Thread listen;
    String inMsg = null;
	boolean listenOn = true;
	public String recvMsg="";
	EditText rcvBox;
	
	private Bitmap[] emoticons;
	private static final int NO_OF_EMOTICONS = 54;
	
	private static int bytesRead;
    private static int current = 0;	
    
    BufferedReader in;
    
   // InputStream inputStream;
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.app.ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
        setTitle("My Chats");
        setContentView(R.layout.my_chats);
        actionBar = getActionBar();
        rcvBox = new EditText(this);
        rcvBox.setText("");
        
        readEmoticons();
        
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);

		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("My Chats", R.drawable.ic_location));
		navSpinner
				.add(new SpinnerNavItem("Online Friends", R.drawable.ic_my_places));
		navSpinner.add(new SpinnerNavItem("My Profile", R.drawable.ic_checkin));
		navSpinner.add(new SpinnerNavItem("Friends Profile", R.drawable.ic_checkin));
		//navSpinner.add(new SpinnerNavItem("Contact List", R.drawable.ic_latitude));

		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);

		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);

		// Changing the action bar icon
		// actionBar.setIcon(R.drawable.ico_actionbar);
		
		
		
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS myChatUsers (me Text, him Text);";
        MainActivity.hmmDB.execSQL(CREATE_TABLE);
        
        //String sql = "INSERT INTO myChatUsers VALUES('lee', 'xn')";
		//hmmDB.execSQL(sql);
        String q="Select him from myChatUsers where me = '"+MainActivity.userName+"'";
		
		Cursor c = MainActivity.hmmDB.rawQuery(q, null);
		
		int cursorSize = c.getCount();
		c.moveToFirst();
		int i=0;
		while(i<cursorSize)
		{
			String uname=c.getString(c.getColumnIndex("him"));
			chatedUsers.add(uname);
			i++;
			c.moveToNext();
		}
		
        
        
        String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
                "iPhone 4S", "Samsung Galaxy Note 800",
                "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        // Adding items to listview
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, chatedUsers);
        lv.setAdapter(arrayAdapter);
        /*if(MainActivity.userName.equals("xn")==true)
        	{
        		arrayAdapter.add("lee");
        	}
        else if(MainActivity.userName.equals("lee")==true)
    	{
    		arrayAdapter.add("xn");
    	}*/
        
        inputSearch.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainMenu.this.arrayAdapter.getFilter().filter(cs);   
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
                String him = chatedUsers.get(position);
                
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), ChatWindow.class);
                intentTable.put(him, i);
                // sending data to new activity
                Log.i("Him ", him);
                i.putExtra("him", him);
                startActivity(i);
               
            }
          });
        
        listen = new Thread(){
	    	 @Override
	    	    public void run() {
	    		 try {
	            		
	        			
	        	        in = new BufferedReader(new InputStreamReader(MainActivity.s.getInputStream()));
	        	        String tag = "nf";
	        	        
	        	        while (listenOn) {

	        	        	inMsg = in.readLine();

	                        if (inMsg != null ) {
	                            
	                        	Log.i("Server ", "sent: " + inMsg);
	                        	//Message msg = new Message();
	                        	//msg.
	                        	//mHandler.sendMessage(msg);
	                        	//addTextLeft(inMsg);
	                        	int x = inMsg.indexOf("#");
	                        	String from = inMsg.substring(0, x);
	                        	String msg = inMsg.substring(x+1);
	                        	String msg2=msg;
	                        	
	                        	String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+MainActivity.userName+"_"+from+"_chat (no INTEGER PRIMARY KEY   AUTOINCREMENT, message Text, side Text, tag Text)";
	               		     	MainActivity.hmmDB.execSQL(CREATE_TABLE);
	                        	
	                        	
	                     		if(msg.equals("file_incoming")==true)
	                     		{
	                     			Log.i("Rcv ", "file Incoming");
	                     			//String from = in,readLine();
	                     			String fileName = in.readLine();
	                     			Log.i("Rcv File Name", fileName);
	                     			recvFilenSave(fileName);
	                     			rcvBox.setText("File: " +fileName);
	                     			Spanned fs =  rcvBox.getText();
	                     			rcvBox.setText("");
	                     			msg="File: " +fileName;
	                     			int st = fileName.lastIndexOf(".");
	                     			String extension = fileName.substring(st);
	                     			Log.i("Rcv File Extension ", extension);
	                     			if(extension.equals(".jpg")==true)
	                     			{
	                     				tag="fi";
	                     			}
	                     			else if(extension.equals(".mp3"))
	                     			{
	                     				tag="fa";
	                     			}
	                     			ChatWindow.rcvTag=tag;
	                     			//in = new BufferedReader(new InputStreamReader(MainActivity.s.getInputStream()));
	                     			
	                     			String sql_insert_left = "INSERT INTO "+MainActivity.userName+"_"+from+"_chat (message, side, tag) VALUES('"+msg+"','l','"+tag+"')";
		                     		MainActivity.hmmDB.execSQL(sql_insert_left);
		                     		intentForward(from, msg, fs);
	                     			
	                     		}
	                     		
	                     		else
	                     		{
	                     			tag="nf";
	                     			ChatWindow.rcvTag="nf";
		                        	while(true)
		                        	{
		                        		x = msg2.indexOf('µ');
		                        		if(x <0){
		                        			Log.i("Rcv ", "no emo");
		                        			recvMsg=msg2;
		                        			rcvBox.append(recvMsg);
		                        			Log.i("Rcv Msg ", recvMsg);
		                        			break;
		                        		}
		                        		else{
		                        			
		                        			recvMsg =  msg2.substring(0, x);
		                        			Log.i("Rcv Msg ", recvMsg);
		                        			rcvBox.append(recvMsg);
		                        			Log.i("Rcv ", "emo found");
		                        			msg2=msg2.substring(x+1);
		                        			int xx = msg2.indexOf('µ');
		                        			String emo = msg2.substring(0, xx);
		                        			Log.i("Emo Name ", emo);
		                        			msg2 = msg2.substring(xx+1);
		                        			putEmoinBox(emo);
		                        			Log.i("Emo Insert", emo+"  inserted");
		                        			
		                        		}
		                        	}
		                        	String finl = rcvBox.getText().toString();
		                        	Spanned finalSpanned =  rcvBox.getText();
		                        	rcvBox.setText("");
		                        	Log.i("Final Msg", finl);
		                        	Log.i("From ", from + "  msg: " + msg);
		                        	
		                        	if(from.equals("onlineUsersFromServer")==true)
		                        	{
		                        		
		                        		OnlineUsers.onlineUsers.add(msg);
		                        	}
		                        	
		                        	else
		                        	{
		                        		intentForward(from, msg, finalSpanned);
		                        		String sql_insert_left = "INSERT INTO "+MainActivity.userName+"_"+from+"_chat (message, side, tag) VALUES('"+msg+"','l','"+tag+"')";
			                     		MainActivity.hmmDB.execSQL(sql_insert_left);
		                        		
		                           	}
		                        	
	                     		}
	                     		
	                        	
	                        }

	                    }
					}catch (Exception e) {
	        	        e.printStackTrace();
	        	    } finally {
	                    //the socket must be closed. It is not possible to reconnect to this socket
	                    // after it is closed, which means a new socket instance has to be created.
	            	
	                }
	    	    }
	     };
	     listen.start();
        
	}
	private void intentForward(String from, String msg, Spanned finalSpanned)
	{
		String q="Select him from myChatUsers where me = '"+MainActivity.userName+"' AND him = '"+from+"'";
		
		Cursor c = MainActivity.hmmDB.rawQuery(q, null);
		
		int cursorSize = c.getCount();
		if(cursorSize==0)
		{
            String chated_user = "INSERT INTO myChatUsers VALUES('"+MainActivity.userName+"','"+from+"')";
    		MainActivity.hmmDB.execSQL(chated_user);
		}
		
		Intent forward = intentTable.get(from);
		if(forward!=null){
			Log.i("Menu ", "intent found");
			ChatWindow.incoming="1";
			ChatWindow.message = msg;
			ChatWindow.rcvMessage=finalSpanned;
		
		}
		else
		{
			displayNotification(from, msg);
			//unreadMsg.add(inMsg);
				                        			
		}

	}
	
	protected void displayNotification(String him, String msg) {

	      // Invoking the default notification service
	      NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);	
	 
	      mBuilder.setContentTitle(him);
	      mBuilder.setContentText(msg);
	      mBuilder.setTicker("New Message Received From "+him);
	      mBuilder.setSmallIcon(R.drawable.hmm_icon);
	      mBuilder.setAutoCancel(true);

	      // Increase notification number every time a new notification arrives 
	      //mBuilder.setNumber(++numMessagesOne);
	      
	      // Creates an explicit intent for an Activity in your app 
	      Intent resultIntent = new Intent(this, ChatWindow.class);
	      resultIntent.putExtra("notificationId", notificationIdOne++);
	      resultIntent.putExtra("him", him);
	      //This ensures that navigating backward from the Activity leads out of the app to Home page
	      TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	      // Adds the back stack for the Intent
	      stackBuilder.addParentStack(this);

	      // Adds the Intent that starts the Activity to the top of the stack
	      stackBuilder.addNextIntent(resultIntent);
	      PendingIntent resultPendingIntent =
	         stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_ONE_SHOT //can only be used once
	         );
	      // start the activity when the user clicks the notification text
	      //Intent i = new Intent(getApplicationContext(), ChatWindow.class);
          intentTable.put(him, resultIntent);
	      mBuilder.setContentIntent(resultPendingIntent);

	      myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	      // pass the Notification object to the system 
	      myNotificationManager.notify(notificationIdOne, mBuilder.build());
	   }
	
	

	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_refresh){
        	refreshMenuItem = item;
			// load the data from server
			new SyncData().execute();		
			return true;
        }
        if (id == R.id.action_settings) {
        	//Intent myIntent = new Intent(MainMenu.this, ContactList.class);
			//myIntent.putExtra("key", value); //Optional parameters
        	//startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	public void onBackPressed() {
	    //Toast.makeText(context,"Thanks for using application!!",Toast.LENGTH_LONG).show()l
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(MainActivity.s.getOutputStream())), true);
			out.println("out$5719$");
	        out.flush();
	        MainActivity.s.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.finish();
		return;
	    
	    
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		// TODO Auto-generated method stub
		Log.e("I clicked: ", Integer.toString(arg0));
		if(arg0 == 3)
		{
			Intent myIntent = new Intent(MainMenu.this, FriendsProfile.class);
			//myIntent.putExtra("key", value); //Optional parameters
        	startActivity(myIntent);
		}
		else if (arg0 ==1)
		{
			Intent myIntent = new Intent(MainMenu.this, OnlineUsers.class);
			//myIntent.putExtra("key", value); //Optional parameters
        	startActivity(myIntent);
		}
		else if(arg0==2)
		{
			Intent myIntent = new Intent(MainMenu.this, MyProfile.class);
			//myIntent.putExtra("key", value); //Optional parameters
        	startActivity(myIntent);
		}
		return false;
	}
	
	private void recvFilenSave(String fileName) throws IOException
	{
		Socket client = new Socket(MainActivity.serverAddr, 4448);
		//Socket client = new Socket("192.168.139.1", 4448);
		InputStream inputStream;
		inputStream=client.getInputStream();
		Log.i("File: ", "File Rcv p1");
		byte[] mybytearray = new byte[10000000];
//       bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Download/Cm-Punk-3.jpg");
	   
	   
			bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
	
	    current = bytesRead;
	    
	    Log.i("File: ", "File Rcv p2  "+Integer.toString(current));
	
	    do {
	      
				bytesRead = inputStream.read(mybytearray, current, (mybytearray.length - current));
			
	        if (bytesRead >= 0) {
	            current += bytesRead;
	        }
	        Log.i("File: ", "File Rcv p while bytesREad: "+Integer.toString(bytesRead));
	    } while (bytesRead > -1);
	    Bitmap bitmap;
	    bitmap = BitmapFactory.decodeByteArray(mybytearray,0,mybytearray.length);
		 
	    // Find the SD Card path
	    File filepath = Environment.getExternalStorageDirectory();
	
	    
	    int start = fileName.lastIndexOf(".");
	    String extension = fileName.substring(start);
	    Log.i("File: ", "File Extension "+extension);
	    File dir = null;
	    int img=0;
	 // Create a new folder in SD Card
	    if(extension.equals(".jpg")==true)
	    {
	    	 dir = new File(filepath.getAbsolutePath()
		            + "/Hmm Share/Hmm Image");
	    	 img=1;
	    }
	    else if(extension.equals(".mp3")==true)
	    {
	    	dir = new File(filepath.getAbsolutePath()
		            + "/Hmm Share/Hmm Mp3");
	    }
	    
	    dir.mkdirs();
	    // Create a name for the saved image
	    File file = new File(dir, fileName);
	    Log.i("File: ", "File Rcv p3");
	
	    // Show a toast message on successful save
	//         Toast.makeText(MainActivity.this, "Image Saved to SD Card",
	//               Toast.LENGTH_SHORT).show();

    	OutputStream output;
    	
        output = new FileOutputStream(file);
        if(img==1){
        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(output);
        bufferedOutputStream.write(mybytearray, 0, current);
        // Compress into png format image from 0% - 100%
    
        bufferedOutputStream.flush();
        //bufferedOutputStream.close(); 
        output.flush();
        //inputStream.close();
        output.close();
        client.close();
        
        Log.i("File: ", "File Rcvd");
        
	}
	
private void readEmoticons () {
		
		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {			
			emoticons[i] = getImage((i+1) + ".png");
		}
		
	}
	
	private Bitmap getImage(String path) {
		AssetManager mngr = getAssets();
		InputStream in = null;
		try {
			in = mngr.open("emoticons/" + path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bitmap temp = BitmapFactory.decodeStream(in, null, null);
		return temp;
	}
	
	public void putEmoinBox(final String index) {
		Log.i("keyClick", index);
		//emoArray[totalEmo++]=index;
		ImageGetter imageGetter = new ImageGetter() {
            public Drawable getDrawable(String source) {    
            	StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        
        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);        
		
		int cursorPosition = rcvBox.getSelectionStart();		
        rcvBox.getText().insert(cursorPosition, cs);
        
	}
	
	private class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);

			refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			
			chatedUsers.clear();
			String q="Select him from myChatUsers where me = '"+MainActivity.userName+"'";
			Cursor c = MainActivity.hmmDB.rawQuery(q, null);
			int cursorSize = c.getCount();
			c.moveToFirst();
			int i=0;
			while(i<cursorSize)
			{
				String uname=c.getString(c.getColumnIndex("him"));
				chatedUsers.add(uname);
				i++;
				c.moveToNext();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
			arrayAdapter = new ArrayAdapter<String>(MainMenu.this, R.layout.list_item, R.id.product_name, chatedUsers);
	        lv.setAdapter(arrayAdapter);
		}
	};
	
	
}
