package com.example.muzafarimran.lastingsales.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.example.muzafarimran.lastingsales.SessionManager;

public class InitService extends IntentService {

	private int result = Activity.RESULT_CANCELED;
	public static final String URL = "urlpath";
//	public static final String FILENAME = "filename";
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.lastingsales.agent";

	public InitService() {
		super("DownloadService");
	}
	
	
	// called asynchronously be Android
	@Override
	protected void onHandleIntent(Intent intent) {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		SessionManager sessionManager = new SessionManager(this);
		sessionManager.fetchData();

//		String fileName = intent.getStringExtra(FILENAME);

			// Successful finished
			result = Activity.RESULT_OK;


		publishResults(result);
	}

	private void publishResults(int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}
}