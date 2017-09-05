package com.example.hmm;



import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragChatList extends Fragment implements OnClickListener {
	TextView response;
	TextView stat;
	Button signout;
	Button pass;
	Button prof;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		StrictMode.enableDefaults();
		View rootView = inflater.inflate(R.layout.chatscreen, container, false);
		
		
		Intent intent = getActivity().getIntent();
    			
		return rootView;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
