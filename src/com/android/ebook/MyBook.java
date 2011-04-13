package com.android.ebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MyBook extends Activity {
	
	private BookLayout blo;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		blo = new BookLayout(this);
		new BitmapUtils(this);
		blo.setCurrentPage(4);
		setContentView(blo);
	}
}
