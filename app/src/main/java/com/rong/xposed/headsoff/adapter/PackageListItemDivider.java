package com.rong.xposed.headsoff.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rong.xposed.headsoff.R;


public class PackageListItemDivider extends RecyclerView.ItemDecoration {

	private static final int[] ATTRS = new int[]{
			R.attr.packageListItemDivider
	};

	private Drawable mDivider;

	private int mOrientation = -1;

	public PackageListItemDivider(Context context) {
		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
		a.recycle();
	}


	private int getOrientation(RecyclerView parent) {
		if (mOrientation == -1) {
			if (parent.getLayoutManager() instanceof LinearLayoutManager) {
				LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
				mOrientation = layoutManager.getOrientation();
			} else {
				throw new IllegalStateException(
						"DividerItemDecoration can only be used with a LinearLayoutManager.");
			}
		}
		return mOrientation;
	}


	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		if (mDivider == null) {
			super.onDrawOver(c, parent, state);
			return;
		}

		int left = 0, right = 0, top = 0, bottom = 0, size;
		int orientation = mOrientation != -1 ? mOrientation : getOrientation(parent);
		int childCount = parent.getChildCount();

		if (orientation == LinearLayoutManager.VERTICAL) {
			size = mDivider.getIntrinsicHeight();
			left = parent.getPaddingLeft();
			right = parent.getWidth() - parent.getPaddingRight();
		} else { 
			size = mDivider.getIntrinsicWidth();
			top = parent.getPaddingTop();
			bottom = parent.getHeight() - parent.getPaddingBottom();
		}

		for (int i = 1; i < childCount; i++) {
			View child = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

			if (orientation == LinearLayoutManager.VERTICAL) {
				top = child.getTop() - params.topMargin - size;
				bottom = top + size;
			} else { 
				left = child.getLeft() - params.leftMargin;
				right = left + size;
			}
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
							   RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (mDivider == null) {
			return;
		}

		int position = parent.getChildAdapterPosition(view);
		if (position == RecyclerView.NO_POSITION || position == 0) {
			return;
		}

		if (mOrientation == -1)
			getOrientation(parent);

		if (mOrientation == LinearLayoutManager.VERTICAL) {
			outRect.top = mDivider.getIntrinsicHeight();//opp
		} else {
			outRect.left = mDivider.getIntrinsicWidth();
		}
	}

}
