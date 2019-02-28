package com.itc.smartbroadcast.util;

/**
 * 字符串工具类
 */
public class StringUtil {

	/**
	 * 替换字符串
	 */
	public static String replaceString(String source) {
		if (StringUtil.isEmpty(source))
			return "";
		source = source.replace('\'', '\"');
		source = source.replace("\\[", "\\【");
		source = source.replace("\\]", "\\】");
		source = source.replace("\\<", "\\《");
		source = source.replace("\\>", "\\》");
		return source;
	}

	/**
	 * 判断文本为空
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0||str.equals(""));
	}

	/**
	 * 只包含数字和字母
	 */
	public static boolean isNumberLetters(String str) {
		if (str == null || str.length() == 0)
			return true;

		return !str.matches("[a-zA-Z0-9]+");
	}

	/**
	 * 只包含数字字母和中文
	 *
	 */
	public static boolean isNumberLettersCharacter(String str) {
		if (str == null || str.length() == 0)
			return true;

		return !str.matches("[0-9a-zA-Z\\u4e00-\\u9fa5]+");
	}
}