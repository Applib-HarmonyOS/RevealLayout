package per.goweii.reveallayout;

import ohos.agp.animation.AnimatorValue;

public class ValueAnimator extends AnimatorValue {
    private float start = 0;
    private float end = 1;
    private ValueUpdateListener myValueUpdateListener;

    private ValueAnimator() {
        super();
    }

    /**
     * * ValueAnimator
     *
     * @param start float
     * @param end   float
     * @return SlidingUpBuilder
     */
    public static ValueAnimator ofFloat(float start, float end) {
        ValueAnimator myValueAnimator = new ValueAnimator();
        myValueAnimator.start = start;
        myValueAnimator.end = end;
        return myValueAnimator;
    }

    private ValueUpdateListener valueUpdateListener = new ValueUpdateListener() {
        @Override
        public void onUpdate(AnimatorValue animatorValue, float value) {
            float val = value;
            val = val * (end - start) + start;
            if (myValueUpdateListener != null) {
                myValueUpdateListener.onUpdate(animatorValue, val);
            }
        }
    };

    /**
     * * Constructor
     *
     * @param startValue float
     * @param endValue   float
     */
    public void setFloatValues(float startValue, float endValue) {
        this.start = startValue;
        this.end = endValue;
    }

    @Override
    public void setValueUpdateListener(ValueUpdateListener listener) {
        this.myValueUpdateListener = listener;
        super.setValueUpdateListener(valueUpdateListener);
    }

    /**
     * * setFloatValues
     *
     * @param interpolator integer
     */
    public void setInterpolator(int interpolator) {
        //Do Nothing
    }

    /**
     * * ofFloat
     *
     * @return SlidingUpBuilder
     */
    public static ValueAnimator ofFloat() {
        ValueAnimator myValueAnimator = new ValueAnimator();
        return myValueAnimator;
    }

    public float getStart() {
        return start;
    }

    public float getEnd() {
        return end;
    }
}

