package com.unibeta.vrules.tools;

import java.util.regex.Pattern;

import com.unibeta.vrules.parsers.ConfigurationProxy;
import com.unibeta.vrules.parsers.ObjectSerializer;
import com.unibeta.vrules.utils.CommonUtils;

/**
 * CommonSyntaxs is a common validation tool delegated by vRules4j engine. All
 * of them can be used in rule's definition expression directly.
 * 
 * For Example: println("this is a println for var1 ",var1,", print other var2:
 * ", var2);
 * 
 * @author jordan.xue
 */
public class CommonSyntaxs {

	private static final String REGEX_DAY = "(?:0[1-9]|1[0-9]|2[0-9]|3[0-1])";
	private static final String REGEX_MONTH = "(?:(?:0[1-9]|1[0-2]))";
	private static final String REGEX_YEAR = "(?!0000)[0-9]{4}";
	private static final String REGEX_HOUR = "(?:0[0-9]|1[0-9]|2[0-4])";
	private static final String REGEX_MININUTE = "(?:0[0-9]|1[0-9]|2[0-9]|4[0-9]|5[0-9]|6[0])";
	private static final String REGEX_SECOND = "(?:0[0-9]|1[0-9]|2[0-9]|4[0-9]|5[0-9]|6[0])";

	private static Boolean enablePrint = null;
	private static Long ruleFileModifiedBeatsCheckInterval = null;
	private static String xclasspath = null;
	private static String xspringboot = null;
	
	/**
	 * Get extra springboot jar for engine runtime dynamic compiling usage. <br>
	 * Can be configured via '-DvRules4j.springboot=D:\app\lib\springboot.jar' in runtime JVM args
	 * 
	 * @return 
	 */
	public static String getXspringboot() {

		if (null == xspringboot) {
			String configuredAppClasspath = getConfiguredJvmArgValue("springboot");

			if (!CommonUtils.isNullOrEmpty(configuredAppClasspath)) {
				xspringboot = configuredAppClasspath;
			} else {
				xspringboot = "";
			}
		}

		return xspringboot;
	}
	
	/**
	 * Set extra springboot jar for engine runtime dynamic compiling usage. <br>
	 * 
	 * @param xspringboot
	 */
	public static void setXspringboot(String xspringboot) {
		CommonSyntaxs.xspringboot = xspringboot;
	}

	/**
	 * Get extra classpath for engine runtime dynamic compiling usage. <br>
	 * Can be configured via '-DvRules4j.classpath=D:\app\lib\' in runtime JVM args
	 * 
	 * @return 
	 */
	public static String getXclasspath() {

		if (null == xclasspath) {
			String configuredAppClasspath = getConfiguredJvmArgValue("classpath");

			if (!CommonUtils.isNullOrEmpty(configuredAppClasspath)) {
				xclasspath = configuredAppClasspath;
			} else {
				xclasspath = "";
			}
		}

		return xclasspath;
	}

	/**
	 * Set extra classpath for engine runtime dynamic compiling usage. <br>
	 * 
	 * @param classpath
	 */
	public static void setXclasspath(String classpath) {
		CommonSyntaxs.xclasspath = classpath;
	}

	public static Boolean getEnablePrint() {
		return isEnablePrint();
	}

	/**
	 * Get rule files' modified beats check interval. time unit is 'seconds'. <br>
	 * Can be configured via '-DvRules4j.ruleFileModifiedBeatsCheckInterval=60' in
	 * runtime JVM args .
	 * 
	 * @return 10 seconds by default
	 */
	public static Long getRuleFileModifiedBeatsCheckInterval() {

		if (null == ruleFileModifiedBeatsCheckInterval) {
			String configuredBeatsInterval = getConfiguredJvmArgValue("ruleFileModifiedBeatsCheckInterval");

			if (!CommonUtils.isNullOrEmpty(configuredBeatsInterval) && isNumeric(configuredBeatsInterval)) {
				ruleFileModifiedBeatsCheckInterval = Double.valueOf(configuredBeatsInterval).longValue();
			} else {
				ruleFileModifiedBeatsCheckInterval = 0L;
			}
		}

		return ruleFileModifiedBeatsCheckInterval;
	}

	/**
	 * Set rule files' modified beats check interval. time unit is second.
	 * 
	 * @param ruleFileModifiedBeatsCheckInterval
	 */
	public static void setRuleFileModifiedBeatsCheckInterval(Long ruleFileModifiedBeatsCheckInterval) {
		CommonSyntaxs.ruleFileModifiedBeatsCheckInterval = ruleFileModifiedBeatsCheckInterval;
	}

	private static String getConfiguredJvmArgValue(String arg) {

		String value = System.getProperty("vRules4j." + arg);
		if (CommonUtils.isNullOrEmpty(value)) {
			value = System.getProperty("vrules4j." + arg);
		}
		if (CommonUtils.isNullOrEmpty(value)) {
			value = System.getProperty("vrules." + arg);
		}

		return value;
	}

	private static boolean getConfiguredEnablePrint() {

		String paths = getConfiguredJvmArgValue("enableprint");// System.getProperty("vRules4j.enableprint");
		if(CommonUtils.isNullOrEmpty(paths)) {
			paths = getConfiguredJvmArgValue("enablePrint");
		}

		if (!CommonUtils.isNullOrEmpty(paths) && "false".equalsIgnoreCase(paths.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Can be configured via '-DvRules4j.enableprint=true' or
	 * '-DvRules4j.enableprint=false' in runtime JVM args .
	 * 
	 * @return true by default
	 */
	public static Boolean isEnablePrint() {

		if (enablePrint == null) {
			enablePrint = getConfiguredEnablePrint();
		}
		return enablePrint;
	}

	public static void setEnablePrint(Boolean enablePrint) {
		CommonSyntaxs.enablePrint = enablePrint;
	}

	/**
	 * println
	 * 
	 * @param o
	 */
	public static void println(Object... args) {

		if (!isEnablePrint()) {
			return;
		}

		print(args);
		System.out.println();
	}

	/**
	 * print
	 * 
	 * @param o
	 */
	public static void print(Object... args) {

		if (!isEnablePrint()) {
			return;
		}

		if (null != args) {
			for (Object o : args) {
				System.out.print(ObjectSerializer.xStreamToXml(o));
			}
		} else {
			System.out.print(ObjectSerializer.xStreamToXml(args));
		}

	}

	/**
	 * <p>
	 * Checks if the CharSequence contains only Unicode letters and space (' ').
	 * </p>
	 * <p>
	 * {@code null} will return {@code false} An empty CharSequence (length()=0)
	 * will return {@code true}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isAlphaSpace(null)   = false
	 * StringUtils.isAlphaSpace("")     = true
	 * StringUtils.isAlphaSpace("  ")   = true
	 * StringUtils.isAlphaSpace("abc")  = true
	 * StringUtils.isAlphaSpace("ab c") = true
	 * StringUtils.isAlphaSpace("ab2c") = false
	 * StringUtils.isAlphaSpace("ab-c") = false
	 * </pre>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if only contains letters and space, and is non-null
	 * @since 3.0 Changed signature from isAlphaSpace(String) to
	 *        isAlphaSpace(CharSequence)
	 */
	public static boolean isAlphaSpace(final CharSequence cs) {

		if (cs == null) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isLetter(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	private static boolean isAlphanumeric_(final CharSequence cs) {

		if (isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isLetterOrDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 * 
	 * @param cs
	 * @return
	 */
	public static boolean isEmpty(final CharSequence cs) {

		return cs == null || cs.length() == 0;
	}

	/**
	 * Null checking
	 * 
	 * @param regex
	 *            true or false
	 * @param value
	 *            target value
	 * @return true if matched regex
	 */
	public static boolean isNotNull(String regex, String value) {

		if ("true".equalsIgnoreCase(regex)) {
			return !CommonUtils.isNullOrEmpty(value);
		} else {
			return CommonUtils.isNullOrEmpty(value);
		}
	}

	/**
	 * Null checking
	 * 
	 * @param value
	 *            target value
	 * @return true given value is not null
	 */
	public static boolean isNotNull(String value) {

		return !CommonUtils.isNullOrEmpty(value);
	}

	/**
	 * Numeric checking
	 * 
	 * @param regex
	 *            true or false
	 * @param value
	 *            target value to be validated
	 * @return true if matched regex, otherwise return false
	 */
	public static boolean isNumeric(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if ("true".equalsIgnoreCase(regex)) {
			return isNumeric_(value);
		} else {
			return !isNumeric_(value);
		}
	}

	/**
	 * Numeric checking
	 * 
	 * @param value
	 *            target value to be validated
	 * @return true if matched regex, otherwise return false
	 */
	public static boolean isNumeric(String value) {

		if (!isNotNull(value)) {
			return false;
		}
		boolean isTure = true;
		try {
			Double.valueOf(value);
		} catch (NumberFormatException e) {
			isTure = false;
		}

		return isNumeric_(value) || isTure;
	}

	/**
	 * <p>
	 * Checks if the CharSequence contains only Unicode digits. A decimal point is
	 * not a Unicode digit and returns false.
	 * </p>
	 * 
	 * @param cs
	 * @return
	 */
	private static boolean isNumeric_(final CharSequence cs) {

		if (isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Alpha checking
	 * 
	 * @param value
	 * @return true if value is alpha
	 */
	public static boolean isAlpha(String value) {

		if (!isNotNull(value)) {
			return false;
		}

		return isAlpha_(value);
	}

	/**
	 * <p>
	 * Checks if the CharSequence contains only Unicode letters.
	 * </p>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if only contains letters, and is non-null
	 */
	private static boolean isAlpha_(final CharSequence cs) {

		if (isEmpty(cs)) {
			return false;
		}
		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isLetter(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks if the CharSequence contains only Unicode letters or digits.
	 * </p>
	 * <p>
	 * {@code null} will return {@code false}. An empty CharSequence (length()=0)
	 * will return {@code false}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isAlphanumeric(null)   = false
	 * StringUtils.isAlphanumeric("")     = false
	 * StringUtils.isAlphanumeric("  ")   = false
	 * StringUtils.isAlphanumeric("abc")  = true
	 * StringUtils.isAlphanumeric("ab c") = false
	 * StringUtils.isAlphanumeric("ab2c") = true
	 * StringUtils.isAlphanumeric("ab-c") = false
	 * </pre>
	 * 
	 * @param cs
	 *            the CharSequence to check, may be null
	 * @return {@code true} if only contains letters or digits, and is non-null
	 */
	public static boolean isAlphanumeric(String value) {

		if (!isNotNull(value)) {
			return false;
		}

		return isAlphanumeric_(value);
	}

	/**
	 * Checking the value is not larger than given regex parameter
	 * 
	 * @param regex
	 * @param value
	 * @return true if not larger than regex, otherwise return false
	 */
	public static boolean maxValue(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNumeric(value) || !isNumeric(regex)) {
			return false;
		}

		return Double.valueOf(value) <= Double.valueOf(regex);
	}

	/**
	 * Checking the value is not less than given regex parameter
	 * 
	 * @param regex
	 * @param value
	 * @return true if not less than regex, otherwise return false
	 */
	public static boolean minValue(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNumeric(value) || !isNumeric(regex)) {
			return false;
		}

		return Double.valueOf(value) >= Double.valueOf(regex);
	}

	/**
	 * Checking data format. "YYYY" "MM" "DD" is requested as basic syntax. e.g,
	 * "YYYY/MM/DD" or "YY/MM/DD" "MM/DD/YYYY"
	 * 
	 * @param regex
	 * @param value
	 * @return true if matched, otherwise return false
	 */
	public static boolean dateFormat(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNotNull(regex)) {
			return true;
		}

		regex = regex.replace("mm", "MIN").toUpperCase().replace("YYYY", REGEX_YEAR).replace("YY", REGEX_YEAR)
				.replace("MM", REGEX_MONTH).replace("DD", REGEX_DAY).replace("HH", REGEX_HOUR)
				.replace("MIN", REGEX_MININUTE).replace("SS", REGEX_SECOND);

		Pattern dateFormatPattern = Pattern.compile("^" + regex + "$");

		return dateFormatPattern.matcher(value).find();
	}

	/**
	 * Decimal precision checking.
	 * 
	 * @param digital
	 * @param decimal
	 * @param value
	 * @return true if precision is valid, otherwise return false.
	 */
	public static boolean decimal(String digital, String decimal, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNotNull(value) || !isNotNull(digital) || !isNotNull(decimal)) {
			return false;
		}

		if (!isNumeric(digital) || !isNumeric(decimal)) {
			return false;
		}

		Integer decimalLength = Integer.valueOf(decimal);
		Integer digitalLength = Integer.valueOf(digital);

		if (decimalLength >= digitalLength) {
			return false;
		}

		String[] values = value.split("\\.");
		if (null != values && values.length == 1) {
			String digitStr = values[0] != null ? values[0].trim() : "";
			if (digitStr.length() > digitalLength) {
				return false;
			}
		} else if (null != values && values.length == 2) {
			String digitStr = values[0] != null ? values[0].trim() : "";
			String decimalStr = values[1] != null ? values[1].trim() : "";

			if (decimalStr.length() > decimalLength || digitStr.length() > digitalLength - decimalLength) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Max length checking
	 * 
	 * @param regex
	 * @param value
	 * @return true if value's length is not larger than regex; otherwise return
	 *         false.
	 */
	public static boolean maxLength(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNotNull(regex) || !isNumeric(regex)) {
			return true;
		}

		if (!isNotNull(value)) {
			return false;
		}

		Integer length = Integer.valueOf(regex);

		return value.length() <= length;
	}

	/**
	 * Min length checking
	 * 
	 * @param regex
	 * @param value
	 * @return true if value's length is not less than regex; otherwise return
	 *         false.
	 */
	public static boolean minLength(String regex, String value) {

		if (!isNotNull(value)) {
			return false;
		}

		if (!isNotNull(regex) || !isNumeric(regex)) {
			return true;
		}

		if (!isNotNull(value)) {
			return false;
		}

		Integer length = Integer.valueOf(regex);

		return value.length() >= length;
	}
	
	public static boolean isEnableFullClassNameMode() {
		return ConfigurationProxy.isEnableFullClassNameMode();
	}

	public static void setEnableFullClassNameMode(boolean enableFullClassNameMode) {
		ConfigurationProxy.setEnableFullClassNameMode(enableFullClassNameMode);
	}

}
