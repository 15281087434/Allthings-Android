package songqiu.allthings.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextVerifyUtil {

    private static int getLength(String s) {
		if (s != null) {
			final String regularExpression = "[^\\x00-\\xFF]";
			return s.replaceAll(regularExpression, "**").length();
		}
		return 0;
    }


	public static boolean checkMobile(String phone) {
    	return TextVerifyUtil.validateMobilePhoneNum(phone);
	}

    public static boolean validateMobilePhoneNum(final String s){
		if (StringUtil.isEmptyString(s)) {
//			throw new ValidateException("手机号码格式不正确");
			ToastUtil.showToast("手机号码格式不正确");
			return false;
		}

		if (s.length() != 11) {
//			throw new ValidateException("手机号码格式不正确");
            ToastUtil.showToast("手机号码格式不正确");
			return false;
		}

		final String regularExpression = "^1[3456789][0-9]{9}$";  // ^1[34578]\d{9}$
		if (!s.matches(regularExpression)) {
            ToastUtil.showToast("手机号码格式不正确");
			return false;
		}
		return true;
    }

    public static boolean startWithDigit(String s) {
		if (s != null && s.length() > 0) {
			char c = s.charAt(0);
			if (c >= '0' && c <= '9')
			return true;
		}
		return false;
    }

	public static boolean isContainChinese(String str) {
		if (StringUtil.isEmptyString(str))
			return false;
//		Pattern p = Pattern.compile("[\u4e00-\u9fa5|]");
		Pattern p = Pattern.compile("^[\\u4E00-\\u9FA5]{1,16}(?:·[\\u4E00-\\u9FA5]{1,16})*$");
		Matcher m = p.matcher(str);
		return m.find();
	}



	public static class ValidateException extends Exception {
		public ValidateException(String detailMessage) {
			super(detailMessage);
		}
    }
}
