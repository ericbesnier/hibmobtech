package com.hibernatus.hibmobtech.model.dao;

import com.hibernatus.hibmobtech.model.Cause;

import java.util.List;

/**
 * Created by Eric on 23/01/2016.
 *
 * One DAO class cause table or view
 * CRUD - Create, retrieve, onUpdatedContent, delete
 */

public interface CauseDao {

    String addCause(Cause cause) throws DAOException;

    List<Cause> getCauses(String description) throws DAOException;
    List<Cause> getAllCauses() throws DAOException;

    int updateCause(int id, Cause cause) throws DAOException;

    int deleteCause(int id) throws DAOException;
    int deleteAllCauses() throws DAOException;
}


