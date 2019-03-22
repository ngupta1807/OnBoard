package menuapp.activity.setdata;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ToggleRadioButton extends RadioButton {

	public ToggleRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// Implement necessary constructors
	@Override
	public void toggle() {
		if (isChecked()) {
			if (getParent() instanceof RadioGroup) {
				((RadioGroup) getParent()).clearCheck();
			}
		} else {
			setChecked(true);
		}
	}
}
