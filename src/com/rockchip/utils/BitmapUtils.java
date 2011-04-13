package com.rockchip.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.ebook.R;

public class BitmapUtils {

	//private Context context;
	private static final int CAPACITY = 20;
	public  static  LruCache<Integer, Bitmap>mBitmapCache = new LruCache<Integer, Bitmap>(CAPACITY);
	public  static Bitmap defBitmap;
    private static int[] imgs = {
			R.drawable.aa,
			R.drawable.bb,
			R.drawable.cc,
			R.drawable.dd,
			R.drawable.ee,		
			R.drawable.ff,
			R.drawable.aa,
			R.drawable.bb,
			R.drawable.cc,
			R.drawable.dd,
			R.drawable.ee,		
			R.drawable.ff,	
	};

	public BitmapUtils(Context context){
		for(int i =0;i<imgs.length;i++){
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgs[i]);
			mBitmapCache.put(i, bitmap);
		}
		defBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.def);
	}
	
	
	public static Bitmap getBitmap(int index){
		Bitmap bitmap = mBitmapCache.get(index)==null?defBitmap:mBitmapCache.get(index);	
		return bitmap;	
	}
}
