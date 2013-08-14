package com.example.multichat;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.os.StrictMode;

public class ClientConServer {
	public static XMPPConnection connection;
    private Context context;
    private static int PORT = 5222;
    public ClientConServer(Context context){
        this.context = context;
    }
    public boolean login(String a, String p){
        ConnectionConfiguration config = new ConnectionConfiguration("10.101.158.146",PORT);
        config.setSelfSignedCertificateEnabled(true);
        config.setSASLAuthenticationEnabled(false);
        config.setDebuggerEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
    	//Class.forName("org.jivesoftware.smackx.ServiceDiscoveryManager", true, ClientConServer.class.getClassLoader());
        connection = new XMPPConnection(config);
        try{
        	StrictMode.ThreadPolicy policy = new
        			StrictMode.ThreadPolicy.Builder()
        			.permitAll().build();
        			StrictMode.setThreadPolicy(policy);
            connection.connect();
            connection.login(a, p);
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("hehe");
            connection.sendPacket(presence);
            return true;
        }catch(XMPPException e){
            e.printStackTrace();
        }
        return false;
    }

}