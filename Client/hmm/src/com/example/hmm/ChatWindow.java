package com.example.hmm;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;



import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


import java.util.ArrayList;


import java.util.StringTokenizer;



import com.example.hmm.EmoticonsGridAdapter.KeyClickListener;






import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.Html.ImageGetter;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView.BufferType;

public class ChatWindow extends FragmentActivity implements KeyClickListener{
	TextView view1;
	ScrollView myScroll;
	ImageButton send ,recSound;
	EditText chatWrite;
	String him;
	Thread listen;
	boolean listenOn = true;
	public static String incoming = "0";
	String inMsg = null;
	public static String message;
	public static Spanned rcvMessage;
	ScrollView chtbox;
	LinearLayout container;
	int keyBoardOn=0;
	RelativeLayout panel;
	int start = 0;
	int parentHeight=0;
	public int fromNotification = 0;
	
	public String[] emoArray = new String[100];
	public int totalEmo = 0;
	
	final static int RQS_OPEN_AUDIO_MP3 = 1;
	//Button send2;
	
	private static final int NO_OF_EMOTICONS = 54;
	private static int LOAD_IMAGE_RESULTS = 1;
	//private ListView chatList;
	private View popUpView;
	private ArrayList<Spanned> chats;
	//private ChatListAdapter mAdapter;

	private RelativeLayout emoticonsCover;
	private PopupWindow popupWindow;

	private int keyboardHeight;	
	private EditText content;
	
	private LinearLayout parentLayout;

	private boolean isKeyBoardVisible;
	
	private Bitmap[] emoticons;
	int heightView;
 	int panelHeight;
 	int chatBoxHeight;
 	
 	int emoOn=0;
 	
 	String toSend="";
 	int emoToKey=0;
 	
 	EditText et;
 	
 	public String recvMsg="";
	EditText rcvBox;
	private Socket client;
	private Socket clientF;
 	
	int TvId=1;

	 private FileInputStream fileInputStream;
	 private BufferedInputStream bufferedInputStream;
	  private FileOutputStream fileOutputStream;
		 private BufferedOutputStream bufferedOutputStream;
	 private OutputStream outputStream,output;
	 private InputStream inputStream;
	 
	 public static String rcvTag="nf";
	
	 String sendingFileName;
	 String sendingFileUri;
	 Handler fHandler;
	 
	 int share = 1;
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		android.app.ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#274253")));
		
		et = new EditText(this);
		et.setText("");
		
		 int id = 0;
		 CharSequence s;
	     setContentView(R.layout.chatscreen);
	     Bundle extras = getIntent().getExtras();
			if (extras == null) {
				s = "error";
			}
			else {
				id = extras.getInt("notificationId");
			}
			Log.e("id: ", Integer.toString(id));
	     final Intent i = getIntent();
	        // getting attached intent data
	     him = i.getStringExtra("him");
	     setTitle(him);
	     
	     final Handler mHandler = new Handler() { 

	         public void handleMessage(Message msg) { 
	        	 //addTextLeft(message);
	        	 addTextLeft(rcvMessage);
	         } 
	     };
	     
	     fHandler = new Handler() { 

	         public void handleMessage(Message msg) { 
	        	 //addTextLeft(message);
	        	 addFileRight(sendingFileName);
	         } 
	     };
	     
	     
	     
	     if(id>0)
	     {
	    	 NotificationManager myNotificationManager = 
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
				// remove the notification with the specific id
				myNotificationManager.cancel(id);
							
				
	     }
	     
	     recSound = (ImageButton)findViewById(R.id.recSound);
	     
	     
	     container = (LinearLayout)findViewById(R.id.container);
	     chtbox = (ScrollView)findViewById(R.id.scroll);
	     //container.setBackgroundColor(Color.DKGRAY);
	     //view1 = (TextView)findViewById(R.id.textView1);
	     
	     //view1.setText(him);
	     
	     chatWrite = (EditText) findViewById(R.id.chatWrite);
	     send = (ImageButton) findViewById(R.id.sendButton);
	     //send2 = (Button) findViewById(R.id.sendButton2);
	     myScroll = (ScrollView) findViewById(R.id.scroll);
	     panel = (RelativeLayout) findViewById(R.id.relative);
	     //chtbox.setLayoutParams(new LinearLayout.LayoutParams(
	               // LayoutParams.FILL_PARENT, 605));
	     
	     final View activityRootView = findViewById(R.id.container);
	 	//final View containerView = findViewById(R.id.container);
	 	final View panelView = findViewById(R.id.relative);
	 	
	 	final float popUpheight = getResources().getDimension(
				R.dimen.keyboard_height);
	 	
	 	
			
	     recSound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	 	
	 	
	 	
	     activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
	             new OnGlobalLayoutListener() {
	                 @Override
	                 public void onGlobalLayout() {
	                     heightView = activityRootView.getHeight();
	                     //int widthView = activityRootView.getWidth();
	                      panelHeight = panelView.getHeight();
	                     chatBoxHeight = heightView-panelHeight;
	                     if(start == 0){
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
	                    			 LayoutParams.FILL_PARENT, chatBoxHeight));
	                    	 start++;
	                    	 parentHeight = heightView;
	                     }
	                     Log.i("Panel height", Integer.toString(panelHeight));
	                     
	                     Log.e("height", Integer.toString(heightView));
	                     
	                     Log.e("Parent height", Integer.toString(parentHeight));
	                     
	                     if(heightView<parentHeight && keyBoardOn==0 && emoOn==0)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, heightView-panelHeight));
	                    	 
	                    	 keyBoardOn=1;
	                    	 //Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     else if(heightView==parentHeight && keyBoardOn==1 && emoOn==0)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, heightView-panelHeight));
	                    	 keyBoardOn=0;
	                    	 //Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     else if(heightView==parentHeight && keyBoardOn==0 && emoToKey==1)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
			     		               LayoutParams.FILL_PARENT, (int) (heightView-popUpheight-panelHeight)));
	                    	 keyBoardOn=0;
	                    	 emoToKey=0;
	                    	 //Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     else if(heightView<parentHeight && keyBoardOn==0 && emoOn==1)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, heightView-panelHeight));
	                    	 keyBoardOn=1;
	                    	 emoOn=0;
	                    	 //Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     
	                     myScroll.post(new Runnable() {
	                         @Override
	                         public void run() {
	                             myScroll.fullScroll(ScrollView.FOCUS_DOWN);
	                         }
	                     });
	                     
	                     /*if(heightView==296 && keyBoardOn==0)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, 210));
	                    	 
	                    	 keyBoardOn=1;
	                    	 Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     else if(heightView>400 && keyBoardOn==1)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, 605));
	                    	 keyBoardOn=0;
	                    	 Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }*/
	                     
	                     /*if(heightView==981 && keyBoardOn==0)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, 810));
	                    	 
	                    	 keyBoardOn=1;
	                    	 Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }
	                     else if(heightView>1000 && keyBoardOn==1)
	                     {
	                    	 chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                LayoutParams.FILL_PARENT, 1530));
	                    	 keyBoardOn=0;
	                    	 Log.e("height", Integer.toString(heightView) + "  "+ Integer.toString(keyBoardOn));
	                     }*/
	                     //if (1.0 * widthView / heightView > 3) {
	                         //Make changes for Keyboard not visible
	                    	 //chtbox.setLayoutParams(new LinearLayout.LayoutParams(
		         		                //LayoutParams.FILL_PARENT, 200));
	                    	
	                     //} else {
	                         //Make changes for keyboard visible
	                    	 
	                     //}
	                 }
	             });
	     
	     chatWrite.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				//Log.i("change", arg0);
				
				/*String x = chatWrite.getText().toString();
				if(x!=null || x.equals("")==false){
					int y = x.length();
					char z = x.charAt(y-1);
					int zz = z;
					if(zz >= 32)
						{
							Log.i("change", Character.toString(z));
							
							Log.i("ascii", Integer.toString(zz));
						}65532
				}*/
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
	     
	     
	     /*chatWrite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ScrollView.LayoutParams params = (ScrollView.LayoutParams) chtbox.getLayoutParams();
				// Changes the height and width to the specified *pixels*
				params.height = 200;
				
			}
		});*/
	     
	     send.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String msg = chatWrite.getText().toString();
					msg = msg.replace(" ", "");
					if(msg.equals("")==false)
					{
						
							addTextRight();
							
							chatWrite.setText("");
					}
					
				}
			});
	     
	     /*listen = new Thread(){
	    	 @Override
	    	    public void run() {
	    		 try {
	            		
	        			
	        	        BufferedReader in = new BufferedReader(new InputStreamReader(MainActivity.s.getInputStream()));
	        	        
	        	        while (listenOn) {

	        	        	inMsg = in.readLine();

	                        if (inMsg != null ) {
	                            
	                        	Log.i("Server ", "sent: " + inMsg);
	                        	Message msg = new Message();
	                        	//msg.
	                        	mHandler.sendMessage(msg);
	                        	//addTextLeft(inMsg);
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
	     listen.start();*/
	     
	     new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(listenOn)
					{
						if(incoming.equals("1")==true)
						{
							Message msg = new Message();
							Log.i("Chat from Menu ", message);
							mHandler.sendMessage(msg);
							incoming = "0";
						}
							
						
					}
					
				}
			}).start();
	     
	     //chatList = (ListView) findViewById(R.id.chat_list);		

			parentLayout = (LinearLayout) findViewById(R.id.container);

			emoticonsCover = (RelativeLayout) findViewById(R.id.relative);

			popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);

			// Setting adapter for chat list
			//chats = new ArrayList<Spanned>();
			//mAdapter = new ChatListAdapter(getApplicationContext(), chats);
			//chatList.setAdapter(mAdapter);
			/*chatList.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (popupWindow.isShowing())
						popupWindow.dismiss();	
					return false;
				}
			});*/

			// Defining default height of keyboard which is equal to 230 dip
			
			//changeKeyboardHeight((int) popUpheight);
			
			// Showing and Dismissing pop up on clicking emoticons button
			ImageView emoticonsButton = (ImageView) findViewById(R.id.emo);
			emoticonsButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!popupWindow.isShowing()) {
						//keyBoardOn=1;
						emoOn=1;
						int x=0;
						popupWindow.setHeight((int) (popUpheight));

						if (keyBoardOn==1) {
							chatWrite.clearFocus();
							InputMethodManager imm = (InputMethodManager)getSystemService(
								      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(chatWrite.getWindowToken(), 0);
								keyBoardOn=0;
								emoToKey=1;
							
						} else {
							emoticonsCover.setVisibility(LinearLayout.VISIBLE);
							chtbox.setLayoutParams(new LinearLayout.LayoutParams(
			     		               LayoutParams.FILL_PARENT, (int) (heightView-popUpheight-panelHeight)));
						}
						popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
						
						//chtbox.setLayoutParams(new LinearLayout.LayoutParams(
	     		               //LayoutParams.FILL_PARENT, (int) (heightView-popUpheight-panelHeight)));
						

					} else {
						//emoOn=0;
						popupWindow.dismiss();
					}
					

				}
			});
			
			//Log.i("From " , "  1");
			readEmoticons();
			//Log.i("From " , "  2");
			enablePopUpView();
			//Log.i("From " , "  3");
			//checkKeyboardHeight(parentLayout);
			//Log.i("From " , "  4");
			enableFooterView();
			//Log.i("From " , "  5");
			
			
			 rcvBox = new EditText(this);
		 	
		 	 //loading chat history
		     
		     String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+MainActivity.userName+"_"+him+"_chat (no INTEGER PRIMARY KEY   AUTOINCREMENT, message Text, side Text, tag Text)";
		     MainActivity.hmmDB.execSQL(CREATE_TABLE);
		     
		     String CREATE_TABLE_FILE = "CREATE TABLE IF NOT EXISTS "+MainActivity.userName+"_"+him+"_chat_file (id INTEGER PRIMARY KEY, uri Text)";
		     MainActivity.hmmDB.execSQL(CREATE_TABLE_FILE);
		     
			 String q="Select * from "+MainActivity.userName+"_"+him+"_chat Order By no ASC";
				
				Cursor c = MainActivity.hmmDB.rawQuery(q, null);
				//c.moveToFirst();
				if(c.moveToFirst()==true)
				{
					while(true)
					{
						String msg = c.getString(c.getColumnIndex("message"));
						String sd = c.getString(c.getColumnIndex("side"));
						String tag = c.getString(c.getColumnIndex("tag"));
						//Log.i("DB History", msg);
						viewHistory(msg, sd, tag);
						
						
						if(c.moveToNext()==false)
						{	
								//Log.i("DB History", "loop break");
								break;
						}
					}
					
					myScroll.post(new Runnable() {
			            @Override
			            public void run() {
			                myScroll.fullScroll(ScrollView.FOCUS_DOWN);
			            }
			        });
					
				}
				
				
	     
	    
	     
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if (id == R.id.action_sendImage) {
	        	share=1;
	        	System.out.println("Share Image Pressed");
	        	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_RESULTS);
	            return true;
	        }
	        else if(id == R.id.action_sendFile)
	        {
	        	share=2;
	        	System.out.println("Share File Pressed");
	        	Intent intent = new Intent();
	        	intent.setType("audio/mp3");
	        	intent.setAction(Intent.ACTION_GET_CONTENT);
	        	startActivityForResult(Intent.createChooser(
	        	intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3); 
	        	return true;
	        }
	        return super.onOptionsItemSelected(item);
	}
	 
	 
	 
	 @Override
	    protected void onActivityResult(final int requestCode,final int resultCode, final Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	         
	        // Here we need to check if the activity that was triggers was the Image Gallery.
	        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
	        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
	               
	        
	        Thread background = new Thread(new Runnable() {
	        	
	        	String fileName;
	                public void run() {
	                	
	                    try {
	                    	  if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
	                              // Let's read picked image data - its URI
	                    		  Message msg = new Message();
	      						//Log.i("Chat from Menu ", message);
	      						//Handler fHandler = null;
	      						fHandler.sendMessage(msg);
	                              Uri pickedImage = data.getData();
	                              
	                              // Let's read picked image path using content resolver
	                              String[] filePath = { MediaStore.Images.Media.DATA };
	                              Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
	                              cursor.moveToFirst();
	                           String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
	                           sendingFileUri=imagePath;
	                           int start = imagePath.lastIndexOf("/");
	                           int end = imagePath.lastIndexOf(".");
	                           fileName = imagePath.substring(start+1,end);
	                           String extension = imagePath.substring(end);
	                           sendingFileName=imagePath.substring(start+1);
	                           Log.i("Path: ", imagePath);
	                           
	                           cursor.close();
	                           PrintWriter out = null;
	                          
	                    //	 SendFile sendTask = new SendFile();
	                 		
	                           try {
	                        	   client = new Socket(MainActivity.serverAddr, 4445);
	                        	   //client = new Socket("192.168.139.1", 4445);
	                           	//DataOutputStream out1 = new DataOutputStream(new BufferedOutputStream(MainActivity.s.getOutputStream()));
	                   			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
	                   			//out.println("##sendimage##");
	                   			out.println(MainActivity.userName);
	                   			//out.w
	                   			//out1.write();
	                   	        out.flush();
	                   	        out.println(him);
	                   	        out.flush();
	                   	        out.println(fileName);
	                   	        out.flush();
	                   	        out.println(extension);
	                   	        out.flush();
	                   	        out.close();
	                   	        client.close();
	                   	        
	                   		} catch (IOException e1) {;
	                   			// TODO Auto-generated catch block
	                   			e1.printStackTrace();
	                   		}
	                           
	                   //  	sendTask.execute();
	                    	try {
	                     	
	                     	File file = new File(imagePath);
	            			try {
	             
	            				 // connect to the server
	            				
	            				clientF = new Socket(MainActivity.serverAddr, 4446);
	            				//clientF = new Socket("192.168.139.1", 4446);
	            			     byte[] mybytearray = new byte[(int) file.length()]; //create a byte array to file
	            			 
	            			     fileInputStream = new FileInputStream(file);
	            			     bufferedInputStream = new BufferedInputStream(fileInputStream);  
	            			 
	            			     bufferedInputStream.read(mybytearray, 0, mybytearray.length); //read the file
	            			 
	            			     outputStream = clientF.getOutputStream();
	            			 
	            			     outputStream.write(mybytearray, 0, mybytearray.length); //write file to the output stream byte by byte
	            			     outputStream.flush();
	            			     bufferedInputStream.close();
	            			     outputStream.close();
	            			     //out.close();
	            			     //MainActivity.s= new Socket("10.220.53.216", 4444);
	            			     clientF.close();
	            			     
	            			           Thread.sleep(2000);
	            			           //check();
	                        } catch (InterruptedException e) {
	                            // TODO Auto-generated catch block
	                            e.printStackTrace();
	                        }
	             
	            			} catch (UnknownHostException e) {
	            				e.printStackTrace();
	            			} catch (IOException e) {
	            				e.printStackTrace();
	            			}
	                     	
	                        
	                    } 
	                    }
	                    	  catch (Throwable t) {
	                        // just end the background thread
	                      //  Log.i("Animation", "Thread  exception " + t);
	                    }
	                    
	                }

	                

	                // Define the Handler that receives messages from the thread and update the progress
	                

	            });
	            // Start Thread
	            background.start();  //After call start method thread called run Method
	            
	        } 
	 
	 private void check()
	 {
		 try {
				client = new Socket(MainActivity.serverAddr, 4448);
		
         // Retrieve the image from the res folder
        InputStream inputStream;
				inputStream=client.getInputStream();
			
         byte[] mybytearray = new byte[10000000];
  //       bitmap = BitmapFactory.decodeFile("/storage/emulated/0/Download/Cm-Punk-3.jpg");
        
        
				int bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
		
         int current = bytesRead;
         Log.i("File: ", "Image Rcv p2  "+Integer.toString(current));
  
         do {
           
					bytesRead = inputStream.read(mybytearray, current, (mybytearray.length - current));
				
             if (bytesRead >= 0) {
                 current += bytesRead;
             }
             Log.i("File: ", "Image Rcv p while bytesREad: "+Integer.toString(bytesRead));
         } while (bytesRead > -1);
         Bitmap bitmap;
         bitmap = BitmapFactory.decodeByteArray(mybytearray,0,mybytearray.length);
    	 
         // Find the SD Card path
         File filepath = Environment.getExternalStorageDirectory();

         // Create a new folder in SD Card
         File dir = new File(filepath.getAbsolutePath()
                 + "/Save Image tutorials/");
         dir.mkdirs();
         // Create a name for the saved image
         File file = new File(dir, "myphotocandle4.jpg");

         // Show a toast message on successful save
//         Toast.makeText(MainActivity.this, "Image Saved to SD Card",
  //               Toast.LENGTH_SHORT).show();

         	OutputStream output;
             output = new FileOutputStream(file);
             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
             bufferedOutputStream = new BufferedOutputStream(output);
             bufferedOutputStream.write(mybytearray, 0, current);
             // Compress into png format image from 0% - 100%
         
             bufferedOutputStream.flush();
             bufferedOutputStream.close(); 
             output.flush();
             output.close();
         }

         catch (Exception e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
	 }
	                
	                    	  
	 
	 
	 
	
	private void viewHistory(String msg, String side, String tag)
	{
		
		while(true)
    	{
    		int x = msg.indexOf('µ');
    		if(x <0){
    			//Log.i("Rcv ", "no emo");
    			recvMsg=msg;
    			rcvBox.append(recvMsg);
    			//Log.i("Rcv Msg ", recvMsg);
    			break;
    		}
    		else{
    			
    			recvMsg =  msg.substring(0, x);
    			//Log.i("Rcv Msg ", recvMsg);
    			rcvBox.append(recvMsg);
    			//Log.i("Rcv ", "emo found");
    			msg=msg.substring(x+1);
    			int xx = msg.indexOf('µ');
    			String emo = msg.substring(0, xx);
    			//Log.i("Emo Name ", emo);
    			msg = msg.substring(xx+1);
    			putEmoinBox(emo);
    			//Log.i("Emo Insert", emo+"  inserted");
    			
    		}
    	}
    	String finl = rcvBox.getText().toString();
    	Spanned finalSpanned =  rcvBox.getText();
    	rcvBox.setText("");
    	TextView tv = new TextView(this);
    	tv.setText(finalSpanned);
    	tv.setId(TvId++);
		tv.setTag(tag);
    	
    	//Log.i("Final Msg ", finl);
    	
    	if(side.equals("r")==true)
    	{
    		
    		tv.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				Log.i("TextView Press: ", Integer.toString(arg0.getId()));
    				TextView xxx ;
    				xxx = (TextView)findViewById(arg0.getId());
    				String text = xxx.getText().toString();
    				Log.i("TextView Press: ",text);
    				String tag = xxx.getTag().toString();
    				Log.i("TextView Press Tag: ",tag);
    				
    				if(tag.equals("fi")==true)
    				{
    					int start = text.indexOf(":");
    					//int last = text.indexOf(".jpg");
    					String name = text.substring(start+2);
    					Log.i("TextView Press File Name: ",name);
    					File filepath = Environment.getExternalStorageDirectory();
    					
    					String q="Select uri from "+MainActivity.userName+"_"+him+"_chat_file Where id = "+arg0.getId();
    					
    					Cursor c = MainActivity.hmmDB.rawQuery(q, null);
    					c.moveToFirst();
    					String uri = c.getString(c.getColumnIndex("uri"));
    					
    					
    					
    					File file = new File(uri);
    					//String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
    					Intent intent = new Intent();
    					intent.setAction(android.content.Intent.ACTION_VIEW);
    					intent.setDataAndType(Uri.fromFile(file), "image/*");
    					startActivity(intent);
    				}
    				else if(tag.equals("fa"))
    				{
    					int start = text.indexOf(":");
    					//int last = text.indexOf(".jpg");
    					String name = text.substring(start+2);
    					Log.i("TextView Press File Name: ",name);
    					File filepath = Environment.getExternalStorageDirectory();
    					
    					String q="Select uri from "+MainActivity.userName+"_"+him+"_chat_file Where id = "+arg0.getId();
    					
    					Cursor c = MainActivity.hmmDB.rawQuery(q, null);
    					c.moveToFirst();
    					String uri = c.getString(c.getColumnIndex("uri"));
    					
    				   //File file = new File(filepath.getAbsolutePath()+"/Save Image tutorials/myphotocandle.png");
    					File file = new File(uri);
    				    //String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
    				    Intent intent = new Intent();
    				    intent.setAction(android.content.Intent.ACTION_VIEW);
    				    intent.setDataAndType(Uri.fromFile(file),"audio/mp3");
    				    startActivity(intent);
    				}
    			}
    		});
    		
    		
    		
    		tv.setTextColor(Color.WHITE);	
    		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.textsize));
    		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.rightbubbleblue1));
    		LayoutParams lay = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    		lay.gravity = Gravity.RIGHT;
    		lay.setMargins(200, 5, 0, 5);
    		tv.setLayoutParams(lay);
    		
    		LinearLayout container = (LinearLayout)findViewById(R.id.chatContainer);
            // adds dynamic button to the GUI
            container.addView(tv);
    	}
    	else
    	{
    		
    		tv.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				Log.i("TextView Press: ", Integer.toString(arg0.getId()));
    				TextView xxx ;
    				xxx = (TextView)findViewById(arg0.getId());
    				String text = xxx.getText().toString();
    				Log.i("TextView Press: ",text);
    				String tag = xxx.getTag().toString();
    				Log.i("TextView Press Tag: ",tag);
    				
    				if(tag.equals("fi")==true)
    				{
    					int start = text.indexOf(":");
    					//int last = text.indexOf(".jpg");
    					String name = text.substring(start+2);
    					Log.i("TextView Press File Name: ",name);
    					File filepath = Environment.getExternalStorageDirectory();
    					File file = new File(filepath.getAbsolutePath()+"/Hmm Share/Hmm Image/"+name);
    					//String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
    					Intent intent = new Intent();
    					intent.setAction(android.content.Intent.ACTION_VIEW);
    					intent.setDataAndType(Uri.fromFile(file), "image/*");
    					startActivity(intent);
    				}
    				else if(tag.equals("fa"))
    				{
    					int start = text.indexOf(":");
    					//int last = text.indexOf(".jpg");
    					String name = text.substring(start+2);
    					Log.i("TextView Press File Name: ",name);
    					File filepath = Environment.getExternalStorageDirectory();
    					File file = new File(filepath.getAbsolutePath()+"/Hmm Share/Hmm Mp3/"+name);
    				   //File file = new File(filepath.getAbsolutePath()+"/Save Image tutorials/myphotocandle.png");
    				    //String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
    				    Intent intent = new Intent();
    				    intent.setAction(android.content.Intent.ACTION_VIEW);
    				    intent.setDataAndType(Uri.fromFile(file),"audio/mp3");
    				    startActivity(intent);
    				}
    			}
    		});
    		
    		
    		
    		
    		tv.setTextColor(Color.BLACK);	
    		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.textsize));
    		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.leftbubbleblue4));
    		LayoutParams lay = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    		lay.gravity = Gravity.LEFT;
    		lay.setMargins(0, 5, 200, 5);
    		tv.setLayoutParams(lay);
    		
    		LinearLayout container = (LinearLayout)findViewById(R.id.chatContainer);
            // adds dynamic button to the GUI
            container.addView(tv);
    	}
    	
    	
	}
	
	public void putEmoinBox(final String index) {
		//Log.i("keyClick", index);
		//emoArray[totalEmo++]=index;
		ImageGetter imageGetter = new ImageGetter() {
            public Drawable getDrawable(String source) {    
            	StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        //Log.i("keyClick", "here 1");
        
        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);        
		
		int cursorPosition = rcvBox.getSelectionStart();	
		//Log.i("keyClick", Integer.toString(cursorPosition));
        rcvBox.getText().insert(cursorPosition, cs);
        
	}
	
	protected void addTextRight() {
		// TODO Auto-generated method stub
		int i=0;
		String toSend = "";
		for(i=0;i<totalEmo;i++)
		{
			Log.i("emos", emoArray[i]);
		}
		totalEmo=0;
		TextView tv = new TextView(this);
		
		//et.setText("");
		//tv = (TextView) findViewById(R.id.chatText);
		String msg = chatWrite.getText().toString();
		int len = msg.length();
		int emoN=0;
		for(i=0;i<len;i++)
		{
			char c = msg.charAt(i);
			int ac = c;
			if(ac!=65532)
			{
				toSend+=Character.toString(c);
				et.append(Character.toString(c));
				
			}
			else if(ac==65532)
			{
				toSend+="µ"+emoArray[emoN]+"µ";
				addEmoInEt(emoArray[emoN++]);
			}
		}
		Log.i("toSend", toSend);
		
		Spanned sp = et.getText();
		tv.setText(sp);
		et.setText("");
		
	    
		tv.setTextColor(Color.WHITE);	
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.textsize));
		//tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//tv.setPaddingRelative(5, 5, 5, 5);
		//tv.setPadding(0, 5, 0, 0);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.rightbubbleblue1));
		
		tv.setId(TvId++);
		tv.setTag("nf");
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("TextView Press: ", Integer.toString(arg0.getId()));
				TextView xxx ;
				xxx = (TextView)findViewById(arg0.getId());
				
				Log.i("TextView Press: ",xxx.getText().toString());
				String tag = xxx.getTag().toString();
				Log.i("TextView Press Tag: ",tag);
			}
		});
		
		LayoutParams lay = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lay.setMargins(200, 5, 0, 5);
		lay.gravity = Gravity.RIGHT;
		tv.setLayoutParams(lay);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.chatContainer);
        // adds dynamic button to the GUI
        container.addView(tv);
        //container.
        
        //myScroll.fullScroll(ScrollView.FOCUS_DOWN);
        
        myScroll.post(new Runnable() {
            @Override
            public void run() {
                myScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        String xxx = MainActivity.userName+"#"+him+"$";
        //Spanned encodedMsg =sp;
        
        //String encodedMsg = MainActivity.userName+"#"+him+"$"+sp;
        
        try {
        	//DataOutputStream out1 = new DataOutputStream(new BufferedOutputStream(MainActivity.s.getOutputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(MainActivity.s.getOutputStream())), true);
			out.println(xxx+toSend);
			//out.w
			//out1.write();
	        out.flush();
	        
		} catch (IOException e1) {;
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String sql_insert_right = "INSERT INTO "+MainActivity.userName+"_"+him+"_chat (message, side, tag) VALUES('"+toSend+"','r','nf')";
		MainActivity.hmmDB.execSQL(sql_insert_right);
	
	}
	
	protected void addFileRight(String text) {
		// TODO Auto-generated method stub
		text = "File: "+text;
		TextView tv = new TextView(this);
		
		
		tv.setText(text);
		et.setText("");
		
	    
		tv.setTextColor(Color.WHITE);	
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.textsize));
		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.rightbubbleblue1));
		
		int tvId=TvId;
		
		tv.setId(TvId++);
		
		String tag = null;
        if(share==1)
        {
        	tag="fi";
        }
        else if(share==2)
        {
        	tag="fa";
        }
		
		tv.setTag(tag);
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("TextView Press: ", Integer.toString(arg0.getId()));
				TextView xxx ;
				xxx = (TextView)findViewById(arg0.getId());
				String text = xxx.getText().toString();
				Log.i("TextView Press: ",text);
				String tag = xxx.getTag().toString();
				Log.i("TextView Press Tag: ",tag);
				
				if(tag.equals("fi")==true)
				{
					int start = text.indexOf(":");
					//int last = text.indexOf(".jpg");
					String name = text.substring(start+2);
					Log.i("TextView Press File Name: ",name);
					File filepath = Environment.getExternalStorageDirectory();
					
					String q="Select uri from "+MainActivity.userName+"_"+him+"_chat_file Where id = "+arg0.getId();
					
					Cursor c = MainActivity.hmmDB.rawQuery(q, null);
					c.moveToFirst();
					String uri = c.getString(c.getColumnIndex("uri"));
					
					
					
					File file = new File(uri);
					//String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
				else if(tag.equals("fa"))
				{
					int start = text.indexOf(":");
					//int last = text.indexOf(".jpg");
					String name = text.substring(start+2);
					Log.i("TextView Press File Name: ",name);
					File filepath = Environment.getExternalStorageDirectory();
					
					String q="Select uri from "+MainActivity.userName+"_"+him+"_chat_file Where id = "+arg0.getId();
					
					Cursor c = MainActivity.hmmDB.rawQuery(q, null);
					c.moveToFirst();
					String uri = c.getString(c.getColumnIndex("uri"));
					
				   //File file = new File(filepath.getAbsolutePath()+"/Save Image tutorials/myphotocandle.png");
					File file = new File(uri);
				    //String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
				    Intent intent = new Intent();
				    intent.setAction(android.content.Intent.ACTION_VIEW);
				    intent.setDataAndType(Uri.fromFile(file),"audio/mp3");
				    startActivity(intent);
				}
			}
		});
		
		LayoutParams lay = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lay.gravity = Gravity.RIGHT;
		lay.setMargins(200, 5, 0, 5);
		tv.setLayoutParams(lay);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.chatContainer);
        // adds dynamic button to the GUI
        container.addView(tv);
        //container.
        
        //myScroll.fullScroll(ScrollView.FOCUS_DOWN);
        
        myScroll.post(new Runnable() {
            @Override
            public void run() {
                myScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        //String xxx = MainActivity.userName+"#"+him+"$";
        //Spanned encodedMsg =sp;
        
        //String encodedMsg = MainActivity.userName+"#"+him+"$"+sp;
        
        String sql_insert_right = "INSERT INTO "+MainActivity.userName+"_"+him+"_chat (message, side, tag) VALUES('"+text+"','r','"+tag+"')";
		MainActivity.hmmDB.execSQL(sql_insert_right);
		Log.i("Sending File Uri", sendingFileUri);
		String sql_insert_right_file = "INSERT INTO "+MainActivity.userName+"_"+him+"_chat_file(id, uri) VALUES("+tvId+",'"+sendingFileUri+"')";
		MainActivity.hmmDB.execSQL(sql_insert_right_file);
		
	
	}
	
	protected void addTextLeft(Spanned rcvMessage2) {
		// TODO Auto-generated method stub
		//EditText et = new EditText(this);
		TextView tv = new TextView(this);
		
		tv.setText(rcvMessage2);
		
		tv.setId(TvId++);
		tv.setTag(rcvTag);
				
		tv.setTextColor(Color.BLACK);	
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.textsize));
		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.leftbubbleblue4));
		LayoutParams lay = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lay.gravity = Gravity.LEFT;
		lay.setMargins(0, 5, 200, 5);
		tv.setLayoutParams(lay);
		
		LinearLayout container = (LinearLayout)findViewById(R.id.chatContainer);
        // adds dynamic button to the GUI
        container.addView(tv);
		
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("TextView Press: ", Integer.toString(arg0.getId()));
				TextView xxx ;
				xxx = (TextView)findViewById(arg0.getId());
				String text = xxx.getText().toString();
				Log.i("TextView Press: ",xxx.getText().toString());
				String tag = xxx.getTag().toString();
				Log.i("TextView Press Tag: ",tag);
				if(tag.equals("fi")==true)
				{
					int start = text.indexOf(":");
					//int last = text.indexOf(".jpg");
					String name = text.substring(start+2);
					Log.i("TextView Press File Name: ",name);
					File filepath = Environment.getExternalStorageDirectory();
					File file = new File(filepath.getAbsolutePath()+"/Hmm Share/Hmm Image/"+name);
					//String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "image/*");
					startActivity(intent);
				}
				else if(tag.equals("fa"))
				{
					int start = text.indexOf(":");
					//int last = text.indexOf(".jpg");
					String name = text.substring(start+2);
					Log.i("TextView Press File Name: ",name);
					File filepath = Environment.getExternalStorageDirectory();
					File file = new File(filepath.getAbsolutePath()+"/Hmm Share/Hmm Mp3/"+name);
				   //File file = new File(filepath.getAbsolutePath()+"/Save Image tutorials/myphotocandle.png");
				    //String imageId="/Save Image tutorials/myphotocandle.png";//create file instance
				    Intent intent = new Intent();
				    intent.setAction(android.content.Intent.ACTION_VIEW);
				    intent.setDataAndType(Uri.fromFile(file),"audio/mp3");
				    startActivity(intent);
				}
			}
		});
		
        
        //myScroll.fullScroll(ScrollView.FOCUS_DOWN);
        
        myScroll.post(new Runnable() {
            @Override
            public void run() {
                myScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
	}
	
	@Override
	public void onBackPressed() {
	    //Toast.makeText(context,"Thanks for using application!!",Toast.LENGTH_LONG).show()l
		//listen.stop();
		MainMenu.intentTable.remove(him);
		Log.i("Chat Window: ", " him removed");
		listenOn = false;
		//listen.suspend();
		this.finish();
		return;
	    
	    
	}
	
	
	
	
private void readEmoticons () {
		
		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {			
			emoticons[i] = getImage((i+1) + ".png");
		}
		
	}

	/**
	 * Enabling all content in footer i.e. post window
	 */
	private void enableFooterView() {

		content = (EditText) findViewById(R.id.chatWrite);
		content.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (popupWindow.isShowing()) {
					//emoOn=0;
					popupWindow.dismiss();
					//chtbox.setLayoutParams(new LinearLayout.LayoutParams(
     		                //LayoutParams.FILL_PARENT, heightView-panelHeight));
					
				}
				
			}
		});
		/*final Button postButton = (Button) findViewById(R.id.sendButton);		
		
		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (content.getText().toString().length() > 0) {
					
					Spanned sp = content.getText();					
					chats.add(sp);
					content.setText("");					
					//mAdapter.notifyDataSetChanged();

				}

			}
		});*/
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("keyDown", Integer.toString(keyCode));
		if (popupWindow.isShowing()) {
			//emoOn=0;
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
		//return false;
	}
	
	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		/*parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						
						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);
						
						if (previousHeightDiffrence - heightDifference > 50) {							
							popupWindow.dismiss();
						}
						
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {

							isKeyBoardVisible = false;
							
						}

					}
				});*/

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}

	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(3);
		
		ArrayList<String> paths = new ArrayList<String>();

		for (short i = 1; i <= NO_OF_EMOTICONS; i++) {			
			paths.add(i + ".png");
		}

		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(ChatWindow.this, paths, this);
		pager.setAdapter(adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);
		
		TextView backSpace = (TextView) popUpView.findViewById(R.id.back);
		backSpace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				content.dispatchKeyEvent(event);	
			}
		});

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				//emoticonsCover.setVisibility(LinearLayout.GONE);
				//emoOn=0;
				if(keyBoardOn==0){
					chtbox.setLayoutParams(new LinearLayout.LayoutParams(
 		                LayoutParams.FILL_PARENT, heightView-panelHeight));
				}
			}
		});
	}

	/**
	 * For loading smileys from assets
	 */
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

	
	@Override
	public void keyClickedIndex(final String index) {
		Log.i("keyClick", index);
		emoArray[totalEmo++]=index;
		ImageGetter imageGetter = new ImageGetter() {
            public Drawable getDrawable(String source) {    
            	StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        
        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);        
		
		int cursorPosition = content.getSelectionStart();		
        content.getText().insert(cursorPosition, cs);
        
	}
	
	public void addEmoInEt(final String index) {
		//Log.i("keyClick", index);
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
		
		int cursorPosition = et.getSelectionStart();		
        et.getText().insert(cursorPosition, cs);
        
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
}
