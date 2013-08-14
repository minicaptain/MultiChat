package com.example.multichat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class QuestionsList extends Activity {
	public final static String ASK_QUESTION = "com.example.MultiChat.MESSAGE.ASK";
	public final static String ANSWER_QUESTION = "com.example.MultiChat.MESSAGE.ANSWER";
	public final static String QUESTION = "com.example.MultiChat.MESSAGE.QUESTION";
	private EditText rnameEditText;
	private ListView questionsListView;
	private HashMap<String, String> chatRoomMap = new HashMap<String, String>();
	public static MultiUserChat muc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		   SmackAndroid.init(QuestionsList.this);
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_questions_list);
	       rnameEditText = (EditText)findViewById(R.id.question_input);
	       questionsListView = (ListView)findViewById(R.id.questions);
		try{
			
			Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(ClientConServer.connection,"conference." + ClientConServer.connection.getServiceName());
			List<String> questions = new ArrayList<String>();
			if(rooms != null && !rooms.isEmpty())
			{
		       for(HostedRoom entry : rooms){
		    	RoomInfo info = MultiUserChat.getRoomInfo(ClientConServer.connection, entry.getJid());
		    	questions.add(info.getDescription());
		    	chatRoomMap.put(info.getDescription(), entry.getJid());//store them into the hashmap
		    	}
			}
			else {
				questions.add("There");
				questions.add("is");
				questions.add("No");
				questions.add("live");
				questions.add("quesitons!");
			}
			 //show them into the users
		       questionsListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,questions));
		       //add the listener to every question!
		       questionsListView.setOnItemClickListener(new OnItemClickListener(){
		    	   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 
		                    long arg3) {
		    		   String s = questionsListView.getItemAtPosition(arg2).toString();
		    		   Toast.makeText(getApplicationContext(),"你选择了第"+arg2+"个Item，itemTitle的值是："+s, Toast.LENGTH_SHORT).show();
		    		   Intent intent = new Intent(QuestionsList.this, ChatRoom.class);
		    		   intent.putExtra(ANSWER_QUESTION,chatRoomMap.get(s));
		    		   intent.putExtra(QUESTION, s);
		    		   startActivity(intent);
	    	   }
	    		   
	       });
		} catch (XMPPException e1) {
			e1.printStackTrace();
		}
		findViewById(R.id.askQuestion).setOnClickListener(new OnClickListener(){
	           public void onClick(View v){
	        	   String rname = rnameEditText.getText().toString();
	               if(rname.equals("")){
	                   Toast.makeText(QuestionsList.this,"Your question shouldn't be NULL!!",Toast.LENGTH_SHORT).show();
	               }else{
	            	   EditText editText = (EditText)findViewById(R.id.question_input);
	            	   String message = editText.getText().toString();
	                   Intent intent = new Intent(QuestionsList.this, ChatRoom.class);
	                   if(chatRoomMap.containsKey(message))
	                   {
	                	   Toast.makeText(getApplicationContext(), "Your question had been asked by other person,"+
	                   "you can communicate with him", Toast.LENGTH_LONG).show();
	                	   intent.putExtra(ANSWER_QUESTION, chatRoomMap.get(message));
	                	   intent.putExtra(QUESTION, message);
	                   }
	                   else {
		                   intent.putExtra(ASK_QUESTION, message);
					}
	                   startActivity(intent);
	               }
	           }
	       });
	 }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questions_list, menu);
		return true;
	}

}
