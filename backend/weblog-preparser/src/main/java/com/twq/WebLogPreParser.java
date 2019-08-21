package com.twq;

public class WebLogPreParser {
    //将日志中的文件按照空格进行切分，然后将每一行解析成一个PreParsedLog对象
    public static PreParsedLog parse(String line) {
        //因为以#号开头的第一行，是起说明作用的，不是真正的数据
        if(line.startsWith("#")) {
            return null;
        } else {
            PreParsedLog preParsedLog = new PreParsedLog();
            //line按空格切分
            String[] temps = line.split(" ");
            preParsedLog.setServerTime(temps[0] + " " + temps[1]);
            preParsedLog.setServerIp(temps[2]);
            preParsedLog.setMethod(temps[3]);
            preParsedLog.setUriStem(temps[4]);
            String queryString = temps[5]; //用户行为的日志数据
            preParsedLog.setQueryString(queryString);
            String[] queryStrTemps = queryString.split("&");
            String command = queryStrTemps[1].split("=")[1];
            preParsedLog.setCommand(command);
            String profileIdStr = queryStrTemps[2].split("=")[1];
            preParsedLog.setProfileId(getProfileId(profileIdStr));
            preParsedLog.setServerPort(Integer.parseInt(temps[6]));
            preParsedLog.setClientIp(temps[8]);
            preParsedLog.setUserAgent(temps[9].replace("+", " "));
            String tempTime = preParsedLog.getServerTime().replace("-", "");
            preParsedLog.setDay(Integer.parseInt(tempTime.substring(0, 8)));
            preParsedLog.setMonth(Integer.parseInt(tempTime.substring(0, 6)));
            preParsedLog.setYear(Integer.parseInt(tempTime.substring(0, 4)));
            return preParsedLog;
        }
    }

    private static int getProfileId(String profileIdStr) {
        return Integer.valueOf(profileIdStr.substring(profileIdStr.indexOf("-") + 1));
    }
}
