package by.nalivajr.margosample.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import by.nalivajr.margo.annonatations.BindView;
import by.nalivajr.margo.annonatations.OnClick;
import by.nalivajr.margo.tools.Margoja;
import by.nalivajr.margosample.R;

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */

public class TestInflateView extends LinearLayout {

    @BindView(R.id.tv_press_info)
    private TextView pressInfo;

    public TestInflateView(Context context) {
        super(context);
        init(context, null);
    }

    public TestInflateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TestInflateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TestInflateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        Margoja.inflateAndBind(this, R.layout.layout_test_inflate, true);
    }

    @OnClick(R.id.btn_press_me)
    private void onInfoPressed() {
        pressInfo.setText("Button pressed!");
    }
}
