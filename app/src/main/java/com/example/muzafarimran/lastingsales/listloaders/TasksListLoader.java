package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 2/1/2018.
 */

public class TasksListLoader extends AsyncTaskLoader<List<Object>> {

    private List<Object> list = new ArrayList<Object>();

    public TasksListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {


        Collection<LSTask> tasks = LSTask.getAllTasksInDescendingOrderOfTime();
        if (!tasks.isEmpty()) {

            Collection<HomeItem> listHome = new ArrayList<HomeItem>();
            HomeItem itemTask = new HomeItem();
            itemTask.text = "TASKS";
            itemTask.value = "" + tasks.size();
            itemTask.drawable = R.drawable.bg_inquiry_cardxxxhdpi;
            listHome.add(itemTask);

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Tasks";

            list.addAll(listHome);
            list.add(separatorItem);
            list.addAll(tasks);

            SeparatorItem separatorSpace = new SeparatorItem();
            separatorSpace.text = "";

            list.add(separatorSpace);
            list.add(separatorSpace);

        } else {
            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
            ErrorItem erItem = new ErrorItem();
            erItem.message = "Nothing in Tasks";
            erItem.drawable = R.drawable.ic_inquiries_empty_xxxhdpi;
            listError.add(erItem);
            list.addAll(listError);
        }
        return list;
    }
}
