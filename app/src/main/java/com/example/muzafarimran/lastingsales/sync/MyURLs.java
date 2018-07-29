package com.example.muzafarimran.lastingsales.sync;

public class MyURLs {

//    private static final String server = "http://192.168.100.50/lastingsales-api/public";
//    private static final String server = "https://staging.lastingsales.com";
//    private static final String server = "https://api.lastingsales.com";

    private static final String server = "https://lswe-m-i6stage.azurewebsites.net";

    //    public static String LOGIN_URL_OLD = "http://api.lastingsales.com/api/auth/login";
    public static String LOGIN_URL = server + "/api/v1/auth/login";
    public static String SIGNUP_URL = server + "/api/v1/auth/register";
    public static String ADD_COMPANY_URL = server + "/api/v1/company";
    public static String IMAGE_URL = server + "/";
    public static String ADD_CONTACT = server + "/api/v1/lead";
    public static String ADD_ORGANIZATION = server + "/api/v1/organization";
    public static String ADD_DEAL = server + "/api/v1/deal";
    public static String ADD_FOLLOWUP = server + "/api/v1/lead/"; //SampleFormatComplete ADD_FOLLOWUP = "http://staging/api/v1/lead/85/followup";
    public static String ADD_NOTE = server + "/api/v1/notes"; //SampleFormatComplete ADD_NOTE = "http://staging/api/v1/lead/145/notes";
    public static String ADD_COMMENT = server + "/api/v1/lead/";
    public static String DELETE_NOTE = server + "/api/v1/notes/";
    public static String ADD_CALL = server + "/api/v1/call";
    public static String GET_CALL = server + "/api/v1/call";
    public static String ADD_INQUIRY = server + "/api/v1/inquiries";
    public static String UPDATE_INQUIRY = server + "/api/v1/inquiries/";
    public static String DELETE_INQUIRY = server + "/api/v1/inquiries/";
    public static String GET_INQUIRY = server + "/api/v1/inquiries/";
    public static String GET_TASK = server + "/api/v1/task";
    public static String DELETE_CONTACT = server + "/api/v1/lead/";
    public static String DELETE_ORGANIZATION = server + "/api/v1/organization/";
    public static String DELETE_DEAL = server + "/api/v1/deal/";
    public static String UPDATE_CONTACT = server + "/api/v1/lead/";
    public static String UPDATE_ORGANIZATION = server + "/api/v1/organization/";
    public static String UPDATE_DEAL = server + "/api/v1/deal/";
    public static String UPDATE_NOTE = server + "/api/v1/notes/";
    public static String UPDATE_AGENT = server + "/api/v1/user";
    public static String GET_AGENT = server + "/api/v1/user";
    public static String UPDATE_TASK = server + "/api/v1/lead";
    public static String GET_CONTACTS = server + "/api/v1/lead";
    public static String GET_DEALS = server + "/api/v1/deal";
    public static String GET_NOTES = server + "/api/v1/lead";
    public static String GET_COMMENTS = server + "/api/v1/lead";
    public static String GET_COLUMNS = server + "/api/v1/column";
    public static String GET_WORKFLOW = server + "/api/v1/admin/workflow";
    public static String GET_PROFILE = server + "/api/v1/graph/public/profile";
    public static String GET_CUSTOMER_HISTORY = server + "/api/v1/graph/lead/agent/relation";
    public static String GET_LATEST_APP_VERSION_CODE = server + "/api/v1/config";

    public static final String PRIVACY_POLICY = "https://lastingsales.com/privacy.html";
    public static final String FORGOT_PASSWORD = "https://app.lastingsales.com/#/access/forgotpwd";

    public static final String FILE_UPLOAD_URL = "https://api.lastingsales.com/api/v1/resource/recording";
//    public static final String FILE_UPLOAD_URL = "http://54.91.47.130:3000/api/interviewee/img";
    //	public static final String FILE_UPLOAD_URL = "http://192.168.8.37/AndroidFileUpload/fileUpload.php";


    public static String GET_SYNC = server + "/api/v1/sync";
}
