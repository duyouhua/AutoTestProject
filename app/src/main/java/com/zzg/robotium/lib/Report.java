package com.zzg.robotium.lib;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


/**
 * 测试报告工具类
 *
 * @author zzg
 */
public class Report extends Formatter {

    public static final String TAG = "ROBOT";
    private int i = 0;
    private long setStartTime;
    private long setEndTime;
    private static int p_pass = 0;
    private static int p_fail = 0;
    private static int p_nt = 0;
    private static String result = "";
    public final static String pass = "Pass";
    public final static String success = "Success";
    public final static String fail = "Fail";
    public final static String exception = "Exception";
    private static String imageName = null;
    private static FileHandler fileHTML;
    private static Formatter formatterHTML;

    private final String indexFile = "index.html";

    private static Logger logger = Logger.getLogger(Report.class.getName());

    /**
     * 用例开始时间
     */
    private static String mStartTime;

    /**
     * 用例结束时间
     */
    private static String mEndTime;

    /**
     * 用例执行时间
     */
    private static String mRunTime;

    /**
     * 用例步骤
     */
    private static int mp_total;

    /**
     * 用例链接
     */
    private static String mUrl;

    InputDataStore inputDataStore;

    static final String HTML_HEADER = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
            + "<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"NO-CACHE\">"
            + "<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">"
            + "<html><head><style>\n" +
            "        a,a:hover{ text-decoration: none;}\n" +
            "        .index{ padding: 0 5%;}\n" +
            "        .table{ width: 100%; text-align:center; border: 1px solid #ddd; border-collapse: collapse;  border-spacing: 0;}\n" +
            "        .table tr{ height: 40px;}\n" +
            "        .table tr th{ background: #eeeeee; border: 1px solid #ddd;padding-left: 2px;}\n" +
            "        .table tr td{ border: 1px solid #ddd; padding-left: 2px;}\n" +
            "        .summary {width: 30%; text-align:left; border: 1px solid #ddd; border-collapse: collapse;  border-spacing: 0; margin-top: 20px;}\n" +
            "        .summary tr{ height: 30px;}\n" +
            "        .summary tr:nth-child(odd){ background-color: #ebcccc;}\n" +
            "        .summary tr td { border: 1px solid #ddd;}\n" +
            "        a{ color: #5bc0de; }\n" +
            "        a:hover{ color: #1b6d85;}\n" +
            "    </style><title>测试报告</title></head>"
            + "<body>"
            +"<div class=\"index\">"
            + "<div class=\"page_title\" ><center>"
            + "<h1>测试报告</h1></center></div>"
            + "<table  class=\"table\" ><tr>"
            + "<th>序号</th>"
            + "<th>用例描述</th>"
            + "<th>开始时间</th>"
            + "<th>结束时间</th>"
            + "<th>运行时间</th>"
            + "<th>运行步骤</th>"
            + "<th>状态</th>" + "</tr>";

    public boolean setup() {
        inputDataStore = InputDataStore.getInstance();
        setFolder(inputDataStore.getInput_LogPath());
        Log.e(TAG, "ReportLib  setup方法");
        try {
            logger.setLevel(Level.INFO);
            fileHTML = new FileHandler(inputDataStore.getInput_LogPath() + indexFile);
            formatterHTML = new Report();
            fileHTML.setFormatter(formatterHTML);
            logger.addHandler(fileHTML);
            Log.i(TAG, "Create file" + inputDataStore.getInput_LogPath() + indexFile + "Successful!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Create file" + inputDataStore.getInput_LogPath() + indexFile + "Unsuccessful! Please check your permission" + e.toString());
            return false;
        }
    }

    public String setFolder(String directoryName) {
        File dataDirectory = new File(directoryName);
        if (dataDirectory != null) {
            //Environment.getDataDirectory().setWritable(true, false);
            if (!dataDirectory.exists()) {
                if (!dataDirectory.mkdirs()) {
                    dataDirectory = null;
                }
            }
        }
        return dataDirectory.toString();
    }

    public void closeLog() {
        fileHTML.close();
        p_pass = 0;
        p_fail = 0;
        p_nt = 0;
        result = "";
    }

    /**
     * @param caseName  用例名称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param runTime   执行时间
     * @param p_totle   执行步骤
     * @param p_result  执行结果
     */
    public static void logWriter(String caseName, String url, String startTime, String endTime,
                                 String runTime, int p_totle, String p_result) {
        Log.e(TAG, "report写入" + url);
        mStartTime = startTime;
        mEndTime = endTime;
        mRunTime = runTime;
        mp_total = p_totle;
        result = p_result;
        mUrl = url;
        try {
            logger.info(caseName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" logger write exception!");
        }
    }

    @Override
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        Log.e(TAG, "调用了 format方法");
        //序号
        buf.append("<tr style=\"height:50px;\">");
        buf.append("<td>");
        buf.append(recordStep());
        buf.append("</td>");
        buf.append("<td>");
        //用例名称
        buf.append("<a href=\"" + mUrl + "\""+" >");
        buf.append("<b>");
        buf.append(formatMessage(rec));
        buf.append("</b>");
        buf.append("</a>");
        buf.append('\n');
        buf.append("</td>");
        //开始时间
        buf.append("<td>");
        buf.append(mStartTime);
        buf.append("</td>");
        //结束时间
        buf.append("<td>");
        buf.append(mEndTime);
        buf.append("</td>");
        //执行时间
        buf.append("<td>");
        buf.append(mRunTime);
        buf.append("</td>");
        //执行步骤
        buf.append("<td>");
        buf.append(mp_total);
        buf.append("</td>");
        //执行状态
        buf.append("<td>");
        if (result.matches(pass)) {
            p_pass = p_pass + 1;
            buf.append("<b>");
            buf.append("<font color=Green>");
            buf.append("成功");
            buf.append("</font>");
            buf.append("</b>");
        } else if (result.matches(fail)) {
            p_fail = p_fail + 1;
            buf.append("<a href=\"" + mUrl + "\"" + ">");
            buf.append("<b>");
            buf.append("<font color=Red>");
            buf.append("失败");
            buf.append("</font>");
            buf.append("</b>");
            buf.append("</a>");
        } else if (result.matches(exception)) {
            p_nt = p_nt + 1;
            buf.append("<a href=\"" + mUrl + "\"" + ">");
            buf.append("<b>");
            buf.append("<font color=Red>");
            buf.append("异常");
            buf.append("</font>");
            buf.append("</b>");
            buf.append("</a>");
        } else {
            buf.append("<b>");
            buf.append("");
            buf.append("</b>");
        }
        buf.append("</td>");
        buf.append("</tr>");
        buf.append("</div>\n");
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buf.toString();
    }


    private int recordStep() {
        i = i + 1;
        return i;
    }

    private String getPercnet(double p_numerator, double p_denominator) {
        double percent = p_numerator / p_denominator;
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        return nt.format(percent);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCalcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    private String getDeltaTime(long p_startTime, long p_endTime) {
        long day = (p_endTime - p_startTime) / (24 * 60 * 60 * 1000);
        long hour = ((p_endTime - p_startTime) / (60 * 60 * 1000) - day * 24);
        long min = (((p_endTime - p_startTime) / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = ((p_endTime - p_startTime) / 1000 - day * 24 * 60 * 60 - hour
                * 60 * 60 - min * 60);

        return day + "天" + hour + "小时" + min + "分" + s + "秒";
    }


    public String getHead(Handler h) {
        setStartTime = System.currentTimeMillis();
        return HTML_HEADER;
    }

    public String getTail(Handler h) {
        setEndTime = System.currentTimeMillis();
        String HTML_Tail;
        int p_total = p_pass + p_fail + p_nt;
        if (p_total > 0) {
                HTML_Tail = "</table>" +
                        "<table class=\"summary\">\n" +
                        "        <tr>\n" +
                        "            <td>用例数量</td>\n" +
                        "            <td>"+p_total+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>异常用例</td>\n" +
                        "            <td>"+p_nt+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>成功用例</td>\n" +
                        "            <td>"+p_pass+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>失败用例</td>\n" +
                        "            <td>"+p_fail+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>成功率(%)</td>\n" +
                        "            <td>"+getPercnet(p_pass, p_total)+"</td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "            <td>失败率(%)</td>\n" +
                        "            <td>"+getPercnet(p_fail + p_nt, p_total)+"</td>\n" +
                        "        </tr>\n" +
                        "    </table>"
                        + "</div></BODY></HTML>";
        } else {
            HTML_Tail = "</table></PRE>" + "<br>&nbsp;用例执行异常！" + "<br><br>"
                    + "</div></BODY></HTML>";
        }
        return HTML_Tail;
    }
}