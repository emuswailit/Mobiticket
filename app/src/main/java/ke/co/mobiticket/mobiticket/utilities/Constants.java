package ke.co.mobiticket.mobiticket.utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String BASE_URL = "https://apis.mobiticket.co.ke/v2/";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EMAIL_ADDRESS = "email";
    public static final String PHONE_NUMBER = "phone";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String ID = "id";
    public static final String SHARED_PREFRENCES ="mobiticketsharedpreferences" ;
    public static final String GENDER = "gender";
    public static final String IPRS_ACTION = "validate-identity";
    public static final String CREATE_ACTION ="create" ;
    public static final String GENERATE_OTP_ACTION ="generate-otp" ;
    public static final String VALIDATE_OTP_ACTION ="validate-otp" ;
    public static final String LOGIN_ACTION ="login" ;
    public static final String MIDDLE_NAME = "middle_name";
    public static final String ID_NUMBER = "id_number";
    public static final String STREET_ADDRESS = "street_address";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String STATUS = "status";
    public static final String DRIVE ="" ;
    public static final String OWNED ="" ;
    public static final String CONDUCT ="" ;
    public static final String UPDATE_USER_ACTION ="update" ;
    public static final String ENTIRE_RESPONSE ="entire_response" ;
    public static final String READ_ONE_ACTION = "read-one";
    public static final String SEARCH_VEHICLES = "searchvehicles";
    public static final String SEAT_COUNT ="seat_count" ;
    public static final String SOURCE = "AndroidApp";
    public static final String TRAVEL_FROM ="travel_from" ;
    public static final String TRAVEL_TO = "travel_to";
    public static final String TRAVEL_DATE = "";
    public static final String SEARCH_ACTION = "search";
    public static final String NO_INTERNET_TITLE ="No Internet connection!" ;
    public static final String PASSENGER_DATA_THIS_BOOKING ="passenger_data_this_booking" ;
    public static final String READ_ACTION = "read";
    public static String NO_INTERNET_MESSAGE="Internet connection is required for this function!";
    public static String CREATE_REF_NUMBER_ACTION="createreferencenumber";
    String title="No internet connection!";



    /*intent data*/
    public static class intentdata
    {
        public static String CARDDETAIL="carddetail";
        public static String TRAVELLERNAME="TravellerName";
        public static String TYPECOACH="typecoach";
        public static String PRICE="price";
        public static String HOLD="hold";
        public static String PACKAGE="package";
        public static String OFFER="offer";
        public static String TRIP_KEY="trip_key";
        public static String SEARCH_BUS="search_bus";
        public static String PACKAGE_NAME="package_name";
        public static String CARDFLAG="cardflag";
        public static String FROM="from";
        public static String TO="to";
    }


    /*Date format*/
    public static class DateFormat {
        public static String dd_MM = "dd-MMM";
        public static String  dd_MM_yyyy = "dd - MMM - yyyy";
        public static String  yyyy_MM_dd = "yyyy-MMM-dd";
        public static final SimpleDateFormat DAY_MONTH_FORMATTER = new SimpleDateFormat(dd_MM, Locale.getDefault());
        public static final SimpleDateFormat  DAY_MONTH_YEAR_FORMATTER= new SimpleDateFormat(dd_MM_yyyy, Locale.getDefault());
        public static final SimpleDateFormat  YEAR_MONTH_DAY_FORMATTER= new SimpleDateFormat(yyyy_MM_dd, Locale.getDefault());

    }
}
