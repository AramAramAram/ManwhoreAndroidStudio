/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

/**
 *
 * @author sigi
 */
public interface IPersonResolver {

    String getKeyAddressPart(String address);

    String getPersonByAddress(String address);
    
}
