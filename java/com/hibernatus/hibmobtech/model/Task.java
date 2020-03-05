package com.hibernatus.hibmobtech.model;

/**
 * Created by Eric on 21/01/2016.
 */
public class Task extends BasicRefEntity {
    public static final String MRO_TASK_CODE = "mroTaskCode";
    public Task() {}
    public Task(String description) {
        super(description);
    }
}
