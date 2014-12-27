package de.testroid.xposed.lenovomultiwinmod.replacer;

import android.R;
import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class Replacer implements IXposedHookZygoteInit, IXposedHookInitPackageResources {
	private static String MODULE_PATH = null;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;
	}
	
	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
	  if (!resparam.packageName.equals("com.lenovo.multiwindow"))
		  return;
	XposedBridge.log("---> hooking multiwindow");
	XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
	resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_package", modRes.fwd(de.testroid.xposed.lenovomultiwinmod.R.array.new_array));
	resparam.res.setReplacement("com.lenovo.multiwindow", "array", "whitelist_class", modRes.fwd(de.testroid.xposed.lenovomultiwinmod.R.array.new_array));
	XposedBridge.log("---> replacement done");
	}
	  
}