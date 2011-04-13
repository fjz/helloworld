package com.android.ebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class SinglePage extends View {

	//此时翻页的是否是左边的页
	public boolean isLeftPage = false;
	//bitmap的左上角坐标
	private int x;
	private int y;
	//bitmap的宽度
	private int width;
	//bitmap的高度
	private int height;
	//使用的画刷
	private Paint paint;
	//点击时所选择的角
	private int chooseCorner = -1;
	public static final int LEFT_UP_CORNER = 0;
	public static final int LEFT_BOTTOM_CORNER = 1;
	public static final int RIGHT_UP_CORNER = 2;
	public static final int RIGHT_BOTTOM_CORNER = 3;
	public static final int LEFT_SIDE = 4;
	public static final int RIGHT_SIDE = 5;
	//点击的点离所对应的那个角的距离
	public static final int DISTANCE = 120;
	//贝塞尔曲线所对应圆柱面的半径
	private  float R = 0;
	//A点的坐标是点击的坐标
	private PointF tapPoint = null;
	//B点的坐标是A点翻起后跟下一页的交叉点
	//private PointF mXIntersectionPoint = null;
	//C点的坐标是D点翻起后跟下一页的交叉点，可能与D点重复
	//private PointF mYIntersectionPoint = null;
	//D点的坐标是本页的另一个角的点的坐标
	//private PointF mDPoint = null;
	private PointF mPcPoint = null;
	private PointF mPbPoint = null;
	private PointF mPnPoint = null;
	private PointF mPcbPoint = null;
	private PointF mPcnPoint = null;
	
	private PointF mPn2Point = null;
	private PointF mPc2Point = null;
	private PointF mPb2Point = null;
	private PointF mPcb2Point = null;
	private PointF mPcn2Point = null;
	//private PointF mSdn2Point = null;
	private PointF mSdnPoint = null;
	//旋转的角度
	private float angle = 0;
	//翻动页背面的阴影角度
	private float shadowAngle = 0;
	//阴影长度
	private float shadowLength = 0;
	private float sdnx = 0;
	private float sdny = 0;
	//是否隐藏层
	private boolean isMask;
	//是否是当前显示页
	private boolean isLookPage;
	//隐藏层的显示区域
	private Path mBackPagePath;
	private Path mNextPagePath;
	private Path pp;
	//翻动页的fx坐标,其实就是A点坐标
	private float fx;
	//翻动页的fy坐标
	private float fy;
	private Handler handler = new Handler();
	private Bitmap bitmap = null;
	private BookLayout blo;
	private static int delta = 80;
	float deltaY=0;
	private int chooseSide = -1;
	private int cr = -1;
	Rect rectf1 = new Rect(0,0,0,0);
	static int[]cc = {0x00ffffff,0xff888888,0x00ffffff};
	static float[]position ={0,3/8f,1.0f};
	
	public SinglePage(Context context,boolean isLeftPage,int pageNo,BookLayout blo,Bitmap bitmap/*PageContent content*/) {
		super(context);
		this.isLeftPage = isLeftPage;
		this.blo = blo;
		this.bitmap = bitmap;
		paint = new Paint();
		paint.setAntiAlias(true);
		isMask = true;
		isLookPage = false;
		init();
	}
	
	private void init(){
		tapPoint =  new PointF(-1,-1);
		mPcPoint =  new PointF(-1,-1);
		mPbPoint =  new PointF(-1,-1);
		mPnPoint =  new PointF(-1,-1);
		mPcbPoint=  new PointF(-1,-1);
		mPcnPoint=  new PointF(-1,-1);
		mPn2Point=  new PointF(-1,-1);
		mPc2Point=  new PointF(-1,-1);
		mPb2Point=  new PointF(-1,-1);
	    mPcb2Point= new PointF(-1,-1);
	    mPcn2Point= new PointF(-1,-1);
		mSdnPoint=  new PointF(-1,-1);	
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//抗锯齿 有差别的
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		if(isMask){
			//long time = System.currentTimeMillis();
			if(mBackPagePath==null|| mNextPagePath == null||pp==null) 
				return;
			float tx = 0;
			float ty = 0;
			shadowLength = (float) (height*Math.sqrt(2));
			LinearGradient lineShader = null;
			switch(chooseCorner){
			case LEFT_UP_CORNER:{
				tx = fx-width;
				ty = fy;
				rectf1.set((int)sdnx,(int)sdny, (int)(sdnx+delta), (int)(sdny+shadowLength));
				lineShader = new LinearGradient((int)sdnx,sdny, sdnx+delta, sdny, cc,position, TileMode.CLAMP);			
				isLeftPage = true;
				break;
			}
			case RIGHT_UP_CORNER:{
				tx = fx;
				ty = fy;
				rectf1.set((int)(sdnx-delta), (int)sdny, (int)(sdnx), (int)(sdny+shadowLength));
				lineShader = new LinearGradient((int)sdnx,sdny, (sdnx-delta), sdny, cc,position, TileMode.CLAMP);
				isLeftPage = false;
				break;
			}
			case LEFT_BOTTOM_CORNER:{
				tx = fx-width;
				ty = fy-height;
				rectf1.set((int)sdnx,(int)sdny, (int)(sdnx+delta), (int)(sdny+shadowLength));
				lineShader = new LinearGradient((int)sdnx,sdny, sdnx+delta, sdny, cc,position, TileMode.CLAMP);
				isLeftPage = true;
				break;
			}
			case RIGHT_BOTTOM_CORNER:{
				tx = fx;
				ty = fy-height;
				rectf1.set((int)(sdnx-delta), (int)sdny, (int)(sdnx), (int)(sdny+shadowLength));
				lineShader = new LinearGradient((int)(sdnx),sdny, (float)(sdnx)-delta, sdny, cc,position, TileMode.CLAMP);
				isLeftPage = false;
				break;
			}
			}	
			
			canvas.save();
			//paint.setStyle(Style.FILL_AND_STROKE);
			canvas.clipPath(mNextPagePath);
			if(bitmap != null){
			 canvas.drawBitmap(bitmap,null, new Rect(x,y,x+width,y+height), paint);
			}
			canvas.restore();
			
			canvas.save();
			paint.setStyle(Style.FILL);
			paint.setColor(Color.WHITE);
			canvas.drawPath(mBackPagePath, paint);
			
			canvas.clipPath(mBackPagePath);
			RadialGradient rg = new RadialGradient(fx, fy, 100, 0x88888888, 0x00ffffff, TileMode.CLAMP);
			paint.setShader(rg);
			
			canvas.rotate(angle, fx,fy);
			if(bitmap != null)
				canvas.drawBitmap(bitmap,null, new Rect((int)tx,(int)ty,(int)tx+width,(int)ty+height), paint);
			canvas.drawCircle(fx, fy, 100, paint);
			paint.setShader(null);
			canvas.restore();
			
			canvas.save();
			canvas.clipPath(pp);
			canvas.rotate(shadowAngle,sdnx,sdny);
			paint.setShader(lineShader);
			canvas.drawRect(rectf1, paint);
			paint.setShader(null);
			canvas.restore();		
			//Log.d("time", ""+(System.currentTimeMillis()-time));	
		}else{//显示层
			if(bitmap != null)
				 canvas.drawBitmap(bitmap,null, new Rect(x,y,x+width,y+height), paint);
	
			if(tapPoint.x!=-1&&tapPoint.y!=-1){
				Path backPath = null;
				Path a=null;
				Path pp = null;
				float opx = -1;
				float opy = -1;
				switch(chooseCorner){
				 case RIGHT_UP_CORNER:{
					opx = x+width;
					opy = y;
					break;
				  }
				 case RIGHT_BOTTOM_CORNER:{
					opx= x+width;
					opy = y+height;
					break;
				  }
				 case LEFT_UP_CORNER:{
					opx=x;
					opy=y;		
					break;
				  }
				case LEFT_BOTTOM_CORNER: {		
					opx=x;
					opy=y+height;
					break;
				  }
				}	
				backPath = new Path();		
				backPath.moveTo(mPcPoint.x, mPcPoint.y);
				backPath.quadTo(mPcnPoint.x, mPcnPoint.y, mPnPoint.x, mPnPoint.y);
				backPath.lineTo(tapPoint.x, tapPoint.y);
				backPath.lineTo(mPn2Point.x, mPn2Point.y);
				backPath.quadTo(mPcn2Point.x, mPcn2Point.y, mPc2Point.x, mPc2Point.y);
				backPath.lineTo(mPcPoint.x, mPcPoint.y);
				
				a = new Path();
				a.moveTo(mPbPoint.x,mPbPoint.y);
				a.lineTo(opx, opy);
				if(chooseSide!=-1&&tapPoint.y<y)
					a.lineTo(opx, opy+height);
				a.lineTo(mPb2Point.x, mPb2Point.y);		
				a.quadTo(mPcb2Point.x, mPcb2Point.y, mPc2Point.x, mPc2Point.y);
				a.lineTo(mPcPoint.x, mPcPoint.y);
				a.quadTo(mPcbPoint.x, mPcbPoint.y, mPbPoint.x, mPbPoint.y);
				
				 pp = new Path();
				pp.moveTo(mPbPoint.x, mPbPoint.y);
				pp.lineTo(opx,opy);
				pp.lineTo(mPb2Point.x, mPb2Point.y);
				pp.quadTo(mPcb2Point.x, mPcb2Point.y, mPc2Point.x, mPc2Point.y);
				pp.quadTo(mPcn2Point.x, mPcn2Point.y, mPn2Point.x, mPn2Point.y);
				pp.lineTo(tapPoint.x, tapPoint.y);
				pp.lineTo(mPnPoint.x, mPnPoint.y);
				pp.quadTo(mPcnPoint.x, mPcnPoint.y, mPcPoint.x, mPcPoint.y);
				pp.quadTo(mPcbPoint.x, mPcbPoint.y, mPbPoint.x, mPbPoint.y);
				
				blo.flipPage(this,pp, backPath,a,angle,shadowAngle,tapPoint.x,tapPoint.y,mSdnPoint.x,mSdnPoint.y,chooseCorner);
			}
		}
	}
	/**
	 * 显示反页遮罩层的区域
	 * @param path
	 * @param angle
	 * @param x
	 * @param y
	 */
	public void onMaskPathDraw(Path pp,Path path,Path a,float angle,float shadowAngle,float x,float y,float sdnx,float sdny,int chooseCorner){
		this.pp= pp;
		this.mBackPagePath = path;
		this.mNextPagePath=a;
		this.angle = angle;
		this.shadowAngle = shadowAngle;
		this.fx = x;
		this.fy = y;
		this.sdnx = sdnx;
		this.sdny = sdny;
		this.chooseCorner = chooseCorner;
		invalidate();
	}
	
	public void setSize(int w,int h){
		width = w;
		height = h;
	}
	
	public void setPosition(int x,int y){
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		if(!isLookPage||blo.isAutoFlip()){
			return false;
		 }
	int act = event.getAction();
	float hx = event.getX();
	float hy = event.getY();
	
	switch(act){
		case MotionEvent.ACTION_DOWN:{
				chooseCorner = -1;
				chooseSide = -1;
				if(isNearCorner(hx, hy)){
						tapPoint.x = hx;
						tapPoint.y = hy;
						calculate();	
						invalidate();		
					}
				else if(isNearSide(hx,hy)){
					deltaY = hy;
					if(deltaY>(y+height)/2)
						deltaY = height-deltaY;
					else
						deltaY = deltaY-y;
					invalidate();
				}
				break;
			}
		case MotionEvent.ACTION_MOVE:{
				//Log.d("delta", ""+deltaX+" "+deltaY);
			if(chooseCorner!=-1){
				tapPoint.x = hx;
				tapPoint.y = hy;
				switch(chooseCorner){
					case RIGHT_UP_CORNER:
					case LEFT_UP_CORNER:
						tapPoint.y = hy;
						tapPoint.y = chooseSide == -1?hy:(cr==chooseCorner?hy-deltaY:hy-(height-deltaY));
						if(tapPoint.y>150)
							tapPoint.y=150;
							break;
					case RIGHT_BOTTOM_CORNER:                                                                                                     
					case LEFT_BOTTOM_CORNER:
						tapPoint.y = chooseSide ==-1?hy:(cr==chooseCorner?hy+deltaY:hy+height-deltaY);
						if(tapPoint.y<330)
							tapPoint.y=330;
							break;
					}
			 calculate();
			 invalidate();
					}
				break;
			}
		case MotionEvent.ACTION_UP:{
				if(chooseCorner!=-1){
						successOrResetPage();
					}
				break;
			}
		}	
	if(chooseCorner==-1){
		return false;
	}
	return true;
	
   }
	
	private boolean isNearSide(float hx, float hy) {
		if(hx>width-120 && hx <width){
			isLeftPage = false;
			if(hy>=(x+height)/2)
				chooseCorner = RIGHT_BOTTOM_CORNER;
			else
				chooseCorner = RIGHT_UP_CORNER;
			cr = chooseCorner;
			chooseSide = RIGHT_SIDE;
			return true;
		}
		else if(hx>x && hx<x+120){
			if(hy>=(x+height)/2)
				chooseCorner = LEFT_BOTTOM_CORNER;
			else
				chooseCorner = LEFT_UP_CORNER;
			cr = chooseCorner;
			isLeftPage = true;
			chooseSide = LEFT_SIDE;
			return true;
		}
		
		return false;
	}

	//判断是否在页角附近
	private boolean isNearCorner(float hx,float hy){
		int lux = x;
		int luy = y;
		int lbx = x;
		int lby = y+height;
		int rux = x+width;
		int ruy = y;
		int rbx = x+width;
		int rby = y+height;
		int[][] pd = {
				{lux,luy},
				{lbx,lby},
				{rux,ruy},
				{rbx,rby}
		};
		for(int i=0;i<pd.length;i++){
			//计算是否在页角附近，并返回是哪个页角
			if((pd[i][0]-hx)*(pd[i][0]-hx)+(pd[i][1]-hy)*(pd[i][1]-hy)<= DISTANCE* DISTANCE){
				chooseCorner = i;
				if(i<2)
					isLeftPage = true;
				else
					isLeftPage = false;
				return true;
			}
		}
		return false;
	}
	
	//判断是否成功翻页还是取消翻页
	private void successOrResetPage(){
		float sx = -1;
		float sy = -1;
		float fx = -1;
		float fy = -1;
		//计算成功或失败翻页后，被触碰的页角需要达到的坐标
		switch(chooseCorner){
		 case LEFT_UP_CORNER:{
			sx = x+width;
			sy = y;
			fx = x;
			fy = y;
			break;
		  }
		 case LEFT_BOTTOM_CORNER:{
			sx = x+width;
			sy = y+height;
			fx = x;
			fy = y+height;
			break;
		  }
		case RIGHT_UP_CORNER:{
			sx = x;
			sy = y;
			fx = x+width;
			fy = y;
			break;
		 }
		case RIGHT_BOTTOM_CORNER:{
			sx = x;
			sy = y+height;
			fx = x+width;
			fy = y+height;
			break;
		 }
		}
		if(isLeftPage&&(tapPoint.x>=x+width*2/5)){
			autoFlipPage(sx, sy, true);
		}else if(!isLeftPage&&(tapPoint.x<=x+width*3/5)){
			autoFlipPage(sx, sy, true);
		}else{
			autoFlipPage(fx,fy,false);
		 }
	}
	
	//当在翻页过程中，手松开页角的时候，自动执行翻页动作，让翻页更真实
	private void autoFlipPage(final float tx,final float ty,final boolean isSuccessFlip){
		blo.setAutoFlip(true);
		new Thread(){
			public void run(){
				int count = 0;
				boolean flag = false;
				MNLine line = MNLine.initLine(tapPoint.x, tapPoint.y, tx, ty);
				while(true){
					try {
						if(isLeftPage){
						tapPoint.x = tapPoint.x+count;///30;
						   if(tapPoint.x > tx){
							  tapPoint.x = tx;
						    }
						  }
						else{
							tapPoint.x = tapPoint.x-count;///30;
							if(tapPoint.x < tx){
								tapPoint.x = tx;
							}
						}
						tapPoint.y = line.getYbyX(tapPoint.x);
						calculate();
						flag=autoMovePageCorner(tx, ty);
						count +=5;
						Thread.sleep(0);
						if(flag) {
							break;
						}
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
				//清除翻页参数
				chooseCorner = -1;
			    chooseSide = -1;
				clearPosition();
				if(isSuccessFlip){
					handler.post(new Runnable(){
						public void run(){
							blo.setCurrentFlipPage(null);
							blo.successFlipPage(SinglePage.this, true);
						}
					});
				}else{
					handler.post(new Runnable(){
						public void run(){
							blo.setCurrentFlipPage(null);
							blo.successFlipPage(SinglePage.this, false);
						}
					});
				}
				blo.setAutoFlip(false);
			}
		}.start();
	}
	//线程内部，自动移动页角
	private boolean autoMovePageCorner(float tx,float ty){
		boolean isFinish = false;
		if(tapPoint.x == tx){	
			isFinish = true;
		}
		handler.post(new Runnable(){
				public void run(){
					invalidate();
				}
			});
	
			return isFinish;
	}

	//根据A点计算其余点
	private void calculate(){
		float px = -1;
		float py = -1;
		float ox = -1;
		float oy = -1;
		boolean direction = false;
		PointF mXIntersectionPoint = new PointF(-1,-1);
		PointF mYIntersectionPoint = new PointF(-1,-1);
		if(chooseSide== RIGHT_SIDE){
			if(chooseCorner == RIGHT_UP_CORNER && tapPoint.y<y){
				chooseCorner = RIGHT_BOTTOM_CORNER;
				tapPoint.y = tapPoint.y+height;
				}
			if(chooseCorner == RIGHT_BOTTOM_CORNER && tapPoint.y>height+y){
				chooseCorner = RIGHT_UP_CORNER;
				tapPoint.y = tapPoint.y-height;
			}
		}else{
			if(chooseCorner == LEFT_UP_CORNER && tapPoint.y<y){
			chooseCorner = LEFT_BOTTOM_CORNER;
			tapPoint.y = tapPoint.y+height;
			}
		   if(chooseCorner == LEFT_BOTTOM_CORNER && tapPoint.y>height+y){
			chooseCorner = LEFT_UP_CORNER;
			tapPoint.y = tapPoint.y-height;
		   }
		}
		switch(chooseCorner){
		  case LEFT_UP_CORNER:{
			px = x;
			py = y;
			ox = x;
			oy = y+height;
			mXIntersectionPoint.y = py;
			mYIntersectionPoint.x = ox;
			mYIntersectionPoint.y = oy;
			direction = true;
			break;
		  }
		 case LEFT_BOTTOM_CORNER:{
			px = x;
			py = y+height;
			ox = x;
			oy = y;
			mXIntersectionPoint.y = py;
			mYIntersectionPoint.x = ox;
			mYIntersectionPoint.y = oy;	
			direction = false;
			break;
		  }
		case RIGHT_UP_CORNER:{
			px = width+x;
			py = y;
			ox = width+x;
			oy = height+y;
			mXIntersectionPoint.y = py;
			mYIntersectionPoint.x = ox;
			mYIntersectionPoint.y = oy;
			direction = true;
			break;
		  }
		case RIGHT_BOTTOM_CORNER:{
			px = width+x;
			py = height+y;
			ox = width+x;
			oy = y;
			mXIntersectionPoint.y = py;
			mYIntersectionPoint.x = ox;
			mYIntersectionPoint.y = oy;
			direction = false;
			break;
		  }
		}	

		if(tapPoint.y==py) {
		 mXIntersectionPoint.x = (tapPoint.x+px)/2;
		 mXIntersectionPoint.y = py;
		 mPbPoint.x = mXIntersectionPoint.x;
		 mPbPoint.y = mXIntersectionPoint.y;
		 mPcbPoint.x = mXIntersectionPoint.x;
		 mPcbPoint.y = mXIntersectionPoint.y;
		 mPcPoint.x = mXIntersectionPoint.x;
		 mPcPoint.y = mXIntersectionPoint.y; 
		 mPcnPoint.x = mXIntersectionPoint.x;
		 mPcnPoint.y = mXIntersectionPoint.y;
		 mPnPoint.x = mXIntersectionPoint.x;
		 mPnPoint.y = mXIntersectionPoint.y; 
		 mPb2Point.x = ox;
		 mPb2Point.y = oy;
		 mPcb2Point.x = mPb2Point.x;
		 mPcb2Point.y = oy; 
		 mPc2Point.x = mXIntersectionPoint.x;
		 mPc2Point.y = oy;
		 mPcn2Point.x = mPc2Point.x;
		 mPcn2Point.y = mPc2Point.y;
		 mPn2Point.x = tapPoint.x;
		 mPn2Point.y = oy;
		 mSdnPoint.x = mXIntersectionPoint.x-30;
		 mSdnPoint.y = py;
		 angle = 0;
		 shadowAngle = 0;
		 return ; 
		 }
		
		MNLine AP = MNLine.initLine(tapPoint.x, tapPoint.y, px, py);
		//控制直线AP的斜率，也就是控制tappoint.y的值使其斜率保持在小于1，也就是45度。
		if(!blo.isAutoFlip()){
		  if(Math.abs(AP.getA())>1) {
			  tapPoint.y = py + (AP.getA()>0?tapPoint.x-px+1:-(tapPoint.x-px+1))+y;
			  AP.change(tapPoint.x, tapPoint.y, px, py);
		    }
		}
		MNLine BC = AP.getPBLine((tapPoint.x+px)/2, (tapPoint.y+py)/2);
		mXIntersectionPoint.x = BC.getXbyY(mXIntersectionPoint.y);
		R = MNLine.twoPointDistance(tapPoint, mXIntersectionPoint)/2;	
		MNLine PbPn = BC.LineTranslationWidthDistance((float)(R*Math.PI/2),direction);
		MNLine CnCb = BC.LineTranslationWidthDistance((float)(R*Math.PI/2-R), direction);
		MNLine AB = MNLine.initLine(tapPoint.x, tapPoint.y, mXIntersectionPoint.x, mXIntersectionPoint.y);
		mPnPoint = PbPn.getCross(AB);
		mPbPoint.x = PbPn.getXbyY(py);
		mPbPoint.y = py;
		if(direction){
		AP.change(mPbPoint.x, mPbPoint.y);
		MNLine Cn2Cb2 = CnCb.LineTranslationWidthDistance(30, false);
		mSdnPoint = AP.getCross(Cn2Cb2);
		}
		mPcnPoint = CnCb.getCross(AB);
		mPcbPoint.x = CnCb.getXbyY(py);
		mPcbPoint.y = py;
		mPcPoint = MNLine.getMidPoint(mPcnPoint, mPcbPoint);
		mYIntersectionPoint.y = BC.getYbyX(mYIntersectionPoint.x);
		MNLine AC = MNLine.initLine(tapPoint.x, tapPoint.y, mYIntersectionPoint.x, mYIntersectionPoint.y);
		mPn2Point = PbPn.getCross(AC);
		mPb2Point.y = PbPn.getYbyX(px);
		mPb2Point.x = px;
		mPcn2Point = CnCb.getCross(AC);
		mPcb2Point.y = CnCb.getYbyX(px);
		mPcb2Point.x = px;
		mPc2Point = MNLine.getMidPoint(mPcn2Point, mPcb2Point);
		
		if(!direction){
			PointF temp =  new PointF();
		temp.x = CnCb.getXbyY(oy);
		temp.y = oy;
		if(mPb2Point.y>y)	
			AP.change(mPb2Point.x, mPb2Point.y);
		else
			AP.change(temp.x, temp.y);
		
		MNLine Cn2Cb2 = CnCb.LineTranslationWidthDistance(30, true);
		mSdnPoint = AP.getCross(Cn2Cb2);
		}
		angle = (float)(Math.atan(AB.getA())*180/Math.PI);
		shadowAngle = (float)(Math.atan(BC.getA())*180/Math.PI);
			if(shadowAngle>0){
				shadowAngle = shadowAngle-90;
			}else{
				shadowAngle = shadowAngle+90;
			}
		
	}

	public void setMaskPage(boolean flag){
		isMask = flag;
	}
	
	public void setLookPage(boolean flag){
		isLookPage = flag;
		isMask = !flag;
	}
	
	public void reset(){
		clearPosition();
		angle = 0;
		shadowAngle = 0;
		shadowLength = 0;
		fx = x;
		fy = y;
		sdnx =  -1;
		sdny =  -1;
		mBackPagePath = null;
		mNextPagePath=null;
		pp = null;
		isMask = true;
		isLookPage = false;
	}
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	private void clearPosition(){
		tapPoint.x = -1;
		tapPoint.y = -1;
		mPb2Point.x = -1;
		mPb2Point.y = -1;
		mPbPoint.x = -1;
		mPbPoint.y = -1;
		mPc2Point.x = -1;;
		mPc2Point.y= -1;
		mPcb2Point.x = -1;
		mPcb2Point.y = -1;
		mPcbPoint.x = -1;
		mPcbPoint.y = -1;
		mPcn2Point.x = -1;
		mPcn2Point.y = -1;
		mPcnPoint.x = -1;
		mPcnPoint.y = -1;
		mPcPoint.x  =-1;
		mPcPoint.y = -1;
		mPn2Point.x = -1;
		mPn2Point.y = -1;
		mPnPoint.x = -1;
		mPnPoint.y = -1;
		mSdnPoint.x = -1;
		mSdnPoint.y = -1;
	}
}
