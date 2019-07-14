package io.github.yedaxia.musicnote.util;

/**
 * @author wufei
 * @version v1.0
 */
public class RegularUtils {
    /**
     * 正则：手机号（精确）
     * <p>移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188</p>
     * <p>联通：130、131、132、145、155、156、175、176、185、186</p>
     * <p>电信：133、153、173、177、180、181、189</p>
     * <p>全球星：1349</p>
     * <p>虚拟运营商：170</p>
     */
    public static final String REGEX_MOBILE_EXACT
        = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";

    public static final String REGEX_INVALID_NUMBER
        = "^(\\d*)|(\\w*[*]\\w*)|(\\w*[nN][uU][lL][lL]\\w*)|(9774d56d682e549c)$";

    public static final String REGEX_HANDLED_PIC = "(.*jpg@!.*?)|(.*png@!.*?)";

    /**
     * 判断是否是电话号码
     */
    public static boolean isPhoneNumber(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(REGEX_MOBILE_EXACT);
    }


    public static boolean isInvalidNumber(String number) {
        if (StringUtils.isEmpty(number)) {
            return true;
        }
        return number.matches(REGEX_INVALID_NUMBER);
    }

    public static String getOriginalUrl(String url){
        if(StringUtils.isEmpty(url)){
            return null;
        }
        if(url.matches(REGEX_HANDLED_PIC)){
            int index = url.lastIndexOf("@!");
            url = url.substring(0, index)+"?";
        }
        return url;
    }


}
