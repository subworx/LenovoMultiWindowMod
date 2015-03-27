package de.testroid.xposed.lenovomultiwinmod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class AllAppsActivity extends ListActivity implements View.OnClickListener {
	
	public PackageManager packageManager = null;
    public ApplicationAdapter listadaptor = null;
    Button button;
    public static ArrayList<String> arrAppList;
    // https://github.com/rovo89/XposedAppSettings/tree/master/src/de/robv/android/xposed/mods/appsettings
    private SharedPreferences prefs;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		arrAppList = new ArrayList<String>();
		packageManager = getPackageManager();
        button = (Button) findViewById(R.id.btnSubmit);
        button.setOnClickListener(this);
		new LoadApplications().execute();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;
		switch (item.getItemId()) {
			case R.id.menu_about: {
				displayAboutDialog();
				break;
			}
			default: {
				result = super.onOptionsItemSelected(item);
				break;
			}
		}
		return result;
	}

	private void displayAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.about_title));
		builder.setMessage(getString(R.string.about_desc));
		builder.setPositiveButton("XDA Thread", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		    	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/xposed/modules/lenovo-multiwindow-mod-t2985909"));
		    	   startActivity(browserIntent);
		    	   dialog.cancel();
		       }
		   });
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		            dialog.cancel();
		       }
		});
		builder.show();
	}

    // button press action - store preferences here
	@SuppressLint("WorldReadableFiles") 
	public void onClick(View v) {
        if (arrAppList.isEmpty()) {
        	Toast.makeText(AllAppsActivity.this, "Please select at least one app.", Toast.LENGTH_SHORT).show();
        	return;
        }
    	Toast.makeText(AllAppsActivity.this, "List saved, " + (arrAppList.size()) + " items. Please reboot for changes to take effect.", Toast.LENGTH_LONG).show();
    	String strApps = convertToString(arrAppList);
    	System.out.println(strApps);
    	prefs = getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
    	prefs.edit().putString(Common.PREF_APPS, strApps).commit();
    }

    @Override
    protected void onResume() {
    	//datasource.open();
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	//datasource.close();
    	super.onPause();
    }

    // load apps
	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
            List<ApplicationInfo> applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
			listadaptor = new ApplicationAdapter(AllAppsActivity.this,
					R.layout.snippet_list_row, applist);
			// sort collection alphabetically by app name
            Collections.sort(applist, new ApplicationInfo.DisplayNameComparator(packageManager));
            return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(AllAppsActivity.this, null,
					"Loading application info...");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

    // converts an ArrayList<String> to single delimited String
    //http://stackoverflow.com/questions/7057845/save-arraylist-to-sharedpreferences
    private String convertToString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : list)
        {
            sb.append(delim);
            sb.append(s.trim());
            delim = ",";
        }
        return sb.toString();
    }
}