package com.example.wirecamp.activity.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.wirecamp.activity.resources.BaseResource;

/**
 * Created by Pramod on 27-09-2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private String TAG = "DatabaseHandler";
    public static final String DATABASE_NAME = "weather.db";
    public static final int DATABASE_VERSION = 26;
    private String KEY_ID = "id";
    private List<Class> classList;

    private DatabaseHandler(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DatabaseHandler instance;

    public synchronized static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    public void init(List<Class> classList) {
        this.classList = classList;
    }

    private String getCreateSQL(Class aClass) {
        StringBuilder builder = new StringBuilder();
        builder.append("create table " + aClass.getSimpleName().toLowerCase() + " ");
        builder.append("(" + KEY_ID + " text primary key");
        Field[] fields = aClass.getDeclaredFields();
        if ((fields != null)) {
            for (Field field : fields) {
                String type = field.getType().getSimpleName();
                String fieldName = field.getName().toLowerCase();
                if (fieldName.equals("id")) continue;
                if ((type.equals("Integer") || (type.equals("int")))) {
                    builder.append("," + fieldName + " INTEGER");
                } else if ((type.equals("Long") || (type.equals("long")))) {
                    builder.append("," + fieldName + " INT8");
                } else if ((type.equals("Double")) || (type.equals("double"))) {
                    builder.append("," + fieldName + " REAL");
                } else if ((type.equals("Number")) || (type.equals("number"))) {
                    builder.append("," + fieldName + " REAL");
                } else if (type.equals("String")) {
                    builder.append("," + fieldName + " TEXT");
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (classList == null) {
            Log.d(TAG, "Need to call init() method first...");
            return;
        }
        for (Class aClass : classList) {
            String createSQL = getCreateSQL(aClass);
            db.execSQL(createSQL);
            Log.d("DatabaseHandler", "Table Created Successfully :: " + aClass.getSimpleName().toLowerCase());
        }

    }

    public void autoUpgrade() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Class aClass : classList) {
            Field[] fields = aClass.getDeclaredFields();
            Map<String, Boolean> dbFields = new HashMap<>();
            try {
                String sql = "select * from " + aClass.getSimpleName().toLowerCase();
                Cursor cursor = db.rawQuery(sql, null);
                int count = cursor.getColumnCount();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        for (int i = 0; i < count; i++) {
                            String column_name = cursor.getColumnName(i);
                            dbFields.put(column_name.toLowerCase(), true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ((fields != null) && dbFields.size() > 0) {
                for (Field field : fields) {
                    if (dbFields.get(field.getName().toLowerCase()) == null) {
                        try {
                            String command = "alter table " + aClass.getSimpleName().toLowerCase() + " ADD COLUMN ";
                            String type = field.getType().getSimpleName();
                            String fieldName = field.getName().toLowerCase();
                            if ((type.equals("Integer") || (type.equals("int")))) {
                                command = command + fieldName + " INTEGER";
                            } else if ((type.equals("Long") || (type.equals("long")))) {
                                command = command + fieldName + " INT8";
                            } else if ((type.equals("Double")) || (type.equals("double"))) {
                                command = command + fieldName + " REAL";
                            } else if ((type.equals("Number")) || (type.equals("number"))) {
                                command = command + fieldName + " REAL";
                            } else if (type.equals("String")) {
                                command = command + fieldName + " TEXT";
                            }
                            db.execSQL(command);
                        } catch(Exception e) {
                            e.printStackTrace();
                            db.execSQL("DROP TABLE IF EXISTS " + aClass.getSimpleName().toLowerCase());
                            String createSQL = getCreateSQL(aClass);
                            db.execSQL(createSQL);
                            Log.d("DatabaseHandler", "Table droped Successfully :: " + aClass.getSimpleName().toLowerCase());
                        }
                    }
                }
            } else {
                db.execSQL("DROP TABLE IF EXISTS " + aClass.getSimpleName().toLowerCase());
                String createSQL = getCreateSQL(aClass);
                db.execSQL(createSQL);
                Log.d("DatabaseHandler", "Table Created Successfully :: " + aClass.getSimpleName().toLowerCase());
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (classList == null) {
            Log.d(TAG, "Need to call init() method first...");
            return;
        }
        for (Class aClass : classList) {
            db.execSQL("DROP TABLE IF EXISTS " + aClass.getSimpleName().toLowerCase());
        }
        onCreate(db);
    }

    public List<BaseResource> getAll(BaseResource resource) {
        return getByExpression(resource, null);
    }


    public List<BaseResource> getByExpression(BaseResource resource, String expression) {
        return getByExpression(resource, expression, null, null);
    }

    public List<BaseResource> getLatest(BaseResource resource, String expression, Integer number) {
        return getByExpression(resource, expression, 0, number);
    }

    public List<BaseResource> getByExpression(BaseResource resource, String expression, Integer pageNo, Integer pageSize) {
        String TABLE_NAME = resource.getClass().getSimpleName().toLowerCase();
        List<BaseResource> list = new ArrayList<>();
        String sql = "SELECT  * FROM " + TABLE_NAME;
        if (expression != null) {
            sql += " where " + expression;
        }

        if (pageNo != null) {
            sql = sql + " LIMIT " + pageNo * pageSize + ", " + pageSize;
        }
            SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor == null) {
                db.close();
                return null;
            }
            if (cursor.moveToFirst()) {
                do {
                    BaseResource _resource = resource.getClone();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        Field[] fields = resource.getClass().getDeclaredFields();
                        if ((fields != null)) {
                            for (Field field : fields) {
                                String fieldName = field.getName().toLowerCase();
                                String type = field.getType().getSimpleName();
                                if (type.equals("Integer") || (type.equals("int"))) {
                                    jsonObject.put(fieldName, cursor.getInt(cursor.getColumnIndex(fieldName)));
                                } else if (type.equals("Long") || (type.equals("long"))) {
                                    jsonObject.put(fieldName, cursor.getLong(cursor.getColumnIndex(fieldName)));
                                } else if (type.equalsIgnoreCase("Double")) {
                                    jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                                } else  if (type.equals("Number") || (type.equals("Number"))) {
                                    jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                                }else if (type.equals("String")) {
                                    jsonObject.put(fieldName, cursor.getString(cursor.getColumnIndex(fieldName)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        cursor.moveToNext();
                        e.printStackTrace();
                        continue;
                    }
                    _resource.convertJSONObject2Resource(jsonObject);
                    list.add(_resource);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }


    public List<BaseResource> getByExpressionByGroup(BaseResource resource, String expression, String sumField) {
        String TABLE_NAME = resource.getClass().getSimpleName().toLowerCase();
        List<BaseResource> list = new ArrayList<>();
        String sql = "SELECT  *,count(*) count, sum(" + sumField + ") " + sumField + " FROM " + TABLE_NAME;
        if (expression != null) {
            sql += expression;
        }
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor == null) {
                db.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                do {
                    BaseResource _resource = resource.getClone();
                    JSONObject jsonObject = new JSONObject();
                    Field[] fields = resource.getClass().getDeclaredFields();
                    if ((fields != null)) {
                        for (Field field : fields) {
                            String fieldName = field.getName().toLowerCase();
                            String type = field.getType().getSimpleName();
                            if (type.equals("Integer") || (type.equals("int"))) {
                                jsonObject.put(fieldName, cursor.getInt(cursor.getColumnIndex(fieldName)));
                            } else if (type.equals("Long") || (type.equals("long"))) {
                                jsonObject.put(fieldName, cursor.getLong(cursor.getColumnIndex(fieldName)));
                            } else if (type.equalsIgnoreCase("Double")) {
                                jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                            } else  if (type.equals("Number") || (type.equals("Number"))) {
                                jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                            } else if (type.equals("String")) {
                                jsonObject.put(fieldName, cursor.getString(cursor.getColumnIndex(fieldName)));
                            }
                        }
                    }
                    _resource.convertJSONObject2Resource(jsonObject);
                    list.add(_resource);
                } while (cursor.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    public BaseResource getById(BaseResource resource, String id) {
        String TABLE_NAME = resource.getClass().getSimpleName().toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("select * from " + TABLE_NAME + " where id='" + id + "'", null);
            if (cursor == null) {
                db.close();
                return null;
            }
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return null;
        }
        if (cursor.moveToFirst()) {
            JSONObject jsonObject = new JSONObject();
            Field[] fields = resource.getClass().getDeclaredFields();
            try {
                if ((fields != null)) {
                    for (Field field : fields) {
                        String fieldName = field.getName().toLowerCase();
                        String type = field.getType().getSimpleName();
                        if (type.equals("Integer") || (type.equals("int"))) {
                            jsonObject.put(fieldName, cursor.getInt(cursor.getColumnIndex(fieldName)));
                        } else if (type.equals("Long") || (type.equals("long"))) {
                            jsonObject.put(fieldName, cursor.getLong(cursor.getColumnIndex(fieldName)));
                        } else if (type.equals("Double") || (type.equals("double"))) {
                            jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                        } else  if (type.equals("Number") || (type.equals("Number"))) {
                            jsonObject.put(fieldName, cursor.getDouble(cursor.getColumnIndex(fieldName)));
                        }else if (type.equals("String")) {
                            jsonObject.put(fieldName, cursor.getString(cursor.getColumnIndex(fieldName)));
                        }
                    }
                }
                resource.convertJSONObject2Resource(jsonObject);
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
            return resource;
        }
        return null;
    }

    public void addOrUpdate(BaseResource resource) {
        try {
            BaseResource _resource = resource.getClone();
            _resource = getById(_resource, resource.getId());
            if (_resource != null)
                update(resource);
            else
                add(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addIfNotExists(BaseResource resource) {
        try {
            BaseResource _resource = resource.getClone();
            _resource = getById(_resource, resource.getId());
            if (_resource == null) {
                add(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void add(BaseResource resource) {
        String TABLE_NAME = resource.getClass().getSimpleName().toLowerCase();
        if (resource.getId() == null) {
            return;
            //resource.setId(UUID.randomUUID().toString());
        }
        ContentValues values = new ContentValues();
        Field[] fields = resource.getClass().getDeclaredFields();
        try {
            JSONObject jsonObject = resource.convert2JSONObject();
            for (Field field : fields) {
                String fieldName = field.getName().toLowerCase();
                String type = field.getType().getSimpleName();

                if (!jsonObject.has(fieldName)) continue;

                if ((type.equals("Integer") || (type.equals("int")))) {
                    values.put(fieldName, jsonObject.getInt(fieldName));
                } else if ((type.equals("Long") || (type.equals("long")))) {
                    values.put(fieldName, jsonObject.getLong(fieldName));
                } else if (type.equalsIgnoreCase("Double")) {
                    values.put(fieldName, jsonObject.getDouble(fieldName));
                } else if ((type.equals("Number") || (type.equals("number")))) {
                    values.put(fieldName, jsonObject.getDouble(fieldName));
                } else if (type.equals("String")) {
                    values.put(fieldName, jsonObject.getString(fieldName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.insert(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        Log.d("DatabaseHandler", "1 Record Inserted Successfully : id " + resource.getId() + " :: " + TABLE_NAME);
    }

    public int update(BaseResource resource) {
        String TABLE_NAME = resource.getClass().getSimpleName().toLowerCase();
        if (resource.getId() == null) {
            Log.d(TAG, "Unable to update :: id is null");
            return -1;
        }
        ContentValues values = new ContentValues();
        Field[] fields = resource.getClass().getDeclaredFields();
        try {
            JSONObject jsonObject = resource.convert2JSONObject();
            for (Field field : fields) {
                String fieldName = field.getName().toLowerCase();
                String type = field.getType().getSimpleName();

                if (!jsonObject.has(fieldName)) continue;

                if ((type.equals("Integer") || (type.equals("int")))) {
                    values.put(fieldName, jsonObject.getInt(fieldName));
                } else if ((type.equals("Long") || (type.equals("long")))) {
                    values.put(fieldName, jsonObject.getLong(fieldName));
                } else if (type.equalsIgnoreCase("Double")) {
                    values.put(fieldName, jsonObject.getDouble(fieldName));
                } else if ((type.equals("Number") || (type.equals("number")))) {
                    values.put(fieldName, jsonObject.getDouble(fieldName));
                }else if (type.equals("String")) {
                    values.put(fieldName, jsonObject.getString(fieldName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int rc = -1;
        db.beginTransaction();
        try {
            rc = db.update(TABLE_NAME, values, KEY_ID + "=?",
                    new String[]{resource.getId()});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        Log.d("DatabaseHandler", "Table Updated Successfully :: " + resource.getClass().getSimpleName().toLowerCase());
        return rc;
    }

    public void delete(Class aClass, String id) {
        String TABLE_NAME = aClass.getSimpleName().toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?",
                new String[]{id});
        db.close();
        Log.d("DatabaseHandler", "Table :: " + TABLE_NAME + ": Row deleted with ID : " + id);
    }

    public void deleteByExpression(Class aClass, String expression) {
        String TABLE_NAME = aClass.getSimpleName().toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("delete from " + TABLE_NAME + " where " + expression);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteAll(Class aClass) {
        String TABLE_NAME = aClass.getSimpleName().toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        Log.d("DatabaseHandler", "Table :: " + TABLE_NAME + ": All Row deleted");
        db.close();
    }

    public void dumpAllRecords(BaseResource resource) {
        List<BaseResource> test_list = getAll(resource);
        for (int i = 0; i < test_list.size(); i++) {
            BaseResource object = (BaseResource) test_list.get(i);
            try {
                String str = object.convert2JSONObject().toString();
                System.out.println(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
