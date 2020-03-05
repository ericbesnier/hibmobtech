package com.hibernatus.hibmobtech.database.couchbase;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.dao.CauseDao;
import com.hibernatus.hibmobtech.model.dao.DAOConfigurationException;
import com.hibernatus.hibmobtech.model.dao.DaoFactory;

/**
 * Created by Eric on 03/09/2016.
 */

public class CouchBaseDaoFactory implements DaoFactory {
    public static final String TAG = CouchBaseDaoFactory.class.getSimpleName();

    protected Context context;
    private static CouchBaseDaoFactory INSTANCE = null;

    private Manager couchBaseManager = null;
    private Database couchDataBase = null;

    @Override
    public CauseDao getCauseDao() {
        Log.d(TAG, "getCauseDao");
        return new CouchBaseDaoCause( this );
    }

    synchronized public static CouchBaseDaoFactory getInstance(Context context) {
        Log.d(TAG, "getInstance");
        if (INSTANCE == null) {
            INSTANCE = new CouchBaseDaoFactory(context);
        }
        return INSTANCE;
    }

    public CouchBaseDaoFactory(Context context) throws DAOConfigurationException {
        Log.d(TAG, "Constructor: CouchBaseDaoFactory(Context context)");
        this.context = context;
        try {
            AndroidContext androidContext = new AndroidContext(context);
            couchBaseManager = new Manager(androidContext, Manager.DEFAULT_OPTIONS);
            couchDataBase = couchBaseManager.getDatabase(HibmobtechApplication.getCouchBaseName());
        } catch (Exception ex) {
            Log.e(TAG, "Error getting couchDataBase");
            throw new DAOConfigurationException( "Error getting couchDataBase");
        }
    }

    public Manager getCouchBaseManager() {
        return couchBaseManager;
    }

    public Database getCouchDataBase() {
        return couchDataBase;
    }
}





/*    public View getCausesViewByDescription(final String description) {
        Log.d(TAG, "getCausesViewByDescription: description=" + description);
        if(description.equals("")) {
            View causesView = getCouchDataBase().getView("dev_allCausesView");
            causesView.deleteIndex();
            Mapper map = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    Log.d(TAG, "allCausesView: " + document.toString());
                    if ("cause".equals(document.get("type"))) {
                        List<Object> keys = new ArrayList<>();
                        keys.add(document.get("id"));
                        emitter.emit(keys, document);
                    }
                }
            };
            causesView.setMap(map, "1.0");
            return causesView;
        }
        else {
            View causesView = getCouchDataBase().getView("dev_causesByDescriptionView");
            causesView.deleteIndex();
            Mapper map = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    if(document.get("description") != null) {
                        CharSequence docDesc = (CharSequence) document.get("description");
                        boolean match = (Pattern.matches(".*" + description, docDesc)
                                || (Pattern.matches(description + ".*", docDesc)))
                                || (Pattern.matches(".*" + description + ".*", docDesc)
                                || (Pattern.matches(".*" + description.toUpperCase(), docDesc)
                                || (Pattern.matches(description.toUpperCase() + ".*", docDesc)))
                                || (Pattern.matches(".*" + description.toUpperCase() + ".*", docDesc)));

                        Log.e(TAG, "causesByDescriptionView: description=" + description
                                 + " match=" + match
                                 + " document=" + document.toString());
                        if ("cause".equals(document.get("type")) && match) {
                            List<Object> keys = new ArrayList<>();
                            keys.add(document.get("id"));
                            emitter.emit(keys, document);
                        }
                    }
                }
            };
            causesView.setMap(map, "1.0");
            return causesView;
        }
    }*/