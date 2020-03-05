package com.hibernatus.hibmobtech.site;

import android.util.Log;

import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.model.contact.ContactRoleSite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Eric on 12/11/2015.
 */
public class SiteUtils {
    public static final String TAG = SiteUtils.class.getSimpleName();


    public static List<String> formatContactList(Site site) {
        Log.i(TAG, "formatContactList: currentSite=" + site);
        if(site == null) return null;
        String contactString = "";
        String roleString = "";
        String titleString = "";
        String nameString = "";
        String primaryPhoneString = "";


        Set<ContactRoleSite> contactRoleSites = site.getContactRoleSites();

        Log.i(TAG, "formatContactList: contactRoleSites=" + contactRoleSites);
        List<String> fullContactList = new ArrayList<>();
        Iterator<ContactRoleSite> iterator = contactRoleSites.iterator();
        while (iterator.hasNext()) {
            ContactRoleSite contactRoleSite = iterator.next();

            roleString = contactRoleSite.getRole();
            titleString = contactRoleSite.getContact().getTitle();
            nameString = contactRoleSite.getContact().getName();
            if (!contactRoleSite.getContact().getPhones().isEmpty())
                primaryPhoneString = contactRoleSite.getContact().getPhones().get(0).getNumber();

            if ((nameString != null) && (titleString != null) && (roleString != null) && (primaryPhoneString != null)){
                if(!nameString.equals(roleString)) {
                    contactString = nameString
                            + " (" + titleString + ")"
                            + "\n" + roleString
                            + "\n" + primaryPhoneString;
                }
                else{
                    contactString = nameString
                            + " (" + titleString + ")"
                            + "\n" + primaryPhoneString;
                }
            }
            if ((nameString != null) && (titleString == null) && (roleString != null) && (primaryPhoneString != null)){
                if(!nameString.equals(roleString)) {
                    contactString = nameString
                            + "\n" + roleString
                            + "\n" + primaryPhoneString;
                }
                else{
                    contactString = nameString
                            + "\n" + primaryPhoneString;
                }
            }
            if ((nameString != null) && (titleString == null) && (roleString == null) && (primaryPhoneString != null)){
                contactString = nameString
                        + "\n" + primaryPhoneString;
            }
            if ((nameString != null) && (titleString == null) && (roleString == null) && (primaryPhoneString == null)){
                contactString = nameString;
            }
            if ((nameString != null) && (titleString != null) && (roleString == null) && (primaryPhoneString != null)){
                contactString = nameString
                        + " (" + titleString + ")"
                        + "\n" + primaryPhoneString;
            }
            if ((nameString != null) && (titleString != null) && (roleString != null) && (primaryPhoneString == null)){
                contactString = nameString
                        + " (" + titleString + ")"
                        + "\n" + roleString;
            }
            if(!contactString.equals("")) {
                fullContactList.add(contactString);
            }
        }
        return fullContactList;
    }
}
