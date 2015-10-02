package com.example.stockapp;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.*;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.model.*;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

//import android.service.textservice.SpellCheckerService.Session;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.os.Build;

public class MainActivity extends Activity{
	public JSONObject json;
	public static JSONObject stock;
	private JSONObject quote;
	AutoCompleteTextView autoComplete;
	private String arrow_down="\u2193";
	private String arrow_up="\u2191";
	private static String APP_ID = "443959722407992"; // Replace your App ID here
	// Instance of Facebook Class    
	private Facebook facebook;     
	@SuppressWarnings("deprecation")
	private AsyncFacebookRunner mAsyncRunner;
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     // Add code to print out the key hash
       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.stockapp", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        autoComplete = (AutoCompleteTextView) findViewById(R.id.edit_message);  
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, COUNTRIES);  
        //autoComplete.setAdapter(adapter); 
        autoComplete.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));
        autoComplete.setThreshold(1);
        facebook = new Facebook(APP_ID);
        mAsyncRunner = new AsyncFacebookRunner(facebook); 
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_part, new PlaceholderFragment())
                    .commit();
        }*/
    }

    
    private SessionStatusCallback statusCallback = new SessionStatusCallback();
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
                // Respond to session state changes, ex: updating the view
        }
    }

    public void startFB(){
        // start Facebook Login

     Session.openActiveSession(this, true, new Session.StatusCallback() {
        // callback when session changes state
        @SuppressWarnings("deprecation")
		@Override
          public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
            	//loginToFacebook(); 
            	publishFeedDialog();
            }
          }
        });
    }
    
    private void publishFeedDialog() {
    	Session session=Session.getActiveSession();
	    Bundle params = new Bundle();
	    try {
	    	/*JSONObject property=new JSONObject();
	    	property.put("text","here");
	    	property.put("href",stock.getString("Name"));
	    	JSONObject properties=new JSONObject();
	    	properties.put("Look at details", property);*/
	    	String desp="";
	    	desp +="Last Trade Price:"+ quote.getString("LastTradePriceOnly");
	    	if(quote.getString("ChangeType").equals("+")){
	    		desp +=", Changes:" + quote.getString("Change") +"(" +quote.getString("ChangeInPercent")+")";
	    	}else{
	    		desp +=", Changes:-" + quote.getString("Change") +"(-" +quote.getString("ChangeInPercent")+")";
	    	}
	    	params.putString("name", stock.getString("Name"));
	    	params.putString("caption", "Stock Information of "+stock.getString("Name")+"("+stock.getString("Symbol")+")");
	    	params.putString("description", desp);
	    	params.putString("picture", stock.getString("StockChartImageURL"));
	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(MainActivity.this,session,params))
	        .setOnCompleteListener(new OnCompleteListener() {
	            @Override
	            public void onComplete(Bundle values,FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(MainActivity.this,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(MainActivity.this.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(MainActivity.this.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(MainActivity.this.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        //facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void sendMessage(View view){
    	AutoCompleteTextView editText=(AutoCompleteTextView) findViewById(R.id.edit_message);
    	String message=editText.getText().toString();
    	if(message.length()==0 ){
    		new AlertDialog.Builder(this)
    	    .setMessage("Please enter symbol...")
    	    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            // do nothing
    	        }
    	     })
    	     .show();
    	}
    	else if(message.toLowerCase().equals("unknown")){
    		new AlertDialog.Builder(this)
    	    .setMessage("Information Not Available")
    	    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            // do nothing
    	        }
    	     })
    	     .show();
    	}
    	else{
    		message="companyname="+URLEncoder.encode(message);
    		String url = "http://cs-server.usc.edu:28699/examples/servlet/SearchServlet/?";
    		ConnectivityManager connMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo networkinfo=connMgr.getActiveNetworkInfo();
    		if(networkinfo !=null && networkinfo.isConnected()){
    			new DownloadData().execute(url+message);
    		}else{
        		new AlertDialog.Builder(this)
        	    .setMessage("Cannot connent to Internet")
        	    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) { 
        	            // do nothing
        	        }
        	     })
        	     .show();
    		}
    	}
    }
    
    private class DownloadData extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... urls) {
	        try {
				URL url=new URL(urls[0]);
				String r=null;
				URLConnection urlconnection=url.openConnection();
				BufferedReader in=new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
				StringBuilder sb=new StringBuilder();
				sb.append(in.readLine()+"\n");
				String line="0";
				while((line=in.readLine())!=null){
					sb.append(line+"\n");
				}
				r=sb.toString();
				return r;
	        } catch (Exception e) {
	            return "Unable to retrieve web page.";
	        }
	    }

	    protected void onPostExecute(String r) {
	        // TODO: check this.exception
	        // TODO: do something with the feed
	    	MainActivity.this.JsonObtained(r);
	    }
	}
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;

		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      Bitmap resized = Bitmap.createScaledBitmap(mIcon11, (int)(mIcon11.getWidth()*2), (int)(mIcon11.getHeight()*2), true);
		      return resized;
		  }

		  protected void onPostExecute(Bitmap result) {
		      bmImage.setImageBitmap(result);
		  }
	}
    
    private void JsonObtained(String r){
		try {
			LinearLayout ll=(LinearLayout) findViewById(R.id.result_part);
			ll.removeAllViews();
	    	LinearLayout lb=(LinearLayout) findViewById(R.id.button_part);
			lb.removeAllViews();
	    	json=new JSONObject(r);
	    	stock = json.getJSONObject("result");
	    	if(stock == null){
	    		return;
	    	}
	    	//--------------title---------------------
    		TextView title=new TextView(this);
    		title.setText(stock.getString("Name")+stock.getString("Symbol"));
	    	title.setGravity(Gravity.CENTER_HORIZONTAL);
    		title.setPadding(0, 10, 0, 0);
    		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
	    	ll.addView(title);
	    	//--------------TITLE END------------------
	    	quote = stock.getJSONObject("Quote");
	    	//-----------------CURRENT--------------------
	    	TextView cur= new TextView(this);
	    	cur.setText(quote.getString("LastTradePriceOnly"));//???????
	    	cur.setGravity(Gravity.CENTER_HORIZONTAL);
	    	cur.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
	    	ll.addView(cur);
	    	//-----------------CURRENT END---------------
	    	//-----------------CHANGE --------------------
	    	TextView change= new TextView(this);
	    	
	    	if(quote.getString("ChangeType").equals("+")){
	    		change.setText(arrow_up+" "+quote.getString("Change")+"("+quote.getString("ChangeInPercent")+")");
	    		change.setTextColor(Color.GREEN);
	    	}else{
	    		change.setText(arrow_down+" "+quote.getString("Change")+"("+quote.getString("ChangeInPercent")+")");
	    		change.setTextColor(Color.RED);
	    	}
	    	change.setGravity(Gravity.CENTER_HORIZONTAL);
	    	change.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
	    	ll.addView(change);
	    	//------------------CHANGE END------------------
	    	//------------------Table-------------------
	    	String[][] table_data={{"Prev Close","PreviousClose"},{"Open", "Open"},{"Bid","Bid"},{"Ask","Ask"},
	    	{"1st Yr Target","OneYearTargetPrice"},{"Day Range","Days"},{"52 wk Range","Year"},{"Volume","Volume"},{"Avg Vol(3m)","AverageDailyVolume"},{"Market Cap","MarketCapitalization"}};
	    	
	    	TableLayout table=new TableLayout(this);
	    	table.setPadding(30,0,30,0);
	    	table.setOrientation(TableLayout.VERTICAL);
	    	table.setShrinkAllColumns(false);
	    	table.setStretchAllColumns(true);
	    	table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
	    	for(int i=0; i<table_data.length;i++){
	    		TableRow row = new TableRow(this);
	    		TextView cell = new TextView(this);
    			cell.setPadding(20, 0, 20, 0);
    			cell.setText(table_data[i][0]);
    			row.addView(cell);
    			cell=new TextView(this);
    			if(table_data[i][0].equals("Day Range")){
    				cell.setText(quote.getString(table_data[i][1]+"Low")+"-"+quote.getString(table_data[i][1]+"High"));
    			}else if(table_data[i][0].equals("52 wk Range")){
    				cell.setText(quote.getString(table_data[i][1]+"Low")+"-"+quote.getString(table_data[i][1]+"High"));
    			}else{
    				cell.setText(quote.getString(table_data[i][1]));
    			}
    			cell.setGravity(Gravity.RIGHT);
    			row.addView(cell);
    			table.addView(row);
	    	}
    		ll.addView(table);
    		//----------Table END------------------
	    	//--------------IMG--------------------
	    	ImageView imageView = new ImageView(this);
	    	imageView.setPadding(0, 10, 0, 0);
	    	new DownloadImageTask(imageView).execute(stock.getString("StockChartImageURL"));
	    	ll.addView(imageView);
	    	//----------IMG END--------------------
	    	//------------News BUTTON------------
			lb.setPadding(0, 10, 0, 0);
	    	Button btn_news = new Button(this);
	    	btn_news.setText("News Headlines");
	    	btn_news.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1));
	    	lb.addView(btn_news);
	    	
	    	btn_news.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
    	        	//define a new Intent for the second Activity
    	    		Intent intent = new Intent(MainActivity.this, SecondActivity.class);
    	    		//start the second Activity
    	    		MainActivity.this.startActivity(intent);
				}
			});
	    	//-------------News BUTTON END--------------
	    	//-----------FB BUTTON---------------
	    	Button btn_fb=new Button(this);
	    	btn_fb.setText("Facebook");
	    	btn_fb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1));
	    	
	    	lb.addView(btn_fb);
	    	final Builder popup=new AlertDialog.Builder(this);
	    	btn_fb.setOnClickListener(new View.OnClickListener() {
	    	    @Override
	    	    public void onClick(View v) {  	    	
	        		popup.setMessage("Share to Facebook")
	        		.setPositiveButton("Cancle", new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) { 
	        	            // do nothing
	        	        }
	        	     })
	        	    .setNegativeButton("Post", new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) { 
	    	    	        startFB(); 
	        	        }
	        	     })
	        	     .show();   
	    	    }
	    	});
	    	//----------------FB BUTTON END--------------------
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
            View rootView = inflater.inflate(R.layout.main, container, false);
            return rootView;
        }
    }

}

class SymbolResult {
	 
    private String stockSymbol;
    private String stockName;
    private String excode;
 
    public SymbolResult() {}
    public SymbolResult(String stockSymbol, String stockName, String excode) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.excode = excode;
    }
     // get and set methods
    @Override
    public String toString() {
        return stockSymbol + "," + stockName + "("+ excode +")";
    }
}

class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "http://autoc.finance.yahoo.com";
	private static final String TYPE_AUTOCOMPLETE = "/autoc";
	private static final String CALL_BACK = "YAHOO.Finance.SymbolSuggest.ssCallback";


	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE+TYPE_AUTOCOMPLETE);
	        sb.append("?query=" + URLEncoder.encode(input, "utf8"));
	        sb.append("&callback=" + CALL_BACK);
	       
	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
	        
	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	    	String answer = jsonResults.toString().substring(jsonResults.toString().indexOf("(")+1,jsonResults.toString().indexOf(")"));
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(answer);
	        JSONArray predsJsonArray = jsonObj.getJSONObject("ResultSet").getJSONArray("Result");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	        	JSONObject item=predsJsonArray.getJSONObject(i);
	            resultList.add(item.getString("symbol")+","+item.getString("name")+"("+item.getString("exch")+")");
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}
	
    private ArrayList<String> resultList;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}