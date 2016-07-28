
package com.wt.testble;

import java.io.File;

import android.os.Environment;

public class Constant {
	public static final String SHARE_PREF_KEY = "com.tuwan.rabit.pillow";
	public static final String SHARE_PREF_KEY_UPLOAD_TIME = "com.tuwan.rabit.pillow.uploadtime";
	public static final String SHARE_PREF_KEY_MAX_SERVER_ID = "com.tuwan.rabit.pillow.maxServerId";
	public static final String PREF_SAVED_DEVICES = "saved_devices";
    // Login APi（9）
	//public static final String ACTION_THIRDLOGIN = "LoginAction.thirdlogin";
	public static final String ACTION_THIRDLOGIN = "loginaction.thirdlogin";
    //public static final String ACTION_LOGIN = "LoginAction.mobilelogin";
    public static final String ACTION_LOGIN = "loginaction.mobilelogin";
    //public static final String ACTION_SEND_SMS = "LoginAction.sendSms";
    public static final String ACTION_SEND_SMS = "loginaction.sendsms";
    //public static final String ACTION_UPDATE_USERINFO = "LoginAction.updateUserInfo";
    //public static final String ACTION_UPDATE_USERINFO = "loginaction.updateuser";
    public static final String ACTION_UPDATE_USERINFO = "loginaction.updateuserdata";
	//public static final String ACTION_LOAD_USERINFO ="LoginAction.loadUserInfo";
	public static final String ACTION_LOAD_USERINFO ="loginaction.userinfo";
	public static final String ACTION_EXIT_LOGIN = "LoginAction.exit";
	//public static final String ACTION_BIND_MOBILE = "LoginAction.bindMobile";
	//public static final String ACTION_ISLOGIN = "LoginAction.isLogin";
	public static final String ACTION_ISLOGIN = "loginaction.userinfo";
	//public static final String ACTION_VERSION_UPDATE = "LoginAction.getLastVersion";
	public static final String ACTION_VERSION_UPDATE = "loginaction.lastversion";

    // Sync Data API（2）
	//public static final String ACTION_DATA_UPLOAD = "DataAction.upload";
	public static final String ACTION_DATA_UPLOAD = "dataaction.uploaddata";
	//public static final String ACTION_DATA_DOWNLOAD = "DataAction.download";
	public static final String ACTION_DATA_DOWNLOAD = "dataaction.downloaddata";
    
    // Feedback API（2）
	//public static final String ACTION_SUGGESTACTION_LOAD = "SuggestAction.load";
	public static final String ACTION_SUGGESTACTION_LOAD = "suggestaction.getmsg";
	//public static final String ACTION_SUGGESTACTION_SAVE = "SuggestAction.save";
	public static final String ACTION_SUGGESTACTION_SAVE = "suggestaction.savemsg";
    
    public static final int USERINFO_REQUEST_BIRTHDAY = 1;
    public static final int USERINFO_REQUEST_WEIGHT = 2;
    public static final int USERINFO_REQUEST_PROFESSION = 3;
    public static final int REQUEST_CODE_CAMERA = 4;
    public static final int REQUEST_CODE_GALLERY = 5;
    public static final int REQUEST_CODE_SLEEP_TIME_SET = 6;
    public static final int REQUEST_CODE_HELP_SLEEP_MUSIC = 7;
    public static final int REQUEST_CODE_WAKE_MUSIC = 8;
    public static final int REQUEST_CODE_DATE_PICKER = 9;
    
    public static final int REQUEST_CODE_FINISH_EX_ACTIVITY = 1024;
    
    public static final String ACTION_FINISH_EX_ACTIVITY="broadcast_finish_ex_activity";
    public static final String USERINFO_RESULT_DATA = "userinfo_result_data";
    public static final String USERINFO_SLEEP_TIME_BEGIN = "userinfo_sleep_time_begin";
    public static final String USERINFO_SLEEP_TIME_END = "userinfo_sleep_time_end";
    public static final String MUSIC_PICKER_RESULT_DATA = "music_picker_result_data_name";
    public static final String MUSIC_PICKER_RESULT_DATA_NUM = "music_picker_result_data_num";
    public static final String MUSIC_PICKER_FLAG = "music_picker_flag";
    
    public static final int MUSIC_PICKER_FLAG_HELP_SLEEP = 0;
    public static final int MUSIC_PICKER_FLAG_WAKE = 1;
    
    public static final String INTENT_IS_NEWFLAG = "intent_is_newflag";

	public static final String PAGE_COMPANY = "http://app.tuwan21.com/company.jsp";
	//public static final String PAGE_PRODUCT = "http://app.tuwan21.com/product.jsp";
	public static final String PAGE_PRODUCT = "https://wap.koudaitong.com/v2/feature/1d73kgig1";
	public static final String PAGE_QUESTION = "http://app.tuwan21.com/question.jsp";
	public static final String PAGE_CONTRACT = "http://app.tuwan21.com/contract.jsp";
	public static final String PAGE_COMPANY_EXTRA = "company";
	public static final String PAGE_PRODUCT_EXTRA = "product";
	public static final String PAGE_QUESTION_EXTRA = "question";
	public static final String PAGE_CONTRACT_EXTRA = "contract";
	public static long ONE_DAY_MILLS = 24 * 3600 * 1000;
	public static long ONE_WEEK_MILLS = 24 * 3600 * 1000 * 7;
	//public static long HALF_DAY_MILLS = 12 * 3600 * 1000;
	public static long HALF_DAY_MILLS = 24 * 3600 * 1000;//去除睡眠时间与起床时间只能设定 12 小时内的限制改为可以设 置24小时
	public static long QUARTER_MILLS = 15 * 60 * 1000;
	public static long ONE_HOUR = 3600 * 1000;
	
	// share preference user setting
	public static final long USER_SLEEP_TIME_BEGIN_DEF = 22 * 3600 *1000;
	public static final long USER_SLEEP_TIME_END_DEF = 7 * 3600 * 1000;
	public static final String USER_SLEEP_TIME_BEGIN = "user_sleep_time_begin";
	public static final String USER_SLEEP_TIME_END = "user_sleep_time_end";
	public static final String USER_HELP_SLEEP_MUSIC = "user_help_sleep_music";
	public static final String USER_WAKE_MUSIC = "user_wake_music";
	public static final String USER_HABITSET_SLEEP_LIGHT = "user_habitset_sleep_light";
	public static final String USER_HABITSET_SLEEP_MUSIC = "user_habitset_sleep_music";
	public static final String USER_HABITSET_WAKE_LIGHT = "user_habitset_wake_light";
	public static final String USER_HABITSET_WAKE_MUSIC = "user_habitset_wake_music";
	public static final String USER_HABITSET_NIGHT_LIGHT = "user_habitset_night_light";
	
    public static final String DOWNLOAD_FILE_ABSOLUTE_PATH = "download_file_absolute_path";
    public static final String DOWNLOAD_FILE_URL = "download_file_url";
    public static final String DOWNLOAD_FILE_NAME = "download_file_name";
    public static final int REQUEST_CODE_APK_UPGRADE = 1;
    
    public static final String PACKAGE_NAME = ".cn.tuwan.rabit.pillow";
    public static final String FILE_PATH_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static final String DOWNLOAD_APK_NAME = "smartpillow";
    public static final String APK_DOWNLOAD_DIR = FILE_PATH_DIR
            + File.separator + PACKAGE_NAME + File.separator;

    public static final String UPDATE_INFO_VERSION_NAME = "name";
    public static final String UPDATE_INFO_VERSION = "ver";
    public static final String UPDATE_INFO_TITLE = "title";
    public static final String UPDATE_INFO_CONTENT = "content";
    public static final String UPDATE_INFO_URL = "url";
    
    public static final int REQUEST_OPEN_BLE = 0xF13;
}
