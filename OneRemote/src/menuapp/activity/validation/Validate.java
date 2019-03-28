package menuapp.activity.validation;

import java.util.regex.Pattern;

import menuapp.activity.util.CustomAlertDialog;

import android.content.Context;
import android.text.TextUtils;

public class Validate {
	CustomAlertDialog am;

	public Validate(Context con) {
		am = new CustomAlertDialog(con);
	}

	public Boolean reg(String email, String reg_password, String cpwd) {
		Boolean status = false;
		if (email.equals("")) {
			am.showValidate("Please input email.", true);
			status = true;
		} else if (isValidEmail(email) == false) {
			am.showValidate("Please input valid email.", true);
			status = true;
		} else if (reg_password.equals("")) {
			am.showValidate("Please input password.", true);
			status = true;
		} else if (reg_password.length() < 6) {
			am.showValidate("Please input password at least of 6 characters.",
					true);
			status = true;
		} else if (cpwd.equals("")) {
			am.showDialog("Please input confirm password.", true);
			status = true;
		} else if (cpwd.length() < 6) {
			am.showValidate(
					"Please input confirm password at least of 6 characters.",
					true);
			status = true;
		} else if (!cpwd.equals(reg_password)) {
			am.showValidate(
					"Password and confirm password is not same! Please input same password.",
					true);
			status = true;
		} else {
			status = false;
		}
		return status;

	}

	public Boolean login(String email, String pwd) {
		Boolean status = false;
		if (email.equals("")) {
			am.showValidate("Please input email.", true);
			status = true;
		} else if (isValidEmail(email) == false) {
			am.showValidate("Please input valid email.", true);
			status = true;
		} else if (pwd.equals("")) {
			am.showValidate("Please input password.", true);
			status = true;
		} else if (pwd.length() < 6) {
			am.showValidate("Please input password at least of 6 characters.",
					true);
			status = true;
		} else {
			status = false;
		}
		return status;

	}

	public boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
}
