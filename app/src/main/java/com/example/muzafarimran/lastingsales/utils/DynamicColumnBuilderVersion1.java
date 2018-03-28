package com.example.muzafarimran.lastingsales.utils;

import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 4/10/2017.
 */

public class DynamicColumnBuilderVersion1 {
    public static final String TAG = "DynamicColumnBuilder1";

    private ArrayList<Column> columnCollection = new ArrayList<Column>();

    //give JSON to it and it will parse
    public void parseJson(String str) {
        try {
            JSONArray jsonarray = new JSONArray(str);
            for (int j = 0; j < jsonarray.length(); j++) {
                JSONObject jsonobject = jsonarray.getJSONObject(j);
                Column column = new Column();
                column.id = jsonobject.getString("id");
                column.value = jsonobject.getString("value");
                column.name = jsonobject.getString("name");
                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                if (allColumns != null) {
                    for (int k = 0; k < allColumns.size(); k++) {
                        if (jsonobject.getString("name").equalsIgnoreCase(allColumns.get(k).getName())) {
                            column.column_type = allColumns.get(k).getColumnType();
                        }
                    }
                }
//                column.column_type = jsonobject.getString("column_type"); // crash here org.json.JSONException: No value for column_type "column_type" OR "type"
                columnCollection.add(column);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Column getById(int id) {

        for (Column column : columnCollection) {
            if (column.id.equals(Integer.toString(id))) {
                return column;
            }
        }
        return null;
    }


    public int getLength() {
        return columnCollection.size();
    }

    public boolean addColumn(Column column) {
        try {
            columnCollection.add(column);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Column> getColumns() {
        return columnCollection;
    }

    public String buildJSON() {
        Gson gson = new Gson();
        Collection<Column> ints = columnCollection;
        String json = gson.toJson(ints);  // ==> json is [1,2,3,4,5]
        Log.d(TAG, "buildJSON: " + json);
        return json;
    }


    public static class Column {
        public String id;
        public String value;
        public String name;
        public String column_type;
    }
}