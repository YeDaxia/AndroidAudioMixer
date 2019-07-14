package io.github.yedaxia.musicnote.data.api;

import io.github.yedaxia.musicnote.app.BuildInfo;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/12.
 */

public class Urls {

    private static final String DEV_HOST = "http://sandbox.52cmajor.com";
    private static final String RELEASE_HOST = "https://www.52cmajor.com";

    /**
     * 获取是否激活的检查url
     * @return
     */
    public static String getCheckActivateUrl(){
        return getUrl("/api/v1/app/checkMusicNote");
    }

    public static String getBuyAppUrl(String deviceId){
        return getUrl("/app/buyMusicNote?deviceId="+deviceId);
    }

    public static String getAboutUrl(){
        return getUrl("/doc?doc=about-developer");
    }

    public static String getSearchTabUrl(){
        return "https://sopu.52cmajor.com";
    }

    public static String getFeedbackUrl(){
        return getUrl("/contact");
    }

    public static String  getHelpUrl(){
        return getUrl("/doc?doc=music-note");
    }

    public static String getAppOrderStatusUrl(String deviceId){
        return getUrl("/app/order/status?deviceId="+deviceId);
    }

    /**
     * 验票url
     * @return
     */
    public static String getValidTicketUrl(){
        return "https://admin.52cmajor.com/api/v1/activity/signin";
    }

    private static String getUrl(String apiUrl){
        if(BuildInfo.ENV == BuildInfo.ENV_DEBUG){
            return DEV_HOST + apiUrl;
        }else{
            return RELEASE_HOST + apiUrl;
        }
    }
}
