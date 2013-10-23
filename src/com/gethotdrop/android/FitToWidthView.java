package com.gethotdrop.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class FitToWidthView extends ImageView {

	public FitToWidthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override protected void onMeasure(int widthMeasureSpec,
			   int heightMeasureSpec) {
			//   let the default measuring occur, then force the desired aspect ratio 
			//   on the view (not the drawable).
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		       
	        Drawable d = getDrawable();

	        if (d != null && d.getIntrinsicWidth() != 0) {
	                // ceil not round - avoid thin vertical gaps along the left/right edges
	                int width = getMeasuredWidth();
	                int height = Math.round(.75f * width);
	                setMeasuredDimension(width, height);
	        } else {
	                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        }
	}
}