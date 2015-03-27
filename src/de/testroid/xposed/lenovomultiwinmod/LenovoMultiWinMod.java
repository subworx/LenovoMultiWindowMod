package de.testroid.xposed.lenovomultiwinmod;

import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class LenovoMultiWinMod implements IXposedHookZygoteInit, IXposedHookInitPackageResources  {

	public static final String this_package = LenovoMultiWinMod.class.getPackage().getName();
	// https://github.com/rovo89/XposedAppSettings/tree/master/src/de/robv/android/xposed/mods/appsettings
	public static XSharedPreferences prefs;
	private static String MODULE_PATH = null;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;
		loadPrefs();
	}
	
	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		if (!resparam.packageName.equals("com.lenovo.multiwindow"))
			return;
		String appString = prefs.getString(Common.PREF_APPS, null);
		XposedBridge.log("---> hooking multiwindow");
		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
		if (appString == null || appString == "") {
			resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_package", modRes.fwd(de.testroid.xposed.lenovomultiwinmod.R.array.new_array));
			resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_class", modRes.fwd(de.testroid.xposed.lenovomultiwinmod.R.array.new_array));
			XposedBridge.log("---> replacement w/ static new_array done");
		} else {
			String[] newString = convertToArray(appString);
			resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_package", newString);
			resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_class", newString);
			XposedBridge.log("---> replacement w/ dynamic arrAppList done");
		}
	}
	
	public static void loadPrefs() {
		prefs = new XSharedPreferences(Common.MY_PACKAGE_NAME, Common.PREFS);
		prefs.makeWorldReadable();
	}
    
	// converts delimited String to String[] array
    private String[] convertToArray(String string) {
    	String[] myArray = string.split(",");
    	System.out.println("-------> myArray = " + myArray + ".");
    	return myArray;
    }

}

