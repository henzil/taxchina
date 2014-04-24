package netlib.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class TouchUtil {
	public static void createTouchDelegate(final View view, final int expandTop, final int expandBottom, final int expandLeft, final int expandRight) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {
			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= expandTop;
				r.bottom += expandBottom;
				r.left -= expandLeft;
				r.right += expandRight;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}

	public static void createTouchDelegate(final View view, final int expandTouchWidth) {
		final View parent = (View) view.getParent();
		parent.post(new Runnable() {
			public void run() {
				final Rect r = new Rect();
				view.getHitRect(r);
				r.top -= expandTouchWidth;
				r.bottom += expandTouchWidth;
				r.left -= expandTouchWidth;
				r.right += expandTouchWidth;
				parent.setTouchDelegate(new TouchDelegate(r, view));
			}
		});
	}
}
