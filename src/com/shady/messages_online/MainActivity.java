package com.shady.messages_online;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

	  private MyHTTPD server;
		List<String> where = new ArrayList<String>();
		List<String> display = new ArrayList<String>();
		List<String> messages = new ArrayList<String>();
		String filename="previouscontacts";
		String filename1="lasttime";
		//List<Integer> from = new ArrayList<Integer>();
		//String returnstate="return oldHtml.replace(/\\b(\\w+?)\\b/g, \'<span class=\"word\">$1</span>\')"+"});";
		String contacttosend="";
		
		String searchbox="var $cells = $(\"td\");"+
				"$(\"#search\").keyup(function() {\n"+
		        "var val = $.trim(this.value).toUpperCase();\n"+
		        "if (val === \"\")\n"+
		        "    $cells.parent().show();\n"+
		        "else {\n"+
		            "$cells.parent().hide();\n"+
		            "$cells.filter(function() {\n"+
		            "    return -1 != $(this).text().toUpperCase().indexOf(val);\n"+
		            "}).parent().show();\n"+
		        "}\n"+
		    "});\n";
		
		String sendfunc="$(\"#send\").click(\n"
				+ "function(){\n"
				+"$.ajax({\n"
				+"type: 'POST',\n"
				+"url: 'send',\n"
    			+"data: {name: $(\"#messagebox\").val()}, \n"
    			+"success: function(data) { alert('Message from server: ' + data); },\n"
				+"});\n"
				+ "});\n";
		
		String script1="<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>"+
		"<script>"+
		"$(document).ready(function(){"+
		  "$(\"td\").click(function(){"+
		  "$('#content').html(\"\");"+
		  	"$('#loadingDiv').show();"+
		    "$.get(\"message.\"+this.id,"+
		    "function(data,status){"+
		    "$('#content').html(data);"+
		    "var objDiv = document.getElementById(\"content\");objDiv.scrollTop = objDiv.scrollHeight;"+ 
		    "$('#loadingDiv').hide();"+
		    "});"+
		  "});"+
		    searchbox+
		    sendfunc+
		"});"+
		"</script>";
		
		String onload="<script>window.onload=function(){"
	        	        +"$('#loadingDiv').hide();"
	        	        +"$.get(\"message.1\","+
	        		    "function(data,status){"+
	        		    "$('#content').html(data);"+
	        		    "var objDiv = document.getElementById(\"content\");objDiv.scrollTop = objDiv.scrollHeight;"+ 
	        		    "$('#loadingDiv').hide();"+
	        		    "});"+
	        	        "};"
	        	        + "</script>";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}	
	}
	@Override
	protected void onResume(){
		super.onResume();
		try {
			server=new MyHTTPD();
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	public class MyHTTPD extends NanoHTTPD {
	    public MyHTTPD() {
	        super(8080);
	    }

	    @Override public Response serve(IHTTPSession session) {
	        Method method = session.getMethod();
	        String uri = session.getUri();
	        System.out.println(method + " '" + uri + "' ");
	        String msg = null;
	        
	        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
			int b=1;
			where.add("");

			if(uri.contains("index.html")){
				msg="<html><head>"+
	        			script1+
	        			onload+
	        			"</head><body><h1>Hello server</h1>\n";
		        msg+="<div id=\"container\" style=\"width:800px\"><div id=\"header\" style=\"background-color:#FFA500;\"><h1 style=\"margin-bottom:0;\">Main Title of Web Page</h1></div>";
		        Log.d("com.shady", Long.toString(System.currentTimeMillis()));
			/*if(cursor.moveToFirst()){
				//Log.d("com.shady",cursor.getLong(cursor.getColumnIndex("date"));
				do{
					int i=0;
					long contactId = cursor.getLong(3);
	            	String contactId_string = String.valueOf(contactId);
	        		String address = cursor.getString(cursor.getColumnIndex("address"));
	            	boolean add=true;
	            	for(i=0;i<b;i++){
	            	if(where.get(i).equals(address)){
	            		add=false;
	            		break;
	            	}}
	            	if(add){
	           		Uri uri1 = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactId_string));
	            	String[] projection = new String[]{ PhoneLookup.DISPLAY_NAME,PhoneLookup.PHOTO_URI};
	            	Cursor cursor1 = getContentResolver().query(uri1, projection,null,null,null);
	            	cursor1.moveToFirst();
	            	where.add(address);

	            	try{
	            		display.add(cursor1.getString(0));

	            	}catch(Exception e){
	            		display.add(address);
	            	}
	            	
	            	b++;
	            	cursor1.close();
	            	}
				}while(cursor.moveToNext());
			}*/
	    	cachedata();

			Log.d("com.shady", Long.toString(System.currentTimeMillis()));
			msg+="<label>Search for contacts: <input id=\"search\" type=\"text\"></label>";
			msg+="<div id=\"menu\" style=\"height:400px;width:300px;float:left;overflow:auto;\"><br><table>";	        
	        for (int cnt=0;cnt<display.size();cnt++)
				msg+="<tr>\n<td id=\""+(cnt+1)+"\">"+display.get(cnt)+"</td>\n</tr>\n";
	        msg+="</table></div>";	
	        msg+="<div id=\"content\" style=\"height:400px;width:500px;float:right;overflow:auto;\">";
			}
        	Integer val=0;
        	if(uri.contains("message")){
        		val=Integer.parseInt(uri.substring(9, uri.length()));
        		contacttosend=where.get(val);
	    	if(cursor.moveToLast()){
				do{
	          		String address = cursor.getString(cursor.getColumnIndex("address"));
	            	String body = cursor.getString(cursor.getColumnIndex("body"));
	            	String read=cursor.getString(cursor.getColumnIndex("read"));
	            	String from;
	            	try{
	            		String a=cursor.getString(cursor.getColumnIndex("service_center"));
	                	if(a.equalsIgnoreCase(null)){
	                		from="right";
	                	}else{
	                		from="left";
	                	}
	            	}catch(Exception e){
	            		from="right";
	            	}
	            		if(address.equals(where.get(val))){
	            		messages.add(body);
	            		if(msg==null)
	                	msg="<p align=\""+from+"\">"+body+"</p>";
	            		else
	            			msg+="<p align=\""+from+"\">"+body+"</p>";
	            		if(read.equals("0")){
	                		String asa=cursor.getString(0);
	                        ContentValues values = new ContentValues();
	                        values.put("READ", "1");
	                        String selection = null;
	                        String[] selectionArgs = null;          
	                        getContentResolver().update(
	                                Uri.parse("content://sms/inbox/" + asa), 
	                                values, 
	                                selection, 
	                                selectionArgs);
	                 	}
	            	}
				}while(cursor.moveToPrevious());
		    	Log.d("com.shady",Integer.toString(val)+where.get(val));
	    	}	
        	}
	    	cursor.close();
	    	if(uri.contains("index.html")){
	        msg += "</div>"+
	        		"<div><input id=\"messagebox\" type=\"text\"></input><button id=\"send\">Send Text</button></div>"+
	        		"<div id='loadingDiv'>"+
	        		"Please wait...  <img src='spinner.gif' />"+
	        		"</div> "+
	        		 "</body></html>\n";
	    	}
	    	
	    	if(uri.contains("send")){
	    		//Send sms procedure
	            Map<String, String> files = new HashMap<String, String>();
	    		try {
					session.parseBody(files);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ResponseException e) {
					e.printStackTrace();
				}
	    		
	    		String message=session.getQueryParameterString().substring(5,session.getQueryParameterString().length());
	    		String sms="SMS Sent";//sendSMS(contacttosend,message);
	    		return new NanoHTTPD.Response(sms);
	    	}
	    	
	    	
	    	//String test="<html><head><script>function myFunction() {document.getElementById(\"demo\").innerHTML = \"Paragraph changed.\";}</script></head><body><h1>My Web Page</h1><p id=\"demo\">A Paragraph.</p><button type=\"button\" onclick=\"myFunction()\">Try it</button></body></html>";
	        Log.d("com.shady", Long.toString(System.currentTimeMillis()));
	        if(uri.contains(".gif")){
	              InputStream mbuffer = null;
                try {
					mbuffer = getApplicationContext().getAssets().open(uri.substring(1));
				} catch (IOException e) {
					e.printStackTrace();
				}      
                return new NanoHTTPD.Response(Status.OK, "image/gif", mbuffer);
	        }
	    	return new NanoHTTPD.Response(msg);
	    }
	}
    private String sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        final String smsresponse = "SMS_SENT";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                    	//smsresponse= "SMS sent";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    	//smsresponse= "Generic failure";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    	//smsresponse= "No service"; 
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    	//smsresponse= "Null PDU"; 
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    	//smsresponse= "Radio off";
                        break;
                }
            }
        }, new IntentFilter(SENT));       
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
        
		return smsresponse;
    }
    
    @SuppressLint("SimpleDateFormat")
	private void cachedata(){
    	//Long.toString(System.currentTimeMillis());
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
        int b=1;
        long buffer = 0;
    	if(cursor.moveToFirst()){
			buffer=cursor.getLong(cursor.getColumnIndex("date"));
			do{
				int i=0;
				long contactId = cursor.getLong(3);
            	String contactId_string = String.valueOf(contactId);
        		String address = cursor.getString(cursor.getColumnIndex("address"));
            	boolean add=true;
            	for(i=0;i<b;i++){
            	if(where.get(i).equals(address)){
            		add=false;
            		break;
            	}}
            	if(add){
           		Uri uri1 = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactId_string));
            	String[] projection = new String[]{ PhoneLookup.DISPLAY_NAME,PhoneLookup.PHOTO_URI};
            	Cursor cursor1 = getContentResolver().query(uri1, projection,null,null,null);
            	cursor1.moveToFirst();
            	where.add(address);

            	try{
            		display.add(cursor1.getString(0));

            	}catch(Exception e){
            		display.add(address);
            	}
            	
            	b++;
            	cursor1.close();
            	}
			}while(cursor.moveToNext());
		}
        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            for(String line : where)
            outputStream.write(line.getBytes());
            outputStream.close();
            FileOutputStream outputStream1 = openFileOutput(filename1, Context.MODE_PRIVATE);
            outputStream1.write(longToBytes(buffer));
            outputStream1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putLong(x);
        return buffer.array();
    }
}
