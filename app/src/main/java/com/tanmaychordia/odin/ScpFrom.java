package com.tanmaychordia.odin;
/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program will demonstrate the file transfer from remote to local
 *   $ CLASSPATH=.:../build javac ScpFrom.java
 *   $ CLASSPATH=.:../build java ScpFrom user@remotehost:file1 file2
 * You will be asked passwd. 
 * If everything works fine, a file 'file1' on 'remotehost' will copied to
 * local 'file1'.
 *
 */

import android.content.Context;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ScpFrom{
    public static final String OUTPUT =  "Odin";



    public static void getReal(Context context){

        FileOutputStream fos=null;
        try{

            String user="root";
            String host ="97.107.138.168";
            String rfile = "/root/NewApp/build/outputs/apk/NewApp-debug.apk";
            String lfile = OUTPUT;

            String prefix=null;
            if(context.getDir(lfile, Context.MODE_APPEND).isDirectory()){
                prefix=lfile+File.separator;
            }
            System.out.println(context.getDir(lfile, 0).createNewFile());
            System.out.println(context.getDir(lfile, 0).exists());
            System.out.println(context.getDir(lfile, 0).canWrite());


            JSch jsch=new JSch();
            Session session=jsch.getSession(user, host, 22);

            // username and password will be given via UserInfo interface.
            UserInfo ui=new MyUserInfo();
            session.setUserInfo(ui);
            System.out.println("About to connect!");
            session.connect();

            // exec 'scp -f rfile' remotely
            String command="scp -f "+ rfile;
            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out=channel.getOutputStream();
            InputStream in=channel.getInputStream();

            channel.connect();

            byte[] buf=new byte[1024];

            // send '\0'
            buf[0]=0; out.write(buf, 0, 1); out.flush();

            while(true){
                int c=checkAck(in);
                if(c!='C'){
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize=0L;
                while(true){
                    if(in.read(buf, 0, 1)<0){
                        // error
                        break;
                    }
                    if(buf[0]==' ')break;
                    filesize=filesize*10L+(long)(buf[0]-'0');
                }

                String file=null;
                for(int i=0;;i++){
                    in.read(buf, i, 1);
                    if(buf[i]==(byte)0x0a){
                        file=new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();

                // read a content of lfile
                System.out.println(context.getFilesDir());
                fos = context.openFileOutput(/*prefix==null ? lfile : prefix+*/file, 0);
                int foo;
                while(true){
                    if(buf.length<filesize) foo=buf.length;
                    else foo=(int)filesize;
                    foo=in.read(buf, 0, foo);
                    if(foo<0){
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize-=foo;
                    if(filesize==0L) break;
                }
                fos.close();
                fos=null;

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();
            }

            session.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println(e.getMessage());
            e.printStackTrace();

            try{if(fos!=null)fos.close();}catch(Exception ee){}
        }
    }

    static int checkAck(InputStream in) throws IOException{
        int b=in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if(b==0) return b;
        if(b==-1) return b;

        if(b==1 || b==2){
            StringBuffer sb=new StringBuffer();
            int c;
            do {
                c=in.read();
                sb.append((char)c);
            }
            while(c!='\n');
            if(b==1){ // error
                System.out.print(sb.toString());
            }
            if(b==2){ // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    public static class MyUserInfo implements UserInfo{

        String pass = "turtleSluts";
        @Override
        public String getPassphrase() {
            return pass;
        }

        @Override
        public String getPassword() {
            return pass;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return true;
        }

        @Override
        public boolean promptYesNo(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {

        }
    }
}