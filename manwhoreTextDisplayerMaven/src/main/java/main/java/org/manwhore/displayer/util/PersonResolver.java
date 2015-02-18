package org.manwhore.displayer.util;

import org.slf4j.Logger;
import android.app.Application;
import com.google.inject.Inject;
import java.util.HashMap;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import org.manwhore.displayer.logging.Util;
import org.slf4j.LoggerFactory;
import static org.manwhore.displayer.util.Constants.*;

/**
 * Helper class to retrieve contacts from contact list.
 * 
 * @author siglerv
 *
 */
public class PersonResolver implements IPersonResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonResolver.class);
    private static final String UNKNOWN = "Unknown";
        
    @Inject
    private Application context;
    private boolean cacheLoaded = false;
    private HashMap<String, String> addressCache = new HashMap<String, String>();

    private boolean needsInit = false;

    public PersonResolver() {}

//    public synchronized void init() {
//            if (!needsInit) {
//                    needsInit = true;
//                    //new CacheFillerTask().execute();
//            }
//    }
    
    private String getCachedPerson(String address) {
        return addressCache.get(address);
    }

    private synchronized void cachePerson(String address, String person) {
        addressCache.put(address, person);
    }

    @Override
    public synchronized String getPersonByAddress(String address) {
	
        if (address == null || "".equals(address)) {
            LOGGER.debug("Address empty - returning '{}'", UNKNOWN);
            return UNKNOWN;
        }
        
        String result = getCachedPerson(address);                
        if (result == null) {
            LOGGER.debug("Cache does not contain contact for address {}", Util.obfuscateShort(address));

            Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
            
            String[] projection = new String[]{PhoneLookup.DISPLAY_NAME};            
            Cursor c = null;
            
            try {
                c = context.getContentResolver().query(lookupUri, projection, null, null, null);

                if (c != null && c.moveToFirst()) {
                    LOGGER.debug("Found contact for key address '{}', caching", Util.obfuscateShort(address));
                    result = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
                    cachePerson(address, result);
                    c.close();
                } else {
                    LOGGER.debug("No contact data found for key address '{}', caching", Util.obfuscateShort(address));
                    cachePerson(address, address);
                    result = address;
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } 
        
        return result;
    }

    /**
     * Returns key part of phone number/address to be used in lookup.
     * 
     * This approach is outdated and deprecated now. Code should be
     * refactored not to use this anymore.
     * 
     * @param address
     * @return
     * @deprecated
     */
    @Deprecated
    @Override
    public String getKeyAddressPart(String address) {
        if (address == null) {
            LOGGER.debug("Requested key address part for address 'null', returning 'Unknown'.");
            return "";
        }
        
        int len = address.length();
        String keyAddress;
        if (len <= PHONE_MATCH_LENGTH) {
            keyAddress = address;
        } else {
            keyAddress = address.substring(len - PHONE_MATCH_LENGTH);
        }
        return keyAddress;
    }

    //cache preloading disabled
//    private synchronized void setCacheLoaded(boolean value) {
//        this.cacheLoaded = value;
//    }
//
//    private synchronized boolean isCacheLoaded() {
//        return this.cacheLoaded;
//    }

//    private class CacheFillerTask extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object... arg0) {
//            LOGGER.info("Starting initialization of contact cache...");
//            
//            ContentResolver cr = context.getContentResolver();
//            String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
//            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
//
//						int counter = 0;
//            if (cursor != null) {
//                try {
//                    while (cursor.moveToNext()) {
//                        String contactId =
//                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                        String displayName =
//                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        //
//                        //  Get all phone numbers.
//                        //
//                        Cursor phones = cr.query(Phone.CONTENT_URI, null,
//                                Phone.CONTACT_ID + " = " + contactId, null, null);
//                        if (phones != null) {
//                            try {
//                                while (phones.moveToNext()) {
//                                    String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
//                                    cachePerson(getKeyAddressPart(number), displayName);
//																		counter++;
//                                }
//                            } finally {
//                                phones.close();
//                            }
//                        }                    
//                    }
//                } finally {
//                    cursor.close();
//                }
//            }    
//            
//            LOGGER.info("Contact cache initialization complete. Cached {} phone numbers.", counter);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {            
//            PersonResolver.this.setCacheLoaded(true);
//						LOGGER.info("Cache loaded.");
//        }
//    }
}
