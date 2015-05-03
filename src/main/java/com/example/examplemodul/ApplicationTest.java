package com.example.examplemodul;

import android.app.Application;

import com.seebye.messengerapi.api.App;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends App
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		CrashHandler.setCrashHandler();
	}
}