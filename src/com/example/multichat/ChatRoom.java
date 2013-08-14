package com.example.multichat;

import java.io.PushbackInputStream;
import java.util.Date;
import java.util.Iterator;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.os.Build;
import android.provider.ContactsContract.Contacts.Data;

public class ChatRoom extends Activity {
	private final int RECIEVE = 1;
	private MultiUserChat muc;
	private EditText commentEditText;
	private TextView contentTextView;
	private TextView titleTextView;
	Handler handler;
	
    
    class ChatPacketListener implements PacketListener {
        private String _number;
        private Date _lastDate;
        private MultiUserChat _muc;
        private String _roomName;

        public ChatPacketListener(MultiUserChat muc) {
            _number = "0";
            _lastDate = new Date(0);
            _muc = muc;
            _roomName = muc.getRoom();
        }

        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            String from = message.getFrom();
            if (message.getBody() != null) {
                DelayInformation inf = (DelayInformation) message.getExtension(
                        "x", "jabber:x:delay");
                Date sentDate;
                if (inf != null) {
                    sentDate = inf.getStamp();
                } else {
                    sentDate = new Date();
                }
                android.os.Message msg = new android.os.Message();
                msg.what = RECIEVE;
                Bundle bd = new Bundle();
                bd.putString("date", sentDate.toString());
                bd.putString("from", from);
                bd.putString("body", message.getBody());
                msg.setData(bd);
               // System.out.println("ddx on 09:55: " + msg.getData().getString("body"));
                handler.sendMessage(msg);
            }
        }
    }
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SmackAndroid.init(ChatRoom.this);
		setContentView(R.layout.activity_chat_room);
		commentEditText = (EditText)findViewById(R.id.comment);
		contentTextView = (TextView)findViewById(R.id.content);
		contentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		titleTextView = (TextView)findViewById(R.id.room_name);
		//handler = new Handler();
		//TextView room_name = (TextView)findViewById(R.id.room_name);
		handler = new Handler() {

	        @Override
	        public void handleMessage(android.os.Message msg) {
               // System.out.println("ddx on 10:01: " + msg.getData().getString("body"));
                
	            switch (msg.what) {
	            case 1: {
	                String from = msg.getData().getString("from");
	                String result = msg.getData().getString("body");
	                String date = msg.getData().getString("date");
	                contentTextView.setText( contentTextView.getText() + "\n" + from + "    " + date + "\n"+ result);
	            }
	                break;
	            default:
	                break;
	            }
	        }
	    };
		Intent intent = getIntent();
		String message = intent.getStringExtra(QuestionsList.ASK_QUESTION);
		try{
			if(message == null || message.equals(""))
			{
				message = intent.getStringExtra(QuestionsList.ANSWER_QUESTION);
				titleTextView.setText(intent.getStringExtra(QuestionsList.QUESTION));
				DiscussionHistory history = new DiscussionHistory();   
	            history.setMaxStanzas(5);
			    muc = new MultiUserChat(ClientConServer.connection, message);
				String name = ClientConServer.connection.getUser();
				muc.join(name, "123", history,SmackConfiguration.getPacketReplyTimeout());
			}
			else {
				titleTextView.setText(message);
				String roomName = DeleteBlank(message);
				muc = new MultiUserChat(ClientConServer.connection, roomName + "@conference." 
						+ ClientConServer.connection.getServiceName());
				String name = ClientConServer.connection.getUser();
				System.out.println("name:" + name);
				muc.create(name);
				System.out.println("before form!!");
				Form form = muc.getConfigurationForm();
	         	Form submitForm = form.createAnswerForm();
	         	   for(Iterator<FormField> fields = form.getFields(); fields.hasNext();) {   
	                    FormField field = (FormField) fields.next();   
	                    if(!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {  
	                        submitForm.setDefaultAnswer(field.getVariable());   
	                    }   
	                }
	         	   //submitForm.setAnswer("muc#roomconfig_asswordprotectedroom", false);
	             submitForm.setAnswer("muc#roomconfig_roomdesc", message);   
	         	 submitForm.setAnswer("muc#roomconfig_persistentroom", true);
	         	 submitForm.setAnswer("muc#roomconfig_enablelogging", true);
	         	 //submitForm.setAnswer("muc#roomconfig_roomdesc", ClientConServer.connection.getUser() + "'s question");  
		         //muc.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
	         	 muc.sendConfigurationForm(submitForm);
	         	 System.out.println("before join!!!");
	         	 muc.join(name);
	         	 muc.sendMessage("I am coming!");
			}
			findViewById(R.id.send_button).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					String comment = commentEditText.getText().toString();
					try {
						muc.sendMessage(comment);
					} catch (XMPPException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			ChatPacketListener chatListener = new ChatPacketListener(muc);
			muc.addMessageListener(chatListener);
		}catch(XMPPException e){
			e.printStackTrace();
		}
		
		// Show the Up button in the action bar.
		//setupActionBar();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_room, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public String DeleteBlank(String name)
	{
		return name.replace(" ", "");
	}

}
