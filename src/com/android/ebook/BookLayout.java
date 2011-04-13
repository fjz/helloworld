package com.android.ebook;

import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.widget.FrameLayout;

public class BookLayout extends FrameLayout {

	private int currentPage = 0;
	public static final int PAGE_WIDTH = 800;
	public static final int PAGE_HEIGHT = 440;
	private FrameLayout topLayout;
	private FrameLayout secondLayout;
	private boolean isAutoFlip = false;
	private SinglePage currentFlipPage = null;

	public BookLayout(Context context) {
		super(context);
		currentPage = 0;	
		secondLayout = new FrameLayout(context);
		secondLayout.setLayoutParams(LayoutBean.FF);
		addView(secondLayout);
		topLayout = new FrameLayout(context);
		topLayout.setLayoutParams(LayoutBean.FF);
		addView(topLayout);
		showPage = new SinglePage(context, false, 0, this, BitmapUtils.defBitmap);
		maskPage = new SinglePage(context, false, 0, this, BitmapUtils.defBitmap);
		leftPage = new SinglePage(context, false, 0, this, BitmapUtils.defBitmap);
		secondLayout.addView(showPage);
		topLayout.addView(maskPage);
		topLayout.addView(leftPage);
	}

	
	/**
	 * 翻动书页，显示其他两页的内容
	 * @param page
	 * @param maskPagePath
	 * @param resultPagePath
	 */
	void flipPage(SinglePage page,Path pp,Path maskPagePath,Path a,float angle,float shadowAngle,float fx,float fy,float sdnx,float sdny,int chooseCorner){
		SinglePage m = null;
		if(page.isLeftPage)
			m = getMaskPage(page,false);
		else
			m = getMaskPage(page, true);
		if(m!=null)
		m.onMaskPathDraw(pp,maskPagePath,a,angle,shadowAngle,fx,fy,sdnx,sdny,chooseCorner);
	}
	static boolean right;
	SinglePage showPage = null;
	SinglePage maskPage = null;
	SinglePage leftPage = null;
	void refresh()
	{
		showPage.setBitmap(BitmapUtils.getBitmap(currentPage));
			showPage.reset();
			showPage.setLookPage(true);
			showPage.setPosition(0, 0);
			showPage.setSize(PAGE_WIDTH,PAGE_HEIGHT);
			maskPage.setBitmap(BitmapUtils.getBitmap(currentPage+1));
			if(maskPage!=null){
			maskPage.reset();
			maskPage.setLookPage(false);
			maskPage.setPosition(0, 0);
			maskPage.setSize(PAGE_WIDTH, PAGE_HEIGHT);
			}
			leftPage.setBitmap(BitmapUtils.getBitmap(currentPage-1));
			if(leftPage!=null){
			leftPage.reset();
			leftPage.setLookPage(false);
			leftPage.setPosition(0, 0);
			leftPage.setSize(PAGE_WIDTH, PAGE_HEIGHT);
			}

		invalidate();
	}
	
	private void showRight(boolean right){
		if(right){
			if(topLayout.getChildAt(0)!=null)
				topLayout.getChildAt(0).setVisibility(View.VISIBLE);
			if(topLayout.getChildAt(1)!=null)
				topLayout.getChildAt(1).setVisibility(View.GONE);
		 }else{
			if(topLayout.getChildAt(0)!=null)
				topLayout.getChildAt(0).setVisibility(View.GONE);
					if(topLayout.getChildAt(1)!=null)
						topLayout.getChildAt(1).setVisibility(View.VISIBLE);
		 } 
	}
	/**
	 * 设置当前显示页
	 * @param page
	 */
	public void setCurrentPage(int page){	
		currentPage = page;

		refresh();
	 }
	
	/**
	 * 获取当前翻动页的反页
	 * @param page
	 * @return
	 */
	SinglePage getMaskPage(SinglePage page,boolean right){
		if(!right){
			showRight(false);
			return leftPage;
		}else{
			showRight(true);
			return maskPage;
		}
	}
	
	/**
	 * 成功翻动当前页
	 * @param page
	 */
	void successFlipPage(SinglePage page,boolean flag)
	{
		if(flag) {
			if(page.isLeftPage){
				currentPage--;
			}else{
				currentPage++;
			}	
		 }

		refresh();
	}

	public boolean isAutoFlip() {
		return isAutoFlip;
	}

	public void setAutoFlip(boolean isAutoFlip) {
		this.isAutoFlip = isAutoFlip;
	}

	public SinglePage getCurrentFlipPage() {
		return currentFlipPage;
	}

	public void setCurrentFlipPage(SinglePage currentFlipPage) {
		this.currentFlipPage = currentFlipPage;
	}

}
