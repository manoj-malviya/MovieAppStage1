package com.girnarsoft.android.tmdb;

import java.util.ArrayList;

/**
 * Created by User on 7/13/2015.
 */
public interface AsyncTaskListner<T> {
    void onTaskStarted();
    void onTaskFinished(T data);
}
