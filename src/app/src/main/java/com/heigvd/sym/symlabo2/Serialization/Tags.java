package com.heigvd.sym.symlabo2.Serialization;

public class Tags {


    private final static String DIRECTORY       = "directory";

    private final static String PERSON          = "person";
    private final static String NAME            = "name";
    private final static String FIRSTNAME       = "firstname";
    private final static String MIDDLENAME      = "middlename";
    private final static String GENDER          = "gender";
    private final static String PHONE           = "phone";

    private final static String HOME           = "home";
    private final static String WORK           = "work";
    private final static String MOBILE         = "mobile";


    public final static String INFOS           = "infos";

    public final static String XML_HEADER  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public final static String XML_DOCTYPE = "<!DOCTYPE directory SYSTEM \"http://sym.iict.ch/directory.dtd\">";



    public final static String XML_DIRECTORY_START             = "<" + DIRECTORY +">";
    public final static String XML_DIRECTORY_END               = "</" + DIRECTORY +">";

    public final static String XML_PERSON_START                = "<" + PERSON +">";
    public final static String XML_PERSON_END                  = "</" + PERSON +">";

    public final static String XML_NAME_START                  = "<" + NAME +">";
    public final static String XML_NAME_END                    = "</" + NAME +">";

    public final static String XML_FIRSTNAME_START             = "<" + FIRSTNAME +">";
    public final static String XML_FIRSTNAME_END               = "</" + FIRSTNAME +">";

    public final static String XML_MIDDLENAME_START            = "<" + MIDDLENAME +">";
    public final static String XML_MIDDLENAME_END              = "</" + MIDDLENAME +">";


    public final static String XML_GENDER_START                = "<" + GENDER +">";
    public final static String XML_GENDER_END                  = "</" + GENDER +">";


    public final static String XML_INFOS_START                 = "<" + INFOS +">";
    public final static String XML_INFOS_END                   = "</" + INFOS +">";


    public enum PHONE_TYPE{
        HOME,
        WORK,
        MOBILE
    }

    public static String generateXMLPhoneTag(String phoneNumbr, PHONE_TYPE phone_type){
        String XMLPhoneTag = "";
        String phone_type_str;

        switch (phone_type){
            case HOME:   phone_type_str = HOME;     break;
            case WORK:   phone_type_str = WORK;     break;
            case MOBILE: phone_type_str = MOBILE;   break;
            default:     phone_type_str = "";
        }

        XMLPhoneTag  = "<" + PHONE + " type=\"" + phone_type_str + "\">";
        XMLPhoneTag += phoneNumbr;
        XMLPhoneTag +=  "</" + PHONE + ">";

        return XMLPhoneTag;
    }
}
