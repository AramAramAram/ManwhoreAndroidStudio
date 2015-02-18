package org.manwhore.displayer.webservice;

import android.app.Application;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.InputStream;
import org.manwhore.displayer.R;
import org.manwhore.displayer.util.Constants;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.util.List;
import java.util.Map;
import org.manwhore.displayer.util.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlrpc.android.SSLClientFactory;


/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 13.3.2011
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
public class WSManager implements IWSManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WSManager.class);

    private static final String METHOD_LOGIN = "user.login";
    private static final String METHOD_LOGOUT = "user.logout";
    private static final String METHOD_CNVPREPARE = "mwsync.cnvprepare";
    private static final String METHOD_CNVSEND = "mwsync.cnvsend";
    private static final String METHOD_ISLOGGEDIN = "mwsync.isloggedin";
    private static final String METHOD_CNVFINALIZE = "mwsync.cnvfinalize";
    private static final String METHOD_CREATEUSER = "mwsync.createuser";


    private static final String ERRCODE_LOGIN = "loginFailed";
    private static final String ERRCODE_OTHER = "other";

    private static final String FIELD_ERROR = "error";

    private XMLRPCClient client;
    private String user;
    private String pass;

    private String errMessage;

    public WSManager(Application context, SharedPreferences prefs) throws IOException, XMLRPCException
    {        
        InputStream keystoreStream = context.getAssets().open(Constants.KEYSTORE_FILENAME);
        String url = context.getResources().getString(R.string.webservice_url);        
        client = SSLClientFactory.create(url, keystoreStream, Constants.KEYSTORE_PASS);
        
        user = prefs.getString(Constants.SETTINGS_USER, "");
        pass = prefs.getString(Constants.SETTINGS_PASS, "");
        LOGGER.info("Created WSManager. Url: {}", url);
    }

    @Override
    public String getErrMessage() {
        return errMessage;
    }

    @Override
    public int login()
    {
        LOGGER.debug("Calling login - User: {}", user);
        if (user.equals("") || pass.equals("")) {
            return Constants.WSRESULT_PROFILE_UNDEFINED;
        }

        try {
           Object response = client.call(METHOD_LOGIN, user, pass);
           if (response instanceof Map)
           {
               Map map = (Map) response;
               if (map.get("sessid") != null) {
                   LOGGER.debug("Login successful...");
                   return Constants.WSRESULT_OK;
               }
           }
           LOGGER.debug("Login failed!");
           return Constants.WSRESULT_NOT_LOGGED_IN;

        } catch (XMLRPCException e) {
            LOGGER.error("Login error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }

    }

    @Override
    public int logout()
    {
        LOGGER.debug("Calling logout. User: {}", user);
        try {
           Object response = client.call(METHOD_LOGOUT);
           if (response instanceof Boolean)
           {
               if ((Boolean)response) {
                   LOGGER.debug("User {} successfully logged out.", user);
                   return Constants.WSRESULT_OK;
               }
           }
           LOGGER.debug("Logout failed");
           return Constants.WSRESULT_SERVER_ERROR;
        } catch (XMLRPCException e) {
            LOGGER.error("Logout error:", e);
            return Constants.WSRESULT_CALL_FAIL;
        }

    }

    @Override
    public int isLoggedIn()
    {
        LOGGER.debug("Calling isLogggedIn. User: {}", user);
        try {
           Object response = client.call(METHOD_ISLOGGEDIN);
           if (response instanceof Boolean)
           {
               if ((Boolean)response) {
                   LOGGER.debug("User {} is logged in.");
                   return Constants.WSRESULT_OK;
               }
           }
           LOGGER.debug("User {} is not logged in.", user);
           return Constants.WSRESULT_NOT_LOGGED_IN;

        } catch (XMLRPCException e) {
            LOGGER.error("isLoggedIn call error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }
    }

    @Override
    public int cnvPrepare(String convHash, Map outputMap)
    {
        LOGGER.debug("Calling cnvPrepare. User: {}, Hash: {}", user, convHash);
        try {

           Object response = client.call(METHOD_CNVPREPARE, convHash);
           if (response instanceof Map)
           {
               Map map = (Map) response;
               LoggerUtils.logObject(LOGGER, "Response", response);
               if (map.get(FIELD_ERROR) == null)
               {                   
                   LOGGER.debug("cnvPrepare call successful.");
                   outputMap.putAll(map);
                   return Constants.WSRESULT_OK;
               }
               else if (ERRCODE_LOGIN.equals(map.get(FIELD_ERROR)))
               {
                   LOGGER.debug("cnvPrepare call failed (not logged in).");
                   return Constants.WSRESULT_NOT_LOGGED_IN;
               }
           }

           LOGGER.debug("cnvPrepare call failed!");
           return Constants.WSRESULT_SERVER_ERROR;

        } catch (XMLRPCException e) {
            LOGGER.error("cnvPrepare call error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }

    }

    @Override
    public int cnvSend(String convHash, List<Map> messages)
    {
        LOGGER.debug("Calling cnvSend. User: {}, Messages: {}, Hash: {}", new Object[]{user, messages.size(), convHash});
        try {

           Object response = client.call(METHOD_CNVSEND, convHash, messages);
           if (response instanceof Map)
           {
               Map map = (Map) response;
               LoggerUtils.logObject(LOGGER, "Response", response);
               if (map.get(FIELD_ERROR) == null) {
                   LOGGER.debug("cnvSend called successfully.");
                   return Constants.WSRESULT_OK;
               }
               else if (ERRCODE_LOGIN.equals(map.get(FIELD_ERROR)))
               {
                   LOGGER.debug("cnvSend call failed (not logged in).");
                   return Constants.WSRESULT_NOT_LOGGED_IN;
               }
           }
           LOGGER.debug("cnvSend call failed.");
           return Constants.WSRESULT_SERVER_ERROR;

        } catch (XMLRPCException e) {
            LOGGER.error("cnvSend call error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }
    }

    @Override
    public int cnvFinalize(String hash, String title, String person)
    {
        LOGGER.debug("Calling cnvFinalize. User: {}, Hash: {}, Title: {}, Person: {}", new Object[]{user, hash, title, person});
        try {

           Object response = client.call(METHOD_CNVFINALIZE, hash, title, person);
           if (response instanceof Map)
           {
               Map map = (Map) response;
               LoggerUtils.logObject(LOGGER, "Response", response);
               if (map.get(FIELD_ERROR) == null) {
                   LOGGER.debug("cnvFinalize call successful.");
                   return Constants.WSRESULT_OK;
               }
               else if (ERRCODE_LOGIN.equals(map.get(FIELD_ERROR)))
               {
                   LOGGER.debug("cnvFinalize call failed (not logged in)");
                   return Constants.WSRESULT_NOT_LOGGED_IN;
               }
           }
           LOGGER.debug("cnvFinalize call failed.");
           return Constants.WSRESULT_SERVER_ERROR;

        } catch (Exception e) {            
            LOGGER.error("cnvFinalize error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }
    }

    @Override
    public int createUser(String username, String password, String email)
    {
        LOGGER.debug("Caling createUser. Username: {}, email: {}", username, email);
        try {
            Object response = client.call(METHOD_CREATEUSER, username, password, email);

            if (response instanceof Map)
            {
                Map map = (Map) response;
                LoggerUtils.logObject(LOGGER, "Response", response);
                if (map.get(FIELD_ERROR) == null) {
                    LOGGER.debug("createUser call successful.");
                    return Constants.WSRESULT_OK;
                }
            }
            LOGGER.debug("createUser call failed.");
            return Constants.WSRESULT_SERVER_ERROR;

        } catch (XMLRPCException e) {
            LOGGER.error("createUser call error", e);
            return Constants.WSRESULT_CALL_FAIL;
        }
    }

    public XMLRPCClient getClient()
    {
        return client;
    }

    @Override
    public String getUser() {
        return user;
    }
}
