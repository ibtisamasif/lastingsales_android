package com.example.muzafarimran.lastingsales.utils;

import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 1/30/2018.
 */

@Deprecated
public class DynamicColumnBuilderVersion2 {
    public static final String TAG = "DynamicColumnBuilder2";

    private ArrayList<Column> columnCollection = new ArrayList<Column>();

    //give JSON to it and it will parse
    public void parseJson(String str) {
        Log.d(TAG, "parseJson: str: " + str);

        try {

            List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
            JSONObject jsonObject = new JSONObject(str);

            for (int j = 0; j < allColumns.size(); j++) {
                if (jsonObject.has(allColumns.get(j).getName())) {
                    Log.d(TAG, "parseJson: HAS: " + allColumns.get(j).getName());
                    Column column = new Column();
                    column.id = allColumns.get(j).getServerId();
                    column.name = allColumns.get(j).getName(); //myCar
                    column.value = jsonObject.getString(allColumns.get(j).getName());  //hondaa
                    column.column_type = allColumns.get(j).getColumnType();
                    columnCollection.add(column);
                } else {
                    Log.d(TAG, "parseJson: DONT HAS: " + allColumns.get(j).getName());
                }
            }
//            JSONArray jsonarray = new JSONArray(str);
//            for (int j = 0; j < jsonarray.length(); j++) {
//                JSONObject jsonobject = jsonarray.getJSONObject(j);
//                Column column = new Column();
//                column.id = jsonobject.getString("id");
//                column.value = jsonobject.getString("value");
//                column.name = jsonobject.getString("name");
//                column.column_type = jsonobject.getString("column_type");
//                columnCollection.add(column);
//            }

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

    public String buildJSONversion2() {
        Collection<Column> ints = columnCollection;


        JsonObject obj = new JsonObject();

        for (Column oneColumn : columnCollection) {
            obj.addProperty(oneColumn.name, oneColumn.value);
        }

        return obj.toString();

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
