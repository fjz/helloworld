package com.android.ebook;

import android.graphics.PointF;

/**
 * һԪ����ʽ��
 * @author xiexj
 *
 */
public class MNLine {

	private float a;
	
	private float b;
	
	public MNLine(){
		
	}
	
	/**
	 * 2�����¹�������ֱ��
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
	 * 1�㲻�ı�б���ƶ�ֱ��
	 * @param x
	 * @param y
	 */
	public void change(float x,float y){
		b = y-a*x;
	}
	
	/**
	 * ��ʼ����ȡ����ֱ��
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
	 * ����X��ȡY
	 * @param x
	 * @return
	 */
	public float getYbyX(float x){
		return a*x+b;
	}
	
	/**
	 * ����Y����X
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
	 * ��ȡ����ֱ�ߵ��д���
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
	 * ���ظ���һ��ֱ�ߵĽ���㣬float[0]=x,float[1]=y
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
	 * @param line ����ƽ�Ƶ�ֱ��
	 * @param distance ƽ�Ƶľ���
	 * @param direction ƽ�Ƶķ�����������x���������ֵΪtrue�����Ǹ�����ֵΪfalse��
	 * @return ƽ�ƺ��ֱ�߷��̡�
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
