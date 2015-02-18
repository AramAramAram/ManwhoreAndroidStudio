/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import com.google.inject.throwingproviders.ThrowingProvider;

/**
 *
 * @author sigi
 */
public interface ObjectProvider<T> extends ThrowingProvider<T, Exception> {
    
}
