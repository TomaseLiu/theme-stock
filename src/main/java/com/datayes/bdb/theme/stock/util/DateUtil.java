package com.datayes.bdb.theme.stock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Date工具类
 * 
 * @author wei.wang
 * 
 */
public class DateUtil {
	public enum DatePattern {
		month("yyyy-MM"),
		week("yyyy-MM-dd E"),
		day("yyyy-MM-dd"), day2("yyyyMMdd"),
		hour("yyyy-MM-dd HH"), 
		minute("yyyy-MM-dd HH:mm"), 
		second("yyyy-MM-dd HH:mm:ss");
		
		private DatePattern(String value) {
			this.value = value;
		}

		private String value;
	}

	private static final DatePattern DEFAULT_DATE_PATTERN = DatePattern.second;

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();
	private static final Object object = new Object();

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(DatePattern datePattern)
			throws RuntimeException {
		SimpleDateFormat simpleDateFormat = threadLocal.get();
		if (simpleDateFormat == null) {
			synchronized (object) {
				if (simpleDateFormat == null) {
					simpleDateFormat = new SimpleDateFormat(datePattern.value);
					simpleDateFormat.setLenient(false);
					threadLocal.set(simpleDateFormat);
				}
			}
		}
		simpleDateFormat.applyPattern(datePattern.value);
		return simpleDateFormat;
	}

	@SuppressWarnings("unused")
	private static SimpleDateFormat defaultDateFormat() {
		return getDateFormat(DEFAULT_DATE_PATTERN);
	}

	/**
	 * 只用指定格式转成日期字符串
	 * 
	 * @param date
	 * @param datePattern
	 * @return
	 */
	
	public static String dateToStr(Date date, DatePattern datePattern) {
		SimpleDateFormat formatter = getDateFormat(datePattern);
		return formatter.format(date);
	}

	/**
	 * 使用默认格式转成日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date) {
		return dateToStr(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 当前日期
	 * 
	 * @return
	 */
	public static Date currentDate() {
		return new Date();
	}

	/**
	 * 以毫秒为单位当前时间戳
	 * 
	 * @return
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 指定格式获取当前系统时间
	 * 
	 * @return
	 */
	public static String currentToStr(DatePattern datePattern) {
		SimpleDateFormat formatter = getDateFormat(datePattern);
		return formatter.format(currentDate());
	}

	/**
	 * 默认格式获取当前系统时间
	 * 
	 * @return
	 */
	public static String currentToStr() {
		return currentToStr(DEFAULT_DATE_PATTERN);
	}

	@SuppressWarnings("deprecation")
	public static int getMonthsBetweenTwoDay(Date startDate, Date endDate){
		return (endDate.getYear() - startDate.getYear()) * 12 + endDate.getMonth() - startDate.getMonth() + 1;
		
//		Calendar start =Calendar.getInstance();
//		start.setTime(startDate);
//		Calendar end =Calendar.getInstance();
//		start.setTime(endDate);
//		int months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 + end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
//		return months;//months == 0 ? 1 : months;
	}
	public static  Date getFirstDayOfCurrentMonth(Date currentDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	public static  Date getFirstDayOfCurrentWeek(Date currentDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.set(Calendar.DAY_OF_WEEK, 2);
		return cal.getTime();
	}
	/**
	 * 使用默认模式根据时间字符串获取时间
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date strToDate(String dateStr) throws ParseException {
		return strToDate(dateStr, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 根据时间字符串和模式获取时间
	 * 
	 * @param dateStr
	 * @param datePattern
	 * @return
	 * @throws ParseException
	 */
	public static Date strToDate(String dateStr, DatePattern datePattern)
			throws ParseException {
		SimpleDateFormat formatter = getDateFormat(datePattern);
		return formatter.parse(dateStr);
	}

	/**
	 * add day
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addDay(Date date, int day) {
		return DateUtils.addDays(date, day);
	}
	
	public static Date addLessDay(Date date, int day) {
		return DateUtils.addSeconds(DateUtils.addDays(date, day), -1);
	}

	public static int daysBetween(Date d1, Date d2) {
		int days = Days.daysBetween(new DateTime(d1), new DateTime(d2))
				.getDays();
		return days;
	}
	/**
	 * judge if the input date is in the same day with current time or not.
	 * @param date
	 * @return
	 */
	public static boolean isSameDay(Date date){
		String dateStr = DateUtil.dateToStr(date, DatePattern.day);
		String todayStr = DateUtil.currentToStr(DatePattern.day);
		if(dateStr.equals(todayStr)){
			return true;
		}
		return false;
	}
	/**
	 * Compare currentDate time fragment to time string 
	 * @param currentDate
	 * @param compareTimeStr compare time string, eg: 9:35
	 * @return currentDate time fragment great than compare time string, return 1, less than compare time, return -1, or return 0
	 *         if compareTimeStr format is not correct, return null
	 */
	public static Integer timeComare(Date currentDate, String compareTimeStr) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		
		String[] compareTimeStrs = compareTimeStr.split(":");
		
		int compareTimeHour = currentHour;
		if (compareTimeStrs.length > 0) {
			String compareTimeHourStr = compareTimeStrs[0];
			try {
				compareTimeHour = Integer.parseInt(compareTimeHourStr);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		if (currentHour > compareTimeHour) {
			return 1;
		} else if (currentHour < compareTimeHour) {
			return -1;
		}
		
		int currentMinute = calendar.get(Calendar.MINUTE);
		int compareTimeMinute = currentMinute;
		if (compareTimeStrs.length > 1) {
			String compareTimeMinuteStr = compareTimeStrs[1];
			try {
				compareTimeMinute = Integer.parseInt(compareTimeMinuteStr);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		if (currentMinute > compareTimeMinute) {
			return 1;
		} else if (currentMinute < compareTimeMinute) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * get current date day start time
	 * @param date
	 * @return
	 */
	public static Date getDayStart(Date date) {  
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }
	
	/**
	 * check date is weekend
	 * @param date
	 * @return
	 */
	public static boolean isWeekend(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
	    int week = calendar.get(Calendar.DAY_OF_WEEK);
	    if (week == Calendar.SATURDAY || week == Calendar.SUNDAY) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	public static int getWeekSequence(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		return week;
	}
	public static int getMonthSequence(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		return month+1;
	}
}
