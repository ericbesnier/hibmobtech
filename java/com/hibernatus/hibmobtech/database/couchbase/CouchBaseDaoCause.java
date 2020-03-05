package com.hibernatus.hibmobtech.database.couchbase;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.dao.CauseDao;
import com.hibernatus.hibmobtech.model.dao.DAOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Eric on 23/01/2016.
 */

public class CouchBaseDaoCause implements CauseDao {
    public static final String TAG = CouchBaseDaoCause.class.getSimpleName();
    String documentId;
    private CouchBaseDaoFactory couchBaseDaoFactory;

    public CouchBaseDaoCause(CouchBaseDaoFactory couchBaseDaoFactory) {
        this.couchBaseDaoFactory = couchBaseDaoFactory;
        Document document = couchBaseDaoFactory.getCouchDataBase().createDocument();
        documentId = document.getId();
    }

    @Override
    public String addCause(Cause cause) throws DAOException {
        return createCauseDocument(couchBaseDaoFactory.getCouchDataBase(), cause);
    }

    @Override
    public List<Cause> getCauses(String description) throws DAOException {
        Log.d(TAG, "getCauses: description=" + description);
        Query query = getCausesViewByDescription(description).createQuery();
        return getCausesByQuery(query);
    }

    @Override
    public List<Cause> getAllCauses() throws DAOException {
        Log.d(TAG, "getAllCauses");
        Query query = getCausesViewByDescription("").createQuery();
        return getCausesByQuery(query);
    }

    @Override
    public int deleteAllCauses() throws DAOException {
        Log.d(TAG, "deleteAllCauses");
        Query query = getCausesViewByDescription("").createQuery();
        QueryEnumerator result = null;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        for (Iterator<QueryRow> iterator = result; iterator.hasNext(); ) {
            QueryRow queryRow = iterator.next();
            Document document = queryRow.getDocument();
            try {
                document.delete();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private String createCauseDocument(Database database, Cause cause) {
        Log.d(TAG, "createCauseDocument:" +
                " cause.getId()=" + cause.getId() +
                " cause.getDescription()=" + cause.getDescription() +
                " cause.getCategory()=" + cause.getCategory());

        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "cause");
        map.put("id", cause.getId());
        map.put("description", cause.getDescription());
        map.put("category", cause.getCategory());

        try {
            // Save the properties to the document
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
    }

    public List<Cause> getCausesByQuery(Query query) throws DAOException {
        Log.d(TAG, "getCausesByQuery");
        QueryEnumerator result = null;
        try {
            result = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        ArrayList<Cause> causeList = new ArrayList<>();
        for (Iterator<QueryRow> iterator = result; iterator.hasNext(); ) {
            QueryRow queryRow = iterator.next();
            Document document = queryRow.getDocument();
            Cause cause = new Cause();
            cause.setId(document.getProperty("id"));
            cause.setDescription((String)document.getProperty("description"));
            cause.setCategory((String)document.getProperty("category"));
            Log.d(TAG, "getCausesByQuery: cause.getId()=" + cause.getId()
                    + " cause.getDescription()=" + cause.getDescription()
                    + " cause.getCategory()=" + cause.getCategory());
            causeList.add(cause);
        }
        return causeList;
    }

    public View getCausesViewByDescription(final String description) {
        Log.d(TAG, "getCausesViewByDescription: description=" + description);
        if(description.equals("")) {
            View causesView = couchBaseDaoFactory.getCouchDataBase().getView("dev_allCausesView");
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
            View causesView = couchBaseDaoFactory.getCouchDataBase().getView("dev_causesByDescriptionView");
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

                        Log.d(TAG, "causesByDescriptionView: description=" + description
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
    }

    // TODO implementation
    // -----------------------
    @Override
    public int updateCause(int id, Cause cause) throws DAOException {
        Log.d(TAG, "updateCause");
        return 0;
    }

    @Override
    public int deleteCause(int id) throws DAOException {
        Log.d(TAG, "deleteCause");
        return 0;
    }
}
