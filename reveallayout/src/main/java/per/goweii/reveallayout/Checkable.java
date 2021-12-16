package per.goweii.reveallayout;

public interface Checkable {
    void setChecked(boolean checked);

    void ComponentPosition(boolean changed, int left, int top, int right, int bottom);

    boolean isChecked();
    void toggle();
}
