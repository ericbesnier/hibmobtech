package com.hibernatus.hibmobtech.model.dao;

/**
 * Created by Eric on 09/09/2016.
 */

public interface DaoFactory {
    public CauseDao getCauseDao();
}










/*public interface DaoFactory {

    public static final int SQLITE = 0;
    public static final int COUCHBASE = 1;

    public CauseDao getCauseDao();

    public static DaoFactory getDaoFactory(int type, Context context){
        switch (type){
            case SQLITE:
                return new SQLiteBaseDaoFactory(context);
            case COUCHBASE:
                return new CouchBaseDaoFactory(context);
            default:
                return null;
        }
    }
}*/
