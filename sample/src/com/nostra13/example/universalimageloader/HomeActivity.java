/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.example.universalimageloader;

import static com.nostra13.example.universalimageloader.Constants.IMAGES;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.utils.L;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		
		
		File testImageOnSdCard = new File("/mnt/sdcard/UniversalImageLoader.png");
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);       /// Hello..

		}
		
		try {
		    HttpParams params = new BasicHttpParams();
		    HttpConnectionParams.setSoTimeout(params, 0);
		    HttpClient httpClient = new DefaultHttpClient(params);

		    //prepare the HTTP GET call 
		    HttpGet httpget = new HttpGet("http://192.168.123.178:3000/stores.json");
		    //get the response entity
		    HttpEntity entity = httpClient.execute(httpget).getEntity();

		    if (entity != null) {
		        //get the response content as a string
		        String response = EntityUtils.toString(entity);
		        //consume the entity
		        entity.consumeContent();

		        // When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate deallocation of all system resources
		        httpClient.getConnectionManager().shutdown();

		        //return the JSON response
		        JSONObject object = new JSONObject(response.trim());
		        JSONArray jsonArray = object.getJSONArray("phone");
		        if(jsonArray != null) {
		           for(int i = 0 ; i < jsonArray.length() ; i++) {
		                JSONObject object1 = (JSONObject) jsonArray.get(i); 
		                String phoneString = object1.getString("phone");
		                
		                if(phoneString.equals(getMyPhoneNumber())){
		    				Intent Grid_call_intent = new Intent(HomeActivity.this, ImageGridActivity.class);
		    				Grid_call_intent.putExtra(Extra.IMAGES, IMAGES); 
		    				startActivity(Grid_call_intent);
		    			}
		           }
		        } 
		    }
		}catch (Exception e) {
		    e.printStackTrace();
		}
		
		
//		for(int i=0;i<phone_num_db.length;i++){
//			if(phone_num_db[i].equals(getMyPhoneNumber())){
//				Intent Grid_call_intent = new Intent(HomeActivity.this, ImageGridActivity.class);
//				Grid_call_intent.putExtra(Extra.IMAGES, IMAGES); 
//				startActivity(Grid_call_intent);
//			}
//		}
	}
	

		private String getMyPhoneNumber(){
		    TelephonyManager mTelephonyMgr;
		    mTelephonyMgr = (TelephonyManager)
		        getSystemService(Context.TELEPHONY_SERVICE);
		    return mTelephonyMgr.getLine1Number();
		}
		
//		private String getMy10DigitPhoneNumber(){
//		    String s = getMyPhoneNumber();
//		    return s;
//		}

	
//	public void onImageGridClick(View view) {
//		Intent intent = new Intent(this, ImageGridActivity.class);
//		intent.putExtra(Extra.IMAGES, IMAGES);
//		startActivity(intent);
//	}

	

	

	@Override
	public void onBackPressed() {
		imageLoader.stop();
		super.onBackPressed();
	}
	
	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open("UniversalImageLoader.png");
					FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}
}