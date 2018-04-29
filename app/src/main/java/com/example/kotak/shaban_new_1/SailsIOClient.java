package com.example.kotak.shaban_new_1;

/*
 * Author: Naoyuki Kanezawa
 * Date: 2016
 * Availability: https://github.com/socketio/socket.io-client-java
*/
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SailsIOClient {

    private String versionString;
    private String groupId;
    SailsSocket socket;

    private class ConnectionMetaData{
        final static String VERSION = "__sails_io_sdk_version";
        final static String PLATFORM = "__sails_io_sdk_platform";
        final static String LANGUAGE = "__sails_io_sdk_language";
    }

    private class SdkInfo{
        final static String VERSION = "0.11.0";
        final static String PLATFORM = "android";
        final static String LANGUAGE = "Java";
    }

    SailsIOClient(String url, String groupId){

        this.groupId = groupId;

        try{
            JSONObject oUrl = new JSONObject();
            oUrl.put("url",url);
            Log.d("URL", url);
            this.socket = new SailsSocket(oUrl);
            socket.Connect();
        }catch(JSONException e){
            e.printStackTrace();
        }


        GenerateVersionString(SdkInfo.VERSION, SdkInfo.PLATFORM, SdkInfo.LANGUAGE);
    }


    SailsIOClient(String url, String... sdkInfo){
        try{
            JSONObject oUrl = new JSONObject();
            oUrl.put("url",url);
            this.socket = new SailsSocket(oUrl);
            socket.Connect();
        }catch(JSONException e){
            e.printStackTrace();
        }

        GenerateVersionString(sdkInfo[0], sdkInfo[1], sdkInfo[2]);
    }


    private void GenerateVersionString(String... sdkInfo){
        this.versionString = ConnectionMetaData.VERSION + "=" + sdkInfo[0] + "&" +
                ConnectionMetaData.PLATFORM + "=" + sdkInfo[1] + "&" +
                ConnectionMetaData.LANGUAGE + "=" + sdkInfo[2];
    }

    private static void RunRequestQueue(SailsSocket socket){
        List<Request> queue = socket.requestQueue;

        if(queue == null){
            return;
        }
        for(int i = 0; i < queue.size();i++){
            EmitRequest(socket.rawSocket,queue.get(i));
        }

        queue = null;

    }

    private static void EmitRequest(Socket rawSocket,Request request){
        try{
            if(rawSocket == null){
                throw new Exception("Raw Socket connection missing. Failed to emit request.");
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        try {
            String sailsEndpoint = request.context.getString("method");
            rawSocket.emit(sailsEndpoint,request.context,request.cb);
        }catch(JSONException e){
            e.printStackTrace();
        }



    }


    //Could be replaced by a Map ?
    private class Request{
        JSONObject context;
        Ack cb;

        Request(JSONObject ctx, Ack cb){
            this.context = ctx;
            this.cb = cb;
        }
    }

    class SailsSocket{

        private List<Request> requestQueue = new ArrayList<>();
        private Map<String,List<Emitter.Listener>> eventQueue = new HashMap<String,List<Emitter.Listener>>();
        private String url;
        private Socket rawSocket;
        private boolean ranRequestQueue;

        SailsSocket(JSONObject opts){
            try {
                this.url = opts.getString("url");
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        void Connect(){
            //@TODO: Check for url protocol and according assign the target port.
            Log.d("connecting","connecting");
            //IO.Options sOpts = new IO.Options();
            //sOpts.query = versionString;
            try{
                rawSocket = IO.socket(this.url+"?__sails_io_sdk_version=0.11.0");
            }catch (URISyntaxException e){
                e.printStackTrace();
            }


            this.on("connect",new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    //JSONObject obj = (JSONObject) args[0];
                    post("/messages?group=" + groupId, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.d("Acknowledge connection", " to groups");
                        }
                    });
                    Log.d("Socket","Connection Established.");
                }
            });

            this.on("disconnect",new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    //JSONObject obj = (JSONObject) args[0];
                    Log.d("Socket","Disconnected.");
                }
            });

            this.on("reconnecting", new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    //JSONObject obj = (JSONObject) args[0];
                    Log.d("Socket","Reconnecting to socket.");
                }
            });

            this.on("reconnect",new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    //JSONObject obj = (JSONObject) args[0];
                    Log.d("Socket","Reconnection Established.");
                }
            });

            this.on("error",new Emitter.Listener(){
                @Override
                public void call(Object... args){
                    //JSONObject obj = (JSONObject) args[0];
                    Log.d("Socket","Connection Error.");
                }
            });

            rawSocket.connect();
            this.Replay();
        }

        public void Disconnect(){
            try{
                if(rawSocket == null){
                    throw new Exception("Cannot disconnect. Already disconnected.");
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            this.rawSocket.disconnect();

        }

        public boolean isConnected(){
            return this.rawSocket.connected();
        }

        public void Replay(){
            for (String evName : eventQueue.keySet()){
                for (Emitter.Listener fn : eventQueue.get(evName)){
                    this.rawSocket.on(evName,fn);
                }
            }

            if(!this.isConnected()){
                ranRequestQueue = false;

                this.rawSocket.on("connect",new Emitter.Listener(){
                    @Override
                    public void call(Object... args) {
                        RunRequestQueue(SailsSocket.this);
                        ranRequestQueue = true;
                    }
                });
            }
            else{
                RunRequestQueue(this);
            }
            //return this;
        }

        public void on(String evName, Emitter.Listener fn){
            if(this.rawSocket != null){
                this.rawSocket.on(evName,fn);
                return;
                //return this;
            }

            if(!this.eventQueue.containsKey(evName)){
                List<Emitter.Listener> fnL = new ArrayList<>();
                fnL.add(fn);
                this.eventQueue.put(evName,fnL);
            }
            else{
                this.eventQueue.get(evName).add(fn);
            }

            return;

        }

        public void off(String evName, Emitter.Listener fn){
            if(this.rawSocket != null){
                this.rawSocket.off(evName,fn);
                return;
                //return this;
            }

            if(this.eventQueue.containsKey(evName) && this.eventQueue.get(evName).indexOf(fn) > -1){
                this.eventQueue.get(evName).remove(fn);
            }

            return;

        }
        public void removeAllListeners(){
            if(this.rawSocket != null){
                this.rawSocket.off();
                return;
                //return this;
            }

            this.eventQueue.clear();
            return;
        }

        public void get(String url, JSONObject data, Ack cb){
            Log.d("get","get request");
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "get");
                gObj.put("data", data);
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }
        public void get(String url, Ack cb){
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "get");
                gObj.put("data", new JSONObject());
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj, cb);
        }


        public void post(String url, JSONObject data, Ack cb){
            Log.d("post"," request");
            JSONObject gObj = new JSONObject();
            try {
                System.out.println(data.get("group"));
                gObj.put("method", "post");
               // Log.d("dataMsg:",data.toString());
                gObj.put("data", data);
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }

        public void post(String url, Ack cb){
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "post");
                gObj.put("data", new JSONObject());
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }

        public void put(String url, JSONObject data, Ack cb){
            Log.d("put","request");
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "put");
                gObj.put("data", data);
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }
        public void put(String url, Ack cb){
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "put");
                gObj.put("data", new JSONObject());
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }

        public void delete(String url, JSONObject data, Ack cb){
            Log.d("delete","request");
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "delete");
                gObj.put("data", data);
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }
        public void delete(String url, Ack cb){
            JSONObject gObj = new JSONObject();
            try {
                gObj.put("method", "delete");
                gObj.put("data", new JSONObject());
                gObj.put("url", url);
            }catch (JSONException e){
                e.printStackTrace();
            }
            this.request(gObj,cb);
        }
        private void request(JSONObject options, Ack cb){
            try {
                options.put("headers", new JSONObject());
            }catch (JSONException e){
                e.printStackTrace();
            }
            Request req= new Request(options,cb);

            if(!this.isConnected()){
                requestQueue.add(req);
                return;
            }

            EmitRequest(this.rawSocket,req);

        }

    }





}

