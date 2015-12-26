
package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * @author zheng
 *
 */
public class WeatherActivity extends Activity {
		private LinearLayout weatherInfolLayout;
		
		private TextView cityNameText, publishText, weatherDespText,
							temp1Text, temp2Text,currentDateText;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.tx1);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		weatherInfolLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		String countyCode =  getIntent().getStringExtra("county_code");
		
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中...");
			Log.d("xys", ""+countyCode);
			weatherInfolLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
			showWeather();
		}else {
			showWeather();
		}
		
		}

		private void showWeather() {
			// TODO Auto-generated method stub
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			cityNameText.setText(prefs.getString("city_name", ""));
			temp1Text.setText(prefs.getString("temp1", ""));
			temp2Text.setText(prefs.getString("temp2", ""));
			weatherDespText.setText(prefs.getString("weather_desp", ""));
			
			publishText.setText(prefs.getString("publish_time", "")+"发布");
			currentDateText.setText(prefs.getString("current_date", ""));
			weatherInfolLayout.setVisibility(View.VISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
		}

		private void queryWeatherCode(String countyCode) {
			// TODO Auto-generated method stub
			String address = "http://www.weather.com.cn/data/list3/city"+countyCode
					+".xml";
			queryFromServer(address, "countyCode");
		}
		private void queryFromServer(final String address, final String type) {
			// TODO Auto-generated method stub
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					// TODO Auto-generated method stub
					if ("countyCode".equals(type)) {
						if (!TextUtils.isEmpty(response)) {		
							String[] array = response.split("\\|");
							if (array !=null && array.length == 2) {
								String weatherCode = array[1];
								queryWeatherInfo(weatherCode);
							}
 						}
					}else if ("weatherCode".equals(type)) {
						Utility.handleweatherResponse(WeatherActivity.this, response);
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								showWeather();
							}
						});
					}
				}
				
				
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							publishText.setText("加载失败！");
						}
					});
				}
			});
		}
		private void queryWeatherInfo(String weatherCode) {
			// TODO Auto-generated method stub
			String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
			queryFromServer(address, "weatherCode");
		}

}