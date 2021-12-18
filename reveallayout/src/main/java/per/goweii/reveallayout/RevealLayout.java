package per.goweii.reveallayout;

import java.util.logging.Logger;
import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.animation.AnimatorValue.ValueUpdateListener;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.Component.TouchEventListener;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.StackLayout;
import ohos.agp.render.Path;
import ohos.agp.render.Path.Direction;
import ohos.app.Context;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;
import per.goweii.reveallayout.utils.AttrUtils;

/**
 * Reveal the effect layout
 * You can specify 2 sub-layouts to switch the selected state with a circular reveal effect
 *
 * @author Cuizhen
 * @date 2018/9/25
 */
public class RevealLayout extends StackLayout implements Checkable, ValueUpdateListener, Animator.StateChangedListener,
        TouchEventListener {

    private static final String TAG = RevealLayout.class.getSimpleName();
    private Component mCheckedView;
    private Component mUncheckedView;
    private int mCheckedLayoutId = 0;
    private int mUncheckedLayoutId = 0;
    private int mAnimDuration = 500;
    private boolean mCheckWithExpand = true;
    private boolean mUncheckWithExpand = false;
    private boolean mAllowRevert = false;
    private boolean mHideBackView = true;
    private boolean mChecked = false;
    private float mCenterX = 0F;
    private float mCenterY = 0F;
    private float mRevealRadius = 0F;
    private float mStart = 0F;
    private float mEnd = 0F;
    private final Path mPath = new Path();
    private AnimatorValue mAnimator;
    private static final String VIEW_CHECKED = "rl_checkedLayout";
    private static final String VIEW_UNCHECKED = "rl_uncheckedLayout";

    /**
     *It is a constructor call for RevealLayout.
     *
     * @param context Context
     */
    public RevealLayout(Context context) {
        this(context, null);
        setTouchEventListener(this);
    }

    /**
     * It is a constructor call for RevealLayout.
     *
     * @param context Context
     * @param attrs AttrSet
     */
    public RevealLayout(Context context, AttrSet attrs) {
        this(context, attrs, 0);
        setTouchEventListener(this);
    }

    /**
     * It is a constructor call for RevealLayout.
     *
     * @param context Context
     * @param attrs AttrSet
     * @param defStyleAttr int
     */
    public RevealLayout(Context context, AttrSet attrs, int defStyleAttr) {
        super(context, attrs);
        initAttr(attrs);
        initView(context);
        setTouchEventListener(this);
        setClickedListener(this);
    }

    /**
     * Registers a listener for click events in the component.
     * When the position of a click is within the component's display area, a click event is triggered,
     * and all registered observers are notified.
     *
     * @param getCurvedTimeRevealLayout RevealLayout
     */
    private void setClickedListener(RevealLayout getCurvedTimeRevealLayout) {
        getCurvedTimeRevealLayout.toggle();
    }

    /**
     * Get the attributes carried by the layout file, and the subclass overwrites this method to get the attributes
     * defined by the subclass.
     * After obtaining the subclass attributes, it can be used in {#createCheckedView()} and {#createUncheckedView()}
     *
     * @param attrs AttributeSet
     */
    protected void initAttr(AttrSet attrs) {
        mChecked = AttrUtils.getBooleanFromAttr(attrs, "rl_checked", mChecked);
        mAnimDuration = AttrUtils.getIntFromAttr(attrs, "rl_animDuration", mAnimDuration);
        mCheckWithExpand = AttrUtils.getBooleanFromAttr(attrs, "rl_checkWithExpand", mCheckWithExpand);
        mUncheckWithExpand = AttrUtils.getBooleanFromAttr(attrs, "rl_uncheckWithExpand", mUncheckWithExpand);
        mAllowRevert = AttrUtils.getBooleanFromAttr(attrs, "rl_allowRevert", mAllowRevert);
        mHideBackView = AttrUtils.getBooleanFromAttr(attrs, "rl_hideBackView", mHideBackView);
        if (null != attrs) {
            boolean isPresent = attrs.getAttr(VIEW_CHECKED).isPresent();
            String viewString = "";
            if (isPresent) {
                viewString = attrs.getAttr(VIEW_CHECKED).get().getStringValue();
                if (viewString.contains(":")) {
                    mCheckedLayoutId = Integer.valueOf(viewString.split(":")[1]);
                }
            }
            isPresent = attrs.getAttr(VIEW_UNCHECKED).isPresent();
            if (isPresent) {
                viewString = attrs.getAttr(VIEW_UNCHECKED).get().getStringValue();
                if (viewString.contains(":")) {
                    mUncheckedLayoutId = Integer.valueOf(viewString.split(":")[1]);
                }
            }
        }
    }

    /**
     * Initialize the selected and unselected state controls, and set the default state
     */
    protected void initView(Context context) {
        removeAllComponents();
        if (mCheckedView == null) {
            mCheckedView = createCheckedView(context);
        }
        if (mUncheckedView == null) {
            mUncheckedView = createUncheckedView(context);
        }
        ComponentContainer.LayoutConfig checkParams = mCheckedView.getLayoutConfig();
        if (checkParams == null) {
            checkParams = getDefaultLayoutParams();
        }
        ComponentContainer.LayoutConfig uncheckParams = mUncheckedView.getLayoutConfig();
        if (uncheckParams == null) {
            uncheckParams = getDefaultLayoutParams();
        }
        addComponent(mCheckedView, getChildCount(), checkParams);
        addComponent(mUncheckedView, getChildCount(), uncheckParams);
        showTwoView();
        bringFrontView();
        hideBackView();
    }

    /**
     * To set the layout with width and height.
     *
     * @return LayoutConfig values.
     */
    private LayoutConfig getDefaultLayoutParams() {
        return new LayoutConfig(LayoutConfig.MATCH_PARENT, LayoutConfig.MATCH_PARENT);
    }

    /**
     * Create a selected control, subclasses can override this method, and initialize their own control cre
     *
     * @return Selected control
     */
    protected Component createCheckedView(Context context) {
        Component checkedView;
        if (getCheckedLayoutId() > 0) {
            checkedView = LayoutScatter.getInstance(getContext()).parse(getCheckedLayoutId(), null, false);
        } else {
            checkedView = new Component(getContext());
        }
        return checkedView;
    }

    /**
     * Function will return the checkedview Layout resource.
     *
     * @return mCheckedLayoutId
     */
    protected int getCheckedLayoutId() {
        return mCheckedLayoutId;
    }

    /**
     * Create a non-selected control, the subclass can override this method to initialize its own control.
     *
     * @return Unselected controls
     */
    protected Component createUncheckedView(Context context) {
        Component uncheckedView;
        if (getUncheckedLayoutId() > 0) {
            uncheckedView = LayoutScatter.getInstance(getContext()).parse(getUncheckedLayoutId(), this,
                    false);
        } else {
            uncheckedView = new Component(getContext());
        }
        return uncheckedView;
    }

    /**
     * Function will return the uncheckedview Layout resource.
     *
     * @return mUncheckedLayoutId
     */
    protected int getUncheckedLayoutId() {
        return mUncheckedLayoutId;
    }

    /**
     * Processes a touch event dispatched to a component.
     * The listener responds to a touch event before the target component receives the event.
     *
     * @param component component
     * @param event event
     * @return true/false
     */
    @Override
    public boolean onTouchEvent(Component component, TouchEvent event) {
        int actionIndex = event.getIndex();
        int index = event.getPointerId(actionIndex);
        MmiPoint point = event.getPointerPosition(index);
        float upX = point.getX();
        float upY = point.getY();
        if (mAnimator != null) {
            if (mAllowRevert) {
                simulateClick();
                return true;
            } else {
                return false;
            }
        } else {
            mRevealRadius = 0;
            mCenterX = upX;
            mCenterY = upY;
            simulateClick();
            return true;
        }
    }

    /**
     * Performs a click event in a component.
     *
     * @return Returns true if the callback function is executed successfully.
     */
    @Override
    public boolean simulateClick() {
        toggle();
        return super.simulateClick();
    }

    /**
     * Determine whether the touch position is inside the view and whether it is a legal click
     *
     * @param x Touch point x coordinate
     * @param y Touch point y coordinate
     * @return Is the click legal
     */
    private boolean isValidClick(float x, float y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }

    /**
     * Create reveal animation.
     */
    private AnimatorValue createRevealAnim() {
        float[] value = calculateAnimOfFloat();
        mRevealRadius = value[0];
        mStart = value[0];
        mEnd = value[1];
        AnimatorValue animator = new AnimatorValue();
        animator.setCurveType(Animator.CurveType.CYCLE);
        animator.setDuration(mAnimDuration);
        animator.setValueUpdateListener(this);
        animator.setStateChangedListener(this);
        return animator;
    }

    /**
     * Receives a notification indicating that an animator has started.
     *
     * @param animation animator
     */
    @Override
    public void onStart(Animator animation) {
        resetPath();
        bringCurrentViewToFront();
    }

    /**
     * Receives a notification indicating that an animator has stopped. This method must be called before the onEnd and
     * onCancel methods.
     *
     * @param animator animator
     */
    @Override
    public void onStop(Animator animator) {
        Logger.getLogger(TAG,"Animation stopped");
    }

    /**
     * Receives a notification indicating that an animator has been canceled.
     *
     * @param animator animator
     */
    @Override
    public void onCancel(Animator animator) {
        Logger.getLogger(TAG,"Animation cancelled");
    }

    /**
     * Receives a notification indicating that an animator has ended.
     *
     * @param animator animator
     */
    @Override
    public void onEnd(Animator animator) {
        mAnimator = null;
        bringCurrentViewToFront();
        hideBackView();
        resetCenter();
    }

    /**
     * Receives a notification indicating that an animator is paused.
     *
     * @param animator animator
     */
    @Override
    public void onPause(Animator animator) {
        Logger.getLogger(TAG,"Animation pause");
    }

    /**
     * Receives a notification indicating that an animator is resumed.
     *
     * @param animator animator
     */
    @Override
    public void onResume(Animator animator) {
        Logger.getLogger(TAG,"Animation onresume");
    }

    /**
     *Calculate the onAnimationReverse.
     */
    public void onAnimationReverse() {
        Logger.getLogger(TAG,"Animation reverse");
    }

    /**
     * Calculate the start and end radius of the animation based on the selected state and
     * the diffusion effect of the revealed animation.
     *
     * @return {Starting radius, ending radius}
     */
    private float[] calculateAnimOfFloat() {
        float fromValue;
        float toValue;
        float minRadius = calculateMinRadius();
        float maxRadius = calculateMaxRadius();
        if (mChecked) {
            if (mCheckWithExpand) {
                fromValue = minRadius;
                toValue = maxRadius;
            } else {
                fromValue = maxRadius;
                toValue = minRadius;
            }
        } else {
            if (mUncheckWithExpand) {
                fromValue = minRadius;
                toValue = maxRadius;
            } else {
                fromValue = maxRadius;
                toValue = minRadius;
            }
        }
        return new float[] { fromValue, toValue };
    }

    /**
     * It will reset the path with direction.
     */
    private void resetPath() {
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY, mRevealRadius, Direction.COUNTER_CLOCK_WISE);
    }

    /**
     * Display the current state of the view at the top
     */
    private void bringCurrentViewToFront() {
        showTwoView();
        float minRadius = calculateMinRadius();
        float maxRadius = calculateMaxRadius();
        if (mRevealRadius < (minRadius + maxRadius) / 2F) {
            bringFrontView();
        }
    }

    /**
     * Bringing the views into front.
     */
    private void bringFrontView() {
        if (mChecked) {
            moveChildToFront(mCheckedView);
        } else {
            moveChildToFront(mUncheckedView);
        }
    }

    /**
     * Two views are making into visible.
     */
    private void showTwoView() {
        mCheckedView.setVisibility(VISIBLE);
        mUncheckedView.setVisibility(VISIBLE);
    }

    /**
     * Making the back views invisible.
     */
    private void hideBackView() {
        if (!mHideBackView) {
            return;
        }
        if (mChecked) {
            mUncheckedView.setVisibility(INVISIBLE);
        } else {
            mCheckedView.setVisibility(INVISIBLE);
        }
    }

    /**
     * Calculate the reveal effect to make the radius of the circle larger, and the maximum distance
     * from the center of the circle to the 4 corners
     *
     * @return Minimum radius
     */
    private float calculateMinRadius() {
        float w = getEstimatedWidth();
        float h = getEstimatedHeight();
        float l = getPaddingLeft();
        float t = getPaddingTop();
        float r = getPaddingRight();
        float b = getPaddingBottom();
        float x = Math.max(l - mCenterX, mCenterX - (w - r));
        float y = Math.max(t - mCenterY, mCenterY - (h - b));
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        return (float) Math.hypot(x, y);
    }

    /**
     * Calculate the reveal effect to make the radius of the circle larger, and the maximum distance
     * from the center of the circle to the 4 corners
     *
     * @return Maximum radius
     */
    private float calculateMaxRadius() {
        float w = getEstimatedWidth();
        float h = getEstimatedHeight();
        float l = getPaddingLeft();
        float t = getPaddingTop();
        float r = getPaddingRight();
        float b = getPaddingBottom();
        float x = Math.max(mCenterX - l, w - r - mCenterX);
        float y = Math.max(mCenterY - t, h - b - mCenterY);
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        return (float) Math.hypot(x, y);
    }

    /**
     * Registers a listener for click events in the component.
     *
     * @param onClickListener listener
     */
    @Override
    public void setClickedListener(ClickedListener onClickListener) {
        super.setClickedListener(onClickListener);
    }

    /**
     * Get the current selected state
     *
     * @return Whether selected
     */
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Set selected state
     *
     * @param checked Whether selected
     */
    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mAnimDuration > 0) {
            if (mAnimator != null) {
                onAnimationReverse();
            } else {
                mAnimator = createRevealAnim();
                mAnimator.start();
            }
        } else {
            if (mAnimator != null) {
                mAnimator.cancel();
                mAnimator = null;
            }
            showTwoView();
            bringFrontView();
            hideBackView();
            resetCenter();
        }
    }

    /**
     * Set selected state
     *
     * @param checked  Whether selected
     * @param withAnim Is there an animation
     */
    public void setChecked(boolean checked, boolean withAnim) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (withAnim && mAnimDuration > 0) {
            if (mAnimator != null) {
                onAnimationReverse();
            } else {
                mAnimator = createRevealAnim();
                mAnimator.start();
            }
        } else {
            if (mAnimator != null) {
                mAnimator.cancel();
                mAnimator = null;
            }
            showTwoView();
            bringFrontView();
            hideBackView();
            resetCenter();
        }
    }

    /**
     * Switch selected state, with animation effect
     */
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * It will reset the view with position.
     */
    public void resetCenter() {
        float w = getEstimatedWidth();
        float h = getEstimatedHeight();
        float l = getPaddingLeft();
        float t = getPaddingTop();
        float r = getPaddingRight();
        float b = getPaddingBottom();
        mCenterX = l + ((w - l - r) / 2F);
        mCenterY = t + ((h - t - b) / 2F);
    }

    /**
     * Sets the center coordinates for the display resizing rectangle.
     */
    public void setCenter(float centerX, float centerY) {
        mCenterX = centerX;
        mCenterY = centerY;
    }

    /**
     * Obtains the X coordinate of the center in this display resizing rectangle.
     */
    public float getCenterX() {
        return mCenterX;
    }

    /**
     * Obtains the Y coordinate of the center in this display resizing rectangle.
     */
    public float getCenterY() {
        return mCenterY;
    }

    /**
     * Set the button to revert.
     *
     * @param allowRevert mAllowRevert
     */
    public void setAllowRevert(boolean allowRevert) {
        mAllowRevert = allowRevert;
    }

    /**
     * Set the Animduration which is passed from layout.
     *
     * @param animDuration mAnimDuration
     */
    public void setAnimDuration(int animDuration) {
        mAnimDuration = animDuration;
    }

    /**
     * Set the checked button to expand.
     *
     * @param checkWithExpand mCheckWithExpand
     */
    public void setCheckWithExpand(boolean checkWithExpand) {
        mCheckWithExpand = checkWithExpand;
    }

    /**
     * Set the unchecked button to expand.
     *
     * @param uncheckWithExpand mUncheckWithExpand
     */
    public void setUncheckWithExpand(boolean uncheckWithExpand) {
        mUncheckWithExpand = uncheckWithExpand;
    }

    /**
     * set the view as checked.
     *
     * @param checkedView mCheckedView
     */
    public void setCheckedView(Component checkedView) {
        if (checkedView == null) {
            return;
        }
        if (mCheckedView == checkedView) {
            return;
        }
        mCheckedView = checkedView;
        ComponentContainer.LayoutConfig checkParams = mCheckedView.getLayoutConfig();
        if (checkParams == null) {
            checkParams = getDefaultLayoutParams();
        }
        addComponent(mCheckedView, getChildCount(), checkParams);
        showTwoView();
        bringFrontView();
        hideBackView();
    }

    /**
     * set the view as unchecked.
     *
     * @param uncheckedView mUncheckedView
     */
    public void setUncheckedView(Component uncheckedView) {
        if (uncheckedView == null) {
            return;
        }
        if (mUncheckedView == uncheckedView) {
            return;
        }
        mUncheckedView = uncheckedView;
        ComponentContainer.LayoutConfig uncheckParams = mUncheckedView.getLayoutConfig();
        if (uncheckParams == null) {
            uncheckParams = getDefaultLayoutParams();
        }
        addComponent(mUncheckedView, getChildCount(), uncheckParams);
        showTwoView();
        bringFrontView();
        hideBackView();
    }

    /**
     * Function to set the checked layout with the given id.
     *
     * @param checkedLayoutId mCheckedLayoutId
     */
    public void setCheckedLayoutId(int checkedLayoutId) {
        mCheckedLayoutId = checkedLayoutId;
        setCheckedView(createCheckedView(getContext()));
    }

    /**
     * Function to set the unchecked layout with the given id.
     *
     * @param uncheckedLayoutId mUncheckedLayoutId
     */
    public void setUncheckedLayoutId(int uncheckedLayoutId) {
        mUncheckedLayoutId = uncheckedLayoutId;
        setUncheckedView(createUncheckedView(getContext()));
    }

    /**
     * Receives a notification when a value animator is updated, and returns an output value for customizing animators.
     *
     * @param animatorValue AnimatorValue
     * @param v float
     */
    @Override
    public void onUpdate(AnimatorValue animatorValue, float v) {
        mRevealRadius = v * (mEnd - mStart) + mStart;
        resetPath();
        invalidate();
    }
}
