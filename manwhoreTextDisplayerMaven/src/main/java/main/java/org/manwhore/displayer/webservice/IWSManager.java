/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.webservice;

import java.util.List;
import java.util.Map;

/**
 *
 * @author sigi
 */
public interface IWSManager {

    int cnvFinalize(String hash, String title, String person);

    int cnvPrepare(String convHash, Map outputMap);

    int cnvSend(String convHash, List<Map> messages);

    int createUser(String username, String password, String email);

    String getErrMessage();

    String getUser();

    int isLoggedIn();

    int login();

    int logout();
    
}
