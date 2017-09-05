package com.example.hmm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class TCPClient implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.i("TcpClient ", "I am a thread");
	}
	public void start ()
	   {
	      //System.out.println("Starting " +  threadName );
	      //if (t == null)
	      //{
	         Thread t = new Thread ();
	         t.start ();
	      
	   }
	

	
}
