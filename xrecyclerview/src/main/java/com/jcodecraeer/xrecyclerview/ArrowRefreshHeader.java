package com.jcodecraeer.xrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;

import java.util.Date;

public class ArrowRefreshHeader extends LinearLayout implements BaseRefreshHeader {

    private static final String XR_REFRESH_KEY = "XR_REFRESH_KEY";
    private static final String XR_REFRESH_TIME_KEY = "XR_REFRESH_TIME_KEY";
    private String[] content={
                                "网络赌博不可取，盲目借贷上岸难",
                                "不要以贷养贷，及时止损失利益最大化的途径",
                                "借款单数多的时候，可以在贷款账单中记账管理",
                                "信用管家，你信用数据的管家",
                                "合理的借还款频率，有助于建立良好的个人信用",
                                "前三位贷款都是内部通道，一般人不告诉!",
                                "申请3家以上贷款，通过率高达99%"
    };
    private int contentIndex=0;
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private TextView contentText;
	private SimpleViewSwitcher mProgressBar;
	private TextView mStatusTextView;
	private int mState = STATE_NORMAL;

	private TextView mHeaderTimeView;
	private LinearLayout mHeaderRefreshTimeContainer;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private static final int ROTATE_ANIM_DURATION = 180;

	public int mMeasuredHeight;
    private AVLoadingIndicatorView progressView;

	public void destroy(){
        mProgressBar = null;
        if(progressView != null){
            progressView.destroy();
            progressView = null;
        }
	    if(mRotateUpAnim != null){
            mRotateUpAnim.cancel();
            mRotateUpAnim = null;
        }
        if(mRotateDownAnim != null){
            mRotateDownAnim.cancel();
            mRotateDownAnim = null;
        }
    }

	public ArrowRefreshHeader(Context context) {
		super(context);
		initView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ArrowRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public void setRefreshTimeVisible(boolean show){
	    if(mHeaderRefreshTimeContainer != null){
        mHeaderRefreshTimeContainer.setVisibility(show?VISIBLE:GONE);
      }

    }

	private void initView() {
		// 初始情况，设置下拉刷新view高度为0
		mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.listview_header, null);

        mHeaderRefreshTimeContainer
                = (LinearLayout) mContainer.findViewById(R.id.header_refresh_time_container);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
		this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

		addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.listview_header_arrow);
		mStatusTextView = (TextView)findViewById(R.id.refresh_status_textview);
    contentText=findViewById(R.id.text_content);
        //init the progress view
		mProgressBar = (SimpleViewSwitcher)findViewById(R.id.listview_header_progressbar);
        progressView = new  AVLoadingIndicatorView(getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        if(mProgressBar != null){
          mProgressBar.setView(progressView);
        }


		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
		
		mHeaderTimeView = (TextView)findViewById(R.id.last_refresh_time);
		measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		mMeasuredHeight = getMeasuredHeight();
	}

    public void setProgressStyle(int style) {
        if(style == ProgressStyle.SysProgress){
            if(mProgressBar != null)
                mProgressBar.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        }else{
            progressView = new AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            mProgressBar.setView(progressView);
        }
    }

    public void setArrowImageView(int resid){
        mArrowImageView.setImageResource(resid);
    }

	public void setState(int state) {
		if (state == mState) return ;

		if (state == STATE_REFRESHING) {	// 显示进度
			//mArrowImageView.clearAnimation();
			//mArrowImageView.setVisibility(View.INVISIBLE);
            if(mProgressBar != null)
			   // mProgressBar.setVisibility(View.VISIBLE);
            smoothScrollTo(mMeasuredHeight);
		} else if(state == STATE_DONE) {
            //mArrowImageView.setVisibility(View.INVISIBLE);
            //if(mProgressBar != null)
               // mProgressBar.setVisibility(View.INVISIBLE);
        } else {	// 显示箭头图片
			//mArrowImageView.setVisibility(View.VISIBLE);
			if(mProgressBar != null){
                //mProgressBar.setVisibility(View.INVISIBLE);
            }
		}
        mHeaderTimeView.setText(friendlyTime(getLastRefreshTime()));
		switch(state){
            case STATE_NORMAL:
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    //mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    //mArrowImageView.clearAnimation();

                }
                mStatusTextView.setText(R.string.listview_header_hint_normal);
                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH) {
//                    mArrowImageView.clearAnimation();
//                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mStatusTextView.setText(R.string.listview_header_hint_release);
                }
                break;
            case STATE_REFRESHING:
                mStatusTextView.setText(R.string.refreshing);
                Glide.with(getContext()).load(R.drawable.refresh_loading_gif).into(new GlideDrawableImageViewTarget(mArrowImageView));
                break;
            case STATE_DONE:
                mStatusTextView.setText(R.string.refresh_done);
                mArrowImageView.setImageResource(R.drawable.refresh_loading_gif);
              contentIndex++;
              if(contentIndex>content.length-1){
                contentIndex=0;
              }
                break;
            default:
		}
		
		mState = state;
	}

    public int getState() {
        return mState;
    }

    private long getLastRefreshTime(){
        SharedPreferences s =
                getContext()
                    .getSharedPreferences(XR_REFRESH_KEY,Context.MODE_APPEND);
        return s.getLong(XR_REFRESH_TIME_KEY,new Date().getTime());
    }

    private void saveLastRefreshTime(long refreshTime){
        SharedPreferences s =
                getContext()
                    .getSharedPreferences(XR_REFRESH_KEY,Context.MODE_APPEND);
        s.edit().putLong(XR_REFRESH_TIME_KEY,refreshTime).commit();
    }

    @Override
	public void refreshComplete(){
        mHeaderTimeView.setText(friendlyTime(getLastRefreshTime()));
        saveLastRefreshTime(System.currentTimeMillis());
        new Handler().postDelayed(new Runnable(){
            @Override public void run() {
                setState(STATE_DONE);
                reset();
            }
        }, 2000);
	}

	public void setVisibleHeight(int height) {
		if (height < 0) height = 0;
		LayoutParams lp = (LayoutParams) mContainer .getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		return lp.height;
	}

    @Override
    public void onMove(float delta) {
        if(getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if(getVisibleHeight()<mArrowImageView.getMeasuredHeight()){
                mArrowImageView.setScaleX(((float) getVisibleHeight())/mArrowImageView.getMeasuredHeight());
                mArrowImageView.setScaleY(((float) getVisibleHeight())/mArrowImageView.getMeasuredHeight());
                contentText.setVisibility(View.INVISIBLE);
            }else{
              contentText.setVisibility(View.VISIBLE);
              contentText.setText(content[contentIndex]);
            }
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mArrowImageView.getMeasuredHeight()) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <=  mMeasuredHeight) {
            //return;
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    public static String friendlyTime(Date time) {
        return friendlyTime(time.getTime());
    }

    public static String friendlyTime(long time) {
        //获取time距离当前的秒数
        int ct = (int)((System.currentTimeMillis() - time)/1000);

        if(ct == 0) {
            return "刚刚";
        }

        if(ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if(ct >= 60 && ct < 3600) {
            return Math.max(ct / 60,1) + "分钟前";
        }
        if(ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if(ct >= 86400 && ct < 2592000){ //86400 * 30
            int day = ct / 86400 ;
            return day + "天前";
        }
        if(ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

}