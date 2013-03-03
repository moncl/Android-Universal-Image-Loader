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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

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
			copyTestImageToSdCard(testImageOnSdCard);
		}
		
		if("01095073023".equals(getMyPhoneNumber())){
			Intent Grid_call_intent = new Intent(HomeActivity.this, ImageGridActivity.class);
			Grid_call_intent.putExtra(Extra.IMAGES, IMAGES); 
			startActivity(Grid_call_intent);
		}else {
			Toast toast = Toast.makeText(this,"인증된 회원만 이용할 수 있습니다", 
					Toast.LENGTH_SHORT); 
			toast.show(); 
		}

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