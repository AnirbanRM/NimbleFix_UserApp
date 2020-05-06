package com.nimblefix.userapp;

import android.app.Application;
import android.content.Context;

import com.nimblefix.ControlMessages.AuthenticationMessage;
import com.nimblefix.MobileClient;

public class ThisApplication extends Application {

    MobileClient mobileClient;
    private Context currentContext=null;

    private String userID=null;
    private String userName = null;

    public String getUserID() {
        return userID;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void connectToServer(final String user){
        this.userID = user;

        Thread connectThd = new Thread(new Runnable() {
            @Override
            public void run() {
                mobileClient = new MobileClient(currentContext);
                mobileClient.connect(user);

                Object o = mobileClient.readNext();

                if(o instanceof AuthenticationMessage){
                    if(currentContext instanceof  SignUp)
                        ((SignUp)currentContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((SignUp)currentContext).showOTPScreen();
                            }
                        });
                }
            }
        });
        connectThd.start();
    }

    public void connectToServer(final String user, final String token){
        this.userID = user;

        Thread connectThd = new Thread(new Runnable() {
            @Override
            public void run() {
                mobileClient = new MobileClient(currentContext);
                mobileClient.connect(user,token);

                Object o = mobileClient.readNext();

                if(o instanceof AuthenticationMessage){
                    if(currentContext instanceof  SignUp)
                        ((SignUp)currentContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((SignUp)currentContext).showOTPScreen();
                            }
                        });

                    if(currentContext instanceof SplashScreen){
                        if(((AuthenticationMessage)o).getMESSAGEBODY().equals("VALID"))
                            ((SplashScreen) currentContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((SplashScreen)currentContext).goToStartScan();
                                }
                            });
                        else if(((AuthenticationMessage)o).getMESSAGEBODY().equals("INVALID")){
                            ((SplashScreen) currentContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((SplashScreen)currentContext).goToSignUp();
                                }
                            });
                        }
                    }
                }
            }
        });
        connectThd.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
