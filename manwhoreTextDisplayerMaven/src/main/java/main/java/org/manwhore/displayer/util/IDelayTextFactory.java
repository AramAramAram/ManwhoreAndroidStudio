/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import com.google.inject.assistedinject.Assisted;

/**
 *
 * @author sigi
 */
public interface IDelayTextFactory {
    public DelayText create(@Assisted("startTime") Long startTime, @Assisted("endTime") Long endTime);
}
