package org.manwhore.displayer.util;

/**
 * All constants in the application.
 * 
 * @author siglerv
 *
 */
public interface Constants {

    public static final String URI_SMS = "content://sms";
    public static final String URI_SMS_CONVERSATIONS = "content://sms/conversations";
    public static final String EXPORT_DIR = "SMSMExports";
    public static final String _ID = "_id";
    public static final int PHONE_MATCH_LENGTH = 8;
    public static final String SMS_THREAD_ID = "thread_id";
    public static final String SMS_ADDRESS = "address";
    public static final String SMS_TYPE = "type";
    public static final String SMS_BODY = "body";
    public static final String SMS_PROTOCOL = "protocol";
    public static final String SMS_DATE = "date";
    public static final String SMS_PERSON = "person";
    public static final String CONVERSATION_THREAD_ID = "thread_id";
    public static final String CONVERSATION_MSG_COUNT = "msg_count";
    public static final String CONVERSATION_DATE = "date";
    public static final String CONVERSATION_ADDRESS = "address";
    //, new String[] {"thread_id", "sms.address as address", "group_date as date", "msg_count"}, null, null, "date DESC")
    public static final String PACKAGE = "org.manwhore.displayer";
    public static final String ACTIVITY_CONVERSATION_VIEW = "org.manwhore.displayer.ConversationViewActivity";
    public static final String ACTIVITY_CONVERSATION_LIST = "org.manwhore.displayer.ConversationListActivity";    
    public static final String ACTIVITY_SETTINGS = "org.manwhore.displayer.SettingsActivity";
    public static final String ACTIVITY_CREATE_PROFILE = "org.manwhore.displayer.CreateProfileActivity";
    public static final String INTENT_KEY_MSGTHREAD = "org.manwhore.displayer.MsgThread";
    public static final String INTENT_KEY_FILTER = "org.manwhore.displayer.Filter";
    public static final String SETTINGS_USER = "mwUsernamePref";
    public static final String SETTINGS_PASS = "mwPasswordPref";
    public static final String SETTINGS_CHECK = "mwCheckProfilePref";
    public static final String SETTINGS_ADJUST = "mwAdjustIncoming";    
    public static final String SETTINGS_ADJUST_VALUE = "mwAdjustValue";
    public static final String SETTINGS_SEND_LOGS = "mwSendLogs";
    public static final int WSRESULT_PROFILE_UNDEFINED = -1;
    public static final int WSRESULT_OK = 0;
    public static final int WSRESULT_CALL_FAIL = 1;
    public static final int WSRESULT_NOT_LOGGED_IN = 2;
    public static final int WSRESULT_SERVER_ERROR = 3;
    
    public static final String KEYSTORE_FILENAME = "manwhore.bks";
    public static final String KEYSTORE_PASS = "Heslo123!";
}
