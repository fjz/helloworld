package com.rockchip.utils;

import android.graphics.PointF;

/**
 * 一元方程式类
 * @author xiexj
 *
 */
public class MNLine {

	private float a;
	
	private float b;
	
	public MNLine(){
		
	}
	
	/**
	 * 2点重新构造这条直线
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void change(float x1,float y1,float x2,float y2){
		
		a = (y2-y1)/(x2-x1);
		b = (x2*y1-y2*x1)/(x2-x1);
	}
	
	/**
	 * 1点不改变斜率移动直线
	 * @param x
	 * @param y
	 */
	public void change(float x,float y){
		b = y-a*x;
	}
	
	/**
	 * 初始化获取这条直线
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static MNLine initLine(float x1,float y1,float x2,float y2){
		MNLine mnLine = new MNLine();
		mnLine.change(x1, y1, x2, y2);
		return mnLine;
	}
	
	/**
	 * 根据X获取Y
	 * @param x
	 * @return
	 */
	public float getYbyX(float x){
		return a*x+b;
	}
	
	/**
	 * 根据Y计算X
	 * @param y
	 * @return
	 */
	public float getXbyY(float y){
		return (y-b)/a;
	}
	
	public float getA(){
		return a;
	}
	
	public float getB(){
		return b;
	}
	
	/**
	 * 获取这条直线的中垂线
	 * @param bx
	 * @param by
	 * @return
	 */
	public MNLine getPBLine(float bx,float by){
		MNLine line = new MNLine();
		line.a = -1/this.a;
		line.b = by-line.a*bx;
		return line;
	}
	
	/**
	 * 返回跟另一条直线的交叉点，float[0]=x,float[1]=y
	 * @param line
	 * @return
	 */
	public PointF getCross(MNLine line){
		
		PointF focus = new PointF();
		focus.x = (b-line.b)/(line.a-a);
		focus.y = getYbyX(focus.x);
		return focus;
	}
	/**
	 * 
	 * @param line 进行平移的直线
	 * @param distance 平移的距离
	 * @param direction 平移的方向，有两个，x轴的正方向值为true或者是负方向值为false。
	 * @return 平移后的直线方程。
	 */
	public MNLine LineTranslationWidthDistance(float distance,boolean direction)
	{
		MNLine l = new MNLine();
		l.a = this.a;
		if(direction)
			l.b = this.b + distance/(float)Math.cos(Math.atan(this.a));
		else
			l.b = this.b - distance/(float)Math.cos(Math.atan(this.a));
		
		return l;
	}
	
	public static  PointF getMidPoint(PointF start,PointF end)
	{
		PointF mid = new PointF();
		mid.x = (start.x+end.x)/2;
		mid.y = (start.y+end.y)/2;
		return mid;
	}
	
	public static float twoPointDistance(PointF start,PointF end)
	{
		float r = 0;
		float temp = (start.x-end.x)*(start.x-end.x)+(start.y-end.y)*(start.y-end.y);
		r = (float) (Math.sqrt(temp));
		return r ;
	}
}
