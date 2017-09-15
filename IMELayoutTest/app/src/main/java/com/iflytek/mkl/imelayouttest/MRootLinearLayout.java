package com.iflytek.mkl.imelayouttest;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewStructure;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

/**
 * Created by makunle on 2017/9/14.
 */

public class MRootLinearLayout extends LinearLayout {

    private static final String TAG = "MRootLinearLayout";

    public MRootLinearLayout(Context context) {
        super(context);
    }

    public MRootLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MRootLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
    }

    @Override
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        Log.d(TAG, "onRequestSendAccessibilityEvent: ");
        return super.onRequestSendAccessibilityEvent(child, event);
    }

    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        Log.d(TAG, "onResolvePointerIcon: ");
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptHoverEvent: ");
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: ");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        Log.d(TAG, "onRequestFocusInDescendants: ");
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override
    public boolean onNestedPrePerformAccessibilityAction(View target, int action, Bundle args) {
        Log.d(TAG, "onNestedPrePerformAccessibilityAction: ");
        return super.onNestedPrePerformAccessibilityAction(target, action, args);
    }

    @Override
    public void onViewAdded(View child) {
        Log.d(TAG, "onViewAdded: ");
        super.onViewAdded(child);
    }

    @Override
    public void onViewRemoved(View child) {
        Log.d(TAG, "onViewRemoved: ");
        super.onViewRemoved(child);
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: ");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        super.onDetachedFromWindow();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        Log.d(TAG, "onCreateDrawableState: ");
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d(TAG, "onStartNestedScroll: ");
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.d(TAG, "onNestedScrollAccepted: ");
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.d(TAG, "onStopNestedScroll: ");
        super.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll: ");
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d(TAG, "onNestedPreScroll: ");
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "onNestedFling: ");
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "onNestedPreFling: ");
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ");
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + changed + " " + l + " " + t + " " + r + " " + b);
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        Log.d(TAG, "onRtlPropertiesChanged: ");
        super.onRtlPropertiesChanged(layoutDirection);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        Log.d(TAG, "onFocusChanged: ");
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onPopulateAccessibilityEvent: ");
        super.onPopulateAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onInitializeAccessibilityEvent: ");
        super.onInitializeAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        Log.d(TAG, "onInitializeAccessibilityNodeInfo: ");
        super.onInitializeAccessibilityNodeInfo(info);
    }

    @Override
    public void onProvideStructure(ViewStructure structure) {
        Log.d(TAG, "onProvideStructure: ");
        super.onProvideStructure(structure);
    }

    @Override
    public void onProvideVirtualStructure(ViewStructure structure) {
        Log.d(TAG, "onProvideVirtualStructure: ");
        super.onProvideVirtualStructure(structure);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        Log.d(TAG, "onApplyWindowInsets: ");
        return super.onApplyWindowInsets(insets);
    }

    @Override
    public void onStartTemporaryDetach() {
        Log.d(TAG, "onStartTemporaryDetach: ");
        super.onStartTemporaryDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        Log.d(TAG, "onFinishTemporaryDetach: ");
        super.onFinishTemporaryDetach();
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        Log.d(TAG, "onFilterTouchEventForSecurity: ");
        return super.onFilterTouchEventForSecurity(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d(TAG, "onWindowFocusChanged: ");
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged: ");
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDisplayHint(int hint) {
        Log.d(TAG, "onDisplayHint: ");
        super.onDisplayHint(hint);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        Log.d(TAG, "onWindowVisibilityChanged: ");
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        Log.d(TAG, "onVisibilityAggregated: ");
        super.onVisibilityAggregated(isVisible);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyPreIme: ");
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: ");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyLongPress: ");
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: ");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        Log.d(TAG, "onKeyMultiple: ");
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyShortcut: ");
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        Log.d(TAG, "onCheckIsTextEditor: ");
        return super.onCheckIsTextEditor();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        Log.d(TAG, "onCreateInputConnection: ");
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        Log.d(TAG, "onCreateContextMenu: ");
        super.onCreateContextMenu(menu);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        Log.d(TAG, "onTrackballEvent: ");
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "onGenericMotionEvent: ");
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Log.d(TAG, "onHoverEvent: ");
        return super.onHoverEvent(event);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        Log.d(TAG, "onHoverChanged: ");
        super.onHoverChanged(hovered);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.d(TAG, "onScrollChanged: ");
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged: ");
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        Log.d(TAG, "onScreenStateChanged: ");
        super.onScreenStateChanged(screenState);
    }

    @Override
    public void onCancelPendingInputEvents() {
        Log.d(TAG, "onCancelPendingInputEvents: ");
        super.onCancelPendingInputEvents();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "onSaveInstanceState: ");
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate: ");
        super.onFinishInflate();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        Log.d(TAG, "onDrawForeground: ");
        super.onDrawForeground(canvas);
    }

    @Override
    protected void onAnimationStart() {
        Log.d(TAG, "onAnimationStart: ");
        super.onAnimationStart();
    }

    @Override
    protected void onAnimationEnd() {
        Log.d(TAG, "onAnimationEnd: ");
        super.onAnimationEnd();
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        Log.d(TAG, "onSetAlpha: ");
        return super.onSetAlpha(alpha);
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        Log.d(TAG, "onWindowSystemUiVisibilityChanged: ");
        super.onWindowSystemUiVisibilityChanged(visible);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        Log.d(TAG, "onDragEvent: ");
        return super.onDragEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Log.d(TAG, "onOverScrolled: ");
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
}
