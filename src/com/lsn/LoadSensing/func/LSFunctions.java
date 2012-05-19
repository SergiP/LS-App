//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio González Díez        [sergio.gd@gmail.com]
//        Sergio Postigo Collado      [spostigoc@gmail.com]
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.lsn.LoadSensing.func;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.ui.CustomToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class LSFunctions {

	public static boolean checkConnection(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo checkInternet = cm.getActiveNetworkInfo();
		if (checkInternet != null &&  checkInternet.isConnected() && checkInternet.isAvailable()) {
			return true;
		}
		else 
		{
			return false;
		}

	}

	public static boolean isIntentAvailable(Context context, String action)
	{
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);

		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 
				PackageManager.MATCH_DEFAULT_ONLY);

		return (list.size() > 0);
	}

	public static boolean checkSDCard(Activity context)
	{
		String auxSDCardStatus = Environment.getExternalStorageState();
		Integer strCustomMessage=0;
		Integer imgCustomImage=0;

		if      (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		else if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
		{
			imgCustomImage = CustomToast.IMG_EXCLAMATION;
			strCustomMessage = R.string.msgSDReadOnly;
		}
		else if (auxSDCardStatus.equals(Environment.MEDIA_NOFS))
		{
			imgCustomImage = CustomToast.IMG_ERROR;
			strCustomMessage = R.string.msgSDBadFormat;
		}
		else if (auxSDCardStatus.equals(Environment.MEDIA_REMOVED))
		{
			imgCustomImage = CustomToast.IMG_ERROR;
			strCustomMessage = R.string.msgSDNotFound;
		}		
		else if (auxSDCardStatus.equals(Environment.MEDIA_SHARED))
		{
			imgCustomImage = CustomToast.IMG_ERROR;
			strCustomMessage = R.string.msgSDShared;
		}		
		else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTABLE))
		{
			imgCustomImage = CustomToast.IMG_ERROR;
			strCustomMessage = R.string.msgSDUnmountable;

		}		
		else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTED))
		{
			imgCustomImage = CustomToast.IMG_ERROR;
			strCustomMessage = R.string.msgSDUnmounted;
		}		

		CustomToast.showCustomToast(context,
				strCustomMessage,
				imgCustomImage,
				CustomToast.LENGTH_LONG);

		return false;
	}

	public static JSONObject urlRequestJSONObject(String url, Map<?,?> params)
	{
		HttpEntity entity;
		HttpResponse response = null;

		response=urlRequest(url,params);

		JSONObject retJSON = null;
		
		if (response != null)
		{
			entity = response.getEntity();

			try {
				retJSON = new JSONObject(EntityUtils.toString(entity,HTTP.UTF_8));
			} catch (ParseException e) {
	
				e.printStackTrace();
			} catch (JSONException e) {
	
				e.printStackTrace();
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}

		return retJSON;

	}


	public static JSONArray urlRequestJSONArray(String url, Map<?,?> params)
	{
		HttpEntity entity;
		HttpResponse response = null;

		response=urlRequest(url,params);

		JSONArray retJSONArray = null;

		if (response!=null)
		{
			entity = response.getEntity();

			try {
				retJSONArray = new JSONArray(EntityUtils.toString(entity,HTTP.UTF_8));

			} catch (ParseException e) {

				e.printStackTrace();
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return retJSONArray;

	}

	@SuppressWarnings("rawtypes")
	private static HttpResponse urlRequest(String url, Map<?,?> params)
	{
		HttpClient client = new DefaultHttpClient();
		//Set timeout connection
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

		HttpPost post = null;
		HttpResponse response = null;

		try
		{
			post = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
			Iterator<?> it = params.entrySet().iterator();
			while (it.hasNext())
			{

				Map.Entry element = (Map.Entry)it.next();
				nameValuePairs.add(new BasicNameValuePair(element.getKey().toString(),element.getValue().toString()));
			}

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return response;
	}

	public static Bitmap getRemoteImage(final URL aURL) {
		try {
			final URLConnection conn = aURL.openConnection();
			conn.connect();
			final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			final Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			return bm;
		} catch (IOException e) {
			Log.d("DEBUGTAG", "Error detected...");
		}
		return null;
	}
}
