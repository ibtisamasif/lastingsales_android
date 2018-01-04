package com.example.muzafarimran.lastingsales.sync;

public class MyURLs {

//    private static final String server = "staging";
    private static final String server = "api";

    //    public static String LOGIN_URL_OLD = "http://api.lastingsales.com/api/auth/login";
    public static String LOGIN_URL = "https://" + server + ".lastingsales.com/api/v1/auth/login";
    public static String SIGNUP_URL = "https://" + server + ".lastingsales.com/api/v1/auth/register";
    public static String ADD_COMPANY_URL = "https://" + server + ".lastingsales.com/api/v1/company";
    public static String IMAGE_URL = "https://" + server + ".lastingsales.com/";
    public static String ADD_CONTACT = "https://" + server + ".lastingsales.com/api/v1/lead";
    public static String ADD_FOLLOWUP = "https://" + server + ".lastingsales.com/api/v1/lead/"; //SampleFormatComplete ADD_FOLLOWUP = "http://staging.lastingsales.com/api/v1/lead/85/followup";
    public static String ADD_NOTE = "https://" + server + ".lastingsales.com/api/v1/lead/"; //SampleFormatComplete ADD_NOTE = "http://staging.lastingsales.com/api/v1/lead/145/notes";
    public static String DELETE_NOTE = "https://" + server + ".lastingsales.com/api/v1/lead/";
    public static String ADD_CALL = "https://" + server + ".lastingsales.com/api/v1/call";
    public static String GET_CALL = "https://" + server + ".lastingsales.com/api/v1/call";
    public static String ADD_INQUIRY = "https://" + server + ".lastingsales.com/api/v1/inquiries";
    public static String UPDATE_INQUIRY = "https://" + server + ".lastingsales.com/api/v1/inquiries/";
    public static String DELETE_INQUIRY = "https://" + server + ".lastingsales.com/api/v1/inquiries/";
    public static String GET_INQUIRY = "https://" + server + ".lastingsales.com/api/v1/inquiries/";
    public static String DELETE_CONTACT = "https://" + server + ".lastingsales.com/api/v1/lead/";
    public static String UPDATE_CONTACT = "https://" + server + ".lastingsales.com/api/v1/lead/";
    public static String UPDATE_NOTE = "https://" + server + ".lastingsales.com/api/v1/lead/";
    public static String UPDATE_AGENT = "https://" + server + ".lastingsales.com/api/v1/user";
    public static String GET_CONTACTS = "https://" + server + ".lastingsales.com/api/v1/lead";
    public static String GET_NOTES = "https://" + server + ".lastingsales.com/api/v1/lead";
    public static String GET_COLUMNS = "https://" + server + ".lastingsales.com/api/v1/column";
    public static String GET_PROFILE = "http://" + server + ".lastingsales.com/api/v1/graph/public/profile";
    public static String GET_CUSTOMER_HISTORY = "https://" + server + ".lastingsales.com/api/v1/graph/lead/agent/relation";
    public static final String PRIVACY_POLICY = "https://lastingsales.com/privacy.html";
    public static final String FORGOT_PASSWORD = "https://app.lastingsales.com/#/access/forgotpwd";

    public static final String FILE_UPLOAD_URL = "https://api.lastingsales.com/api/v1/resource/recording";
//    public static final String FILE_UPLOAD_URL = "http://54.91.47.130:3000/api/interviewee/img";
    //	public static final String FILE_UPLOAD_URL = "http://192.168.8.37/AndroidFileUpload/fileUpload.php";
}
