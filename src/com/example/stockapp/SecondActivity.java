package com.example.stockapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
public  class SecondActivity extends Activity implements OnItemClickListener{
	private String[] urls;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.second);
        
		//TextView title=new TextView(this);
		try {
			JSONObject news=MainActivity.stock.getJSONObject("News");
			JSONArray items=news.getJSONArray("Item");
			String[] values= new String[items.length()];
			urls= new String[items.length()];
			for(int i=0; i<items.length();i++){
				JSONObject jsonobject = items.getJSONObject(i);
                String name=jsonobject.getString("Title");
                String Url=jsonobject.getString("Link");
                values[i]=name;  
                urls[i]=Url;
			}
			final ListView listView = (ListView) findViewById(R.id.list);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, android.R.id.text1, values);
	        listView.setAdapter(adapter); 
	        listView.setOnItemClickListener(this);
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void onItemClick(AdapterView<?> l, View v, final int position, long id) {
		final Builder popup_news=new AlertDialog.Builder(this);
		popup_news.setMessage("View News")
		.setPositiveButton("Cancle", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setNegativeButton("View", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Uri uri = Uri.parse(urls[position]);
	            SecondActivity.this.startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
	        }
	     })
	     .show();
            
    }
	
    public void returnMain(View view){
    	finish();
    }
}
