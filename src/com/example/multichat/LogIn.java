package com.example.multichat;

import java.util.Collection;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends Activity {

   private EditText accountEditText;
   private EditText passwordEditText;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.activity_log_in);
       accountEditText = (EditText)findViewById(R.id.username);
       passwordEditText = (EditText)findViewById(R.id.password);
       findViewById(R.id.login_button).setOnClickListener(new OnClickListener(){
           public void onClick(View v){
        	   SmackAndroid.init(LogIn.this);
               String account = accountEditText.getText().toString();
               String password = passwordEditText.getText().toString();
               if(account.equals("") || password.equals("")){
                   Toast.makeText(LogIn.this,"UserName or PassWord should not be NULL",Toast.LENGTH_SHORT).show();
               }else{
                   ClientConServer ccs = new ClientConServer(LogIn.this);
                   boolean b;
					b = ccs.login(account, password);
				
                   if(b){
                       Toast.makeText(LogIn.this, "Sucessful", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(LogIn.this, QuestionsList.class);
                       startActivity(intent);
                   }else
                   {
                       Toast.makeText(LogIn.this, "Fail", Toast.LENGTH_SHORT).show();
                   }
                  
               }
           }
          
       });
   }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.log_in, menu);
       return true;
   }
   /*public boolean connectServer(){
      
   }*/

}
