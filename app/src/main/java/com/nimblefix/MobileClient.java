package com.nimblefix;

import android.content.Context;

import com.nimblefix.ControlMessages.AuthenticationMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MobileClient {

    final private static String SERVER_IP="10.0.0.4";
    final private static int SERVER_PORT = 2180;

    Context currentContext=null;

    Socket clientSocket;
    ObjectInputStream reader;
    ObjectOutputStream writer;

    public MobileClient(Context context){
        this.currentContext = context;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void connect(String user){
        try {
            clientSocket = new Socket(SERVER_IP,SERVER_PORT);
            reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startAuthenticationProcedure(user,null);
    }

    public void connect(String user, String token){
        try {
            clientSocket = new Socket(SERVER_IP,SERVER_PORT);
            reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        startAuthenticationProcedure(user,token);
    }


    private void startAuthenticationProcedure(String user, String pwd) {
        Object obj = readNext();
        if(obj instanceof AuthenticationMessage){
            AuthenticationMessage authmsg = (AuthenticationMessage) obj;
            if(authmsg.getSource()== AuthenticationMessage.Server&&authmsg.getMessageType()== AuthenticationMessage.Challenge){
                AuthenticationMessage authmsg2 = new AuthenticationMessage(AuthenticationMessage.User, AuthenticationMessage.Response,user,pwd);
                writeObject(authmsg2);
            }
        }
    }

    public void writeObject(Object object){
        try {
            writer.writeUnshared(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object readNext(){
        Object o=null;
        try{
            o = reader.readUnshared();
        }catch (Exception e){}
        return o;
    }
}
