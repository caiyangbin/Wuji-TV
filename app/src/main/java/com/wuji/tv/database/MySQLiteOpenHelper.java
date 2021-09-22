package com.wuji.tv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.wuji.tv.model.Files;
import com.wuji.tv.model.PosterTabDataModel;
import com.wuji.tv.model.PosterTabModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "poster.db";
    private static final String TABLE_FILE = "tabs";

    public static final String DEVICE_ID = "device_id";
    public static final String TABS_GRADE = "tabs_grade";//区分一级tab/二级tab
    public static final String DATA = "data";
    public static final String TABLE = "_table";
    public static final String POSITION_TOP_TAB = "position_top_tab";
    public static final String POSITION_LEFT_TAB = "position_left_tab";
    public static final String POSITION_FILE = "position_file";
    public static final String POSITION_LIST = "position_list";
    public static final String TOTAL = "total";

    public static final String RANK = "rank";
    public static final String TAB_TWO_POSITION = "tab_two_position";
    public static final String FLAG = "flag";
    public static final String LISTS = "lists";

    public static final String POSITION_ONE_TAB = "position_one_tab";
    public static final String POSITION_TWO_TAB = "position_two_tab";
    public static final String LAYOUT_TYPE = "layout_type";
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String ROOT_PATH = "root_path";
    public static final String SESSION = "session";
    public static final String SESSION_LOCAL = "session_local";
    public static final String IP = "ip";
    public static final String UID = "uid";
    public static final String GID = "gid";
    public static final String SIZE = "size";
    public static final String TIME = "time";
    public static final String FTYPE = "ftype";
    public static final String PERM = "perm";
    public static final String SHARE_PATH_TYPE = "share_path_type";
    public static final String PATH_PIC_POSTER = "path_pic_poster";
    public static final String PATH_PIC_FANART = "path_pic_fanart";
    public static final String MAP = "map";
    public static final String GENRE = "genre";
    public static final String COUNTRY = "country";
    public static final String ACTOR = "actor";

    public static final String MOVIE_POSTER_WALL = "movie_poster_wall";
    public static final String MOVIE_POSTER_LOGO = "movie_poster_logo";
    public static final String MOVIE_POSTER_BG = "movie_poster_bg";
    public static final String MOVIE_POSTER_COVER= "movie_poster_cover";
    public static final String UPDATE_TIME= "update_time";
    public static final String UPDATE_TIME_IN= "update_time_in";
    public static final String HAS_POSTER= "has_poster";

    public static final String TABS_GRADE_TYPE_ONE = "type_one";
    public static final String TABS_GRADE_TYPE_TWO = "type_two";
    public static final String TABS_GRADE_TYPE_POSTER_DATA = "type_poster_data";
    public static final String TABS_GRADE_TYPE_POSTER_INFO = "type_poster_info";
    public static final String TABS_GRADE_TICKET = "type_ticket";
    public static final String TABLE_TYPE_TABS = "type_tabs";
    public static final String TABLE_TYPE_FILES = "type_files";
    public static final String TABLE_TYPE_FILES_LIST_FILE = "type_files_list_file";
    public static final String TABLE_TYPE_FILES_LIST_FANART = "type_files_list_fanart";
    public static final String TABLE_TYPE_FILES_LIST_TRAILER = "type_files_list_trailer";
    public static final String TABLE_TYPE_FILES_LIST_SAMPLE = "type_files_list_sample";
    public static final String TABLE_TYPE_FILES_ALL = "type_files_all";
    public static final String TABLE_TYPE_FILES_ALL_LIST_FILE = "type_files_all_list_file";
    public static final String TABLE_TYPE_FILES_ALL_LIST_FANART = "type_files_all_list_fanart";
    public static final String TABLE_TYPE_FILES_ALL_LIST_TRAILER = "type_files_all_list_trailer";
    public static final String TABLE_TYPE_FILES_ALL_LIST_SAMPLE = "type_files_all_list_sample";

    private SQLiteDatabase db;

    public MySQLiteOpenHelper(Context context){
        this(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_FILE + "(" +
//                "_id integer primary key autoincrement, " +
                DEVICE_ID + " text, "+
                TABS_GRADE + " text, " +
                TABLE + " text, " +
                POSITION_TOP_TAB + " int, " +
                POSITION_LEFT_TAB + " int, " +
                POSITION_FILE + " int, " +
                POSITION_LIST + " int, " +
                RANK + " int, " +
                TOTAL + " int, " +

                TAB_TWO_POSITION + " int, " +
                FLAG + " text, " +
                LISTS + " text, " +

                POSITION_ONE_TAB + " int, " +
                POSITION_TWO_TAB + " int, " +
                LAYOUT_TYPE + " int, " +
                TYPE + " text, " +
                ID + " text, " +
                NAME + " text, " +
                PATH + " text, " +
                ROOT_PATH + " text, " +
                SESSION + " text, " +
                SESSION_LOCAL + " text, " +
                IP + " text, " +
                UID + " int, " +
                GID + " int, " +
                SIZE + " long, " +
                TIME + " int, " +
                FTYPE + " text, " +
                PERM + " text, " +
                SHARE_PATH_TYPE + " int, " +
                PATH_PIC_POSTER + " text, " +
                PATH_PIC_FANART + " text, " +
                GENRE + " text, " +
                COUNTRY + " text, " +
                ACTOR + " text, " +
                MAP + " text, " +

                MOVIE_POSTER_WALL + " text, " +
                MOVIE_POSTER_LOGO + " text, " +
                MOVIE_POSTER_BG + " text, " +
                MOVIE_POSTER_COVER + " text, " +
                UPDATE_TIME_IN + " text, " +
                UPDATE_TIME + " text, " +
                HAS_POSTER + " int, " +

                DATA + " text" +
                ")");
    }

    private ContentValues setContentValues(
            String device_id,
            String tabs_grade,
            String table,
            int positionTab,
            int positionLeft,
            int positionFile,
            int positionList,
            int total,
            Files files
    ) {
        ContentValues values = new ContentValues();
        values.put(DEVICE_ID, device_id);
        values.put(TABS_GRADE, tabs_grade);
        values.put(TABLE, table);
        values.put(POSITION_TOP_TAB, positionTab);
        values.put(POSITION_LEFT_TAB, positionLeft);
        values.put(POSITION_FILE, positionFile);
        values.put(POSITION_LIST, positionList);
        values.put(TOTAL, total);
        values.put(POSITION_ONE_TAB, files.getPositionOneTab());
        values.put(POSITION_TWO_TAB, files.getPositionTwoTab());
        values.put(LAYOUT_TYPE, files.getLayoutType());
        values.put(TYPE, files.getType());
        values.put(ID, files.getId());
        values.put(NAME, files.getName());
        values.put(PATH, files.getPath());
        values.put(ROOT_PATH, files.getRootPath());
        values.put(SESSION, files.getSession());
        values.put(SESSION_LOCAL, files.getSessionLocal());
        values.put(IP, files.getIp());
        values.put(UID, files.getUid());
        values.put(GID, files.getGid());
        values.put(SIZE, files.getSize());
        values.put(TIME, files.getTime());
        values.put(FTYPE, files.getFtype());
        values.put(PERM, files.getPerm());
        values.put(SHARE_PATH_TYPE, files.getShare_path_type());
        values.put(PATH_PIC_POSTER, files.getPath_pic_poster());
        values.put(PATH_PIC_FANART, files.getPath_pic_fanart());
        if(!files.getMap().isEmpty()){
            JSONObject jsonObject = new JSONObject(files.getMap());
            values.put(MAP, jsonObject.toString());
        }
        if(!files.getGenre().isEmpty()){
            JSONArray jsonArray = new JSONArray(files.getGenre());
            values.put(GENRE, jsonArray.toString());
        }
        if(!files.getCountry().isEmpty()){
            JSONArray jsonArray = new JSONArray(files.getCountry());
            values.put(COUNTRY, jsonArray.toString());
        }
        if(!files.getActor().isEmpty()){
            JSONArray jsonArray = new JSONArray(files.getActor());
            values.put(ACTOR, jsonArray.toString());
        }
        return values;
    }

    private Files getFile(
            int position_one_tab, int position_two_tab, int layout_type, String type,
            String id, String name, String path, String root_path, String session,
            String session_local, String ip, int uid, int gid, long size, int time,
            String ftype, String perm, int share_path_type, String path_pic_poster,
            String path_pic_fanart, String map,
            String genre, String country, String actor,
            /*String fileList, String fanartList, String trailerList, String sampleList,*/
            String device_id){
        Files files = new Files();
        files.setDeviceId(device_id);
        files.setPositionOneTab(position_one_tab);
        files.setPositionTwoTab(position_two_tab);
        files.setLayoutType(layout_type);
        if(type != null) files.setType(type);
        if(id != null) files.setId(id);
        if(name != null) files.setName(name);
        if(path != null) files.setPath(path);
        if(root_path != null) files.setRootPath(root_path);
        if(session != null) files.setSession(session);
        if(session_local != null) files.setSessionLocal(session_local);
        if(ip != null) files.setIp(ip);
        files.setUid(uid);
        files.setGid(gid);
        files.setSize(size);
        files.setTime(time);
        if(ftype != null) files.setFtype(ftype);
        if(perm != null) files.setPerm(perm);
        files.setShare_path_type(share_path_type);
        if(path_pic_poster != null) files.setPath_pic_poster(path_pic_poster);
        if(path_pic_fanart != null) files.setPath_pic_fanart(path_pic_fanart);
        if(map != null && !map.isEmpty()){
            HashMap hashMap = new HashMap<String, String>();
            hashMap = new Gson().fromJson(map,hashMap.getClass());
            files.setMap(hashMap);
        }
        if(genre != null && !genre.isEmpty()){
            ArrayList arrayList = new ArrayList<String>();
            arrayList = new Gson().fromJson(genre,arrayList.getClass());
            files.setGenre(arrayList);
        }
        if(country != null && !country.isEmpty()){
            ArrayList arrayList = new ArrayList<String>();
            arrayList = new Gson().fromJson(country,arrayList.getClass());
            files.setCountry(arrayList);
        }
        if(actor != null && !actor.isEmpty()){
            ArrayList arrayList = new ArrayList<String>();
            arrayList = new Gson().fromJson(actor,arrayList.getClass());
            files.setActor(arrayList);
        }
        return files;
    }

    /**
     * 检测是否已经跟新，每日跟新一次
     * @param device_id 设备id
     * @return 0::无需更新 / 1::有跟新 / 2::必须跟新（首次获取数据）
     */
    public int queryUpdateState(String device_id, String updateTime){
        if(db==null || !db.isOpen()){
            db = getReadableDatabase();
        }
        String sql = "select * from " + TABLE_FILE +
                " where " + DEVICE_ID + " = " + "\'" + device_id + "\'" +
                " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_POSTER_DATA + "\'";
        Cursor cursor = db.rawQuery(sql,null);
        String update_time_in = "";
        if (cursor != null && cursor.moveToNext()){
            update_time_in = cursor.getString(cursor.getColumnIndex(UPDATE_TIME_IN));
        }
        cursor.close();
        db.close();
        if(update_time_in == null || update_time_in.isEmpty()) {
            return 2;
        }
        return 1;
//        return update_time_in.equals(updateTime)?0:1;
    }

    /*public DeviceInfoModel queryDeviceInfo(String device_id){
        if(db==null || !db.isOpen()){
            db = getReadableDatabase();
        }
        String sql = "select * from " + TABLE_FILE +
                " where " + DEVICE_ID + " = " + "\'" + device_id + "\'" +
                " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_POSTER_INFO + "\'";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor != null && cursor.moveToNext()){
            String update_time = cursor.getString(cursor.getColumnIndex(UPDATE_TIME));
            if(getUpdate().equals(update_time)){
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                String plot = cursor.getString(cursor.getColumnIndex(DATA));
                String update_time_in = cursor.getString(cursor.getColumnIndex(UPDATE_TIME_IN));
                String type = cursor.getString(cursor.getColumnIndex(TYPE));
                String bg = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_BG));
                String cover = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_COVER));
                String logo = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_LOGO));
                int hasPoster = cursor.getInt(cursor.getColumnIndex(HAS_POSTER));
                DeviceInfoModel info = new DeviceInfoModel(name,plot,update_time_in,type,bg,cover,logo,hasPoster==0);
                cursor.close();
                return info;
            }
        }
        cursor.close();
        db.close();
        return null;
    }*/

    /*public void updateDeviceInfo(String device_id,
                             DeviceInfoModel info){
        synchronized (this){
            if(db == null || !db.isOpen()){
                db = getWritableDatabase();
            }
            String deleteSql = "delete from " + TABLE_FILE +
                    " where " + DEVICE_ID + " = " + "\'" + device_id + "\'" +
                    " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_POSTER_INFO + "\'";
            db.execSQL(deleteSql,new String[]{});
            // 创建ContentValues对象
            ContentValues values = new ContentValues();

            values.put(DEVICE_ID, device_id);
            values.put(TABS_GRADE, TABS_GRADE_TYPE_POSTER_INFO);
            values.put(NAME, info.getName());
            values.put(DATA, info.getPlot());
            values.put(UPDATE_TIME_IN, info.getUpdateTime());
            values.put(TYPE, info.getType());
            values.put(MOVIE_POSTER_BG, info.getMovie_poster_bg());
            values.put(MOVIE_POSTER_COVER, info.getMovie_poster_bg());
            values.put(MOVIE_POSTER_LOGO, info.getMovie_poster_logo());
            values.put(HAS_POSTER, info.getHasPoster()?0:1);
            values.put(UPDATE_TIME, getUpdate());
            db.insert(TABLE_FILE,null,values);
            db.close();
        }
    }*/

    /**
     * 获取海报墙数据
     * @param device_id
     * @return
     */
    public PosterTabDataModel queryPoster(String device_id, String ip, String sessionLocal, String session){
        if(db==null || !db.isOpen()){
            db = getReadableDatabase();
        }
        PosterTabDataModel tabDataModel = new PosterTabDataModel();
        ArrayList<PosterTabModel> tabs = new ArrayList<>();
        String sql = "select * from " + TABLE_FILE +
                " where " + DEVICE_ID + " = " + "\'" + device_id + "\'";
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor != null && cursor.moveToNext()){
            String tabs_grade = cursor.getString(cursor.getColumnIndex(TABS_GRADE));
            String table = cursor.getString(cursor.getColumnIndex(TABLE));
            int positionTab = cursor.getInt(cursor.getColumnIndex(POSITION_TOP_TAB));
            int positionLeft = cursor.getInt(cursor.getColumnIndex(POSITION_LEFT_TAB));
            int positionFile = cursor.getInt(cursor.getColumnIndex(POSITION_FILE));
            int positionList = cursor.getInt(cursor.getColumnIndex(POSITION_LIST));
            int total = cursor.getInt(cursor.getColumnIndex(TOTAL));
            int rank = cursor.getInt(cursor.getColumnIndex(RANK));
            int tab_two_position = cursor.getInt(cursor.getColumnIndex(TAB_TWO_POSITION));
            String flag = cursor.getString(cursor.getColumnIndex(FLAG));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String lists = cursor.getString(cursor.getColumnIndex(LISTS));
            int position_one_tab = cursor.getInt(cursor.getColumnIndex(POSITION_ONE_TAB));
            int position_two_tab = cursor.getInt(cursor.getColumnIndex(POSITION_TWO_TAB));
            int layout_type = cursor.getInt(cursor.getColumnIndex(LAYOUT_TYPE));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String id = cursor.getString(cursor.getColumnIndex(ID));
            String path = cursor.getString(cursor.getColumnIndex(PATH));
            String root_path = cursor.getString(cursor.getColumnIndex(ROOT_PATH));
//            String session = cursor.getString(cursor.getColumnIndex(SESSION));
//            String sessionLocal = cursor.getString(cursor.getColumnIndex(SESSION_LOCAL));
//            String ip = cursor.getString(cursor.getColumnIndex(IP));
            int uid = cursor.getInt(cursor.getColumnIndex(UID));
            int gid = cursor.getInt(cursor.getColumnIndex(GID));
            long size = cursor.getLong(cursor.getColumnIndex(SIZE));
            int time = cursor.getInt(cursor.getColumnIndex(TIME));
            String ftype = cursor.getString(cursor.getColumnIndex(FTYPE));
            String perm = cursor.getString(cursor.getColumnIndex(PERM));
            int share_path_type = cursor.getInt(cursor.getColumnIndex(SHARE_PATH_TYPE));
            String path_pic_poster = cursor.getString(cursor.getColumnIndex(PATH_PIC_POSTER));
            String path_pic_fanart = cursor.getString(cursor.getColumnIndex(PATH_PIC_FANART));
            String map = cursor.getString(cursor.getColumnIndex(MAP));
            String genre = cursor.getString(cursor.getColumnIndex(GENRE));
            String country = cursor.getString(cursor.getColumnIndex(COUNTRY));
            String actor = cursor.getString(cursor.getColumnIndex(ACTOR));
            String movie_poster_wall = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_WALL));
            String movie_poster_logo = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_LOGO));
            if(tabs_grade.equals(TABS_GRADE_TYPE_POSTER_DATA)){
                tabDataModel.setImgWallPath(movie_poster_wall);
                tabDataModel.setImgLogoPath(movie_poster_logo);
            }
//            /*else if(tabs_grade.equals(TABS_GRADE_TYPE_UPDATE_TIME_IN)){
//                tabDataModel.setUpdateTime(data);
//            }*/
            //tabsOne
            else if(tabs_grade.equals(TABS_GRADE_TYPE_ONE)){
                if(table.equals(TABLE_TYPE_TABS)){
                    //tabsOne
                    PosterTabModel tabModel = new PosterTabModel();
                    tabModel.setTabTwoPosition(tab_two_position);
                    tabModel.setRank(rank);
                    if(flag != null) tabModel.setFlag(flag);
                    if(lists != null) tabModel.setLists(lists);
                    if(name != null) tabModel.setName(name);
                    tabs.add(tabModel);
                }
                else if(table.equals(TABLE_TYPE_FILES)){
                    //tabsOne files
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFiles().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_FILE)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFiles().get(positionFile).getFileList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_FANART)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFiles().get(positionFile).getFanartList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_TRAILER)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFiles().get(positionFile).getTrailerList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_SAMPLE)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFiles().get(positionFile).getSampleList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL)){
                    //tabsOne filesAll
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFilesAll().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL_LIST_FILE)){
                    //tabsOne fileList
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFilesAll().get(positionFile).getFileList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL_LIST_FANART)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFilesAll().get(positionFile).getFanartList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL_LIST_TRAILER)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFilesAll().get(positionFile).getTrailerList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL_LIST_SAMPLE)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getFilesAll().get(positionFile).getSampleList().add(file);
                }

            }
            //tabsTwo
            else if(tabs_grade.equals(TABS_GRADE_TYPE_TWO)){
                if(table.equals(TABLE_TYPE_TABS)){
                    //tabsTwo
                    PosterTabModel tabModel = new PosterTabModel();
                    tabModel.setTabTwoPosition(tab_two_position);
                    tabModel.setRank(rank);
                    if(flag != null) tabModel.setFlag(flag);
                    if(lists != null) tabModel.setLists(lists);
                    if(name != null) tabModel.setName(name);
                    tabs.get(positionTab).getTabTwo().add(tabModel);
                }
                else if(table.equals(TABLE_TYPE_FILES)){
                    //tabsTwo files
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFiles().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_FILE)){
                    //tabsTwo files
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFiles().get(positionFile).getFileList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_FANART)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFiles().get(positionFile).getFanartList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_TRAILER)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFiles().get(positionFile).getTrailerList().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_LIST_SAMPLE)){
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, sessionLocal, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFiles().get(positionFile).getSampleList().add(file);
                }
                /*else if(table.equals(TABLE_TYPE_FILES_ALL)){
                    //tabsTwo filesAll
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, session_local, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFilesAll().add(file);
                }
                else if(table.equals(TABLE_TYPE_FILES_ALL_LIST_FILE)){
                    //tabsTwo files
                    Files file = getFile(position_one_tab, position_two_tab, layout_type, type,
                            id, name, path, root_path, session, session_local, ip, uid, gid,
                            size, time, ftype, perm, share_path_type, path_pic_poster,
                            path_pic_fanart, map,genre,country,actor,
                            device_id);
                    tabs.get(positionTab).getTabTwo().get(positionLeft).getFilesAll().get(positionFile).getFileList().add(file);
                }*/
            }
        }
        cursor.close();
        db.close();
        tabDataModel.setTabs(tabs);

        return tabDataModel;
    }

    public void updatePoster(String device_id,
                             ArrayList<PosterTabModel> tabs,
                             String bgImgPath,
                             String logoImgPath,
                             String updateTime){
        synchronized (this){
            Log.d("MySQLiteOpenHelper","update");
            ArrayList<ContentValues> list = new ArrayList<>();

            if(tabs.isEmpty()) return ;

            //tabsOne files list
            for (int positionTab = 0 ; positionTab < tabs.size() ; positionTab++){
                ContentValues values = new ContentValues();
                values.put(DEVICE_ID, device_id);
                values.put(TABS_GRADE, TABS_GRADE_TYPE_ONE);
                values.put(TABLE, TABLE_TYPE_TABS);
                values.put(TOTAL, tabs.size());
                values.put(TAB_TWO_POSITION, tabs.get(positionTab).getTabTwoPosition());
                values.put(RANK, tabs.get(positionTab).getRank());
                values.put(FLAG, tabs.get(positionTab).getFlag());
                values.put(LISTS, tabs.get(positionTab).getLists());
                values.put(NAME, tabs.get(positionTab).getName());
                list.add(values);
                //tabsOne files list
                ArrayList<Files> filesListOne = tabs.get(positionTab).getFiles();
                for (int positionFile = 0 ; positionFile < filesListOne.size() ; positionFile++){
                    values = setContentValues(
                            device_id,
                            TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES,
                            positionTab,0,positionFile,0,filesListOne.size(),filesListOne.get(positionFile)
                    );
                    list.add(values);
                    //filesList
                    ArrayList<Files> filesList = tabs.get(positionTab).getFiles().get(positionFile).getFileList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++){
                        values = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_LIST_FILE,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(values);
                    }
                    filesList = tabs.get(positionTab).getFiles().get(positionFile).getFanartList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++){
                        values = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_LIST_FANART,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(values);
                    }
                    filesList = tabs.get(positionTab).getFiles().get(positionFile).getTrailerList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++){
                        values = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_LIST_TRAILER,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(values);
                    }
                    filesList = tabs.get(positionTab).getFiles().get(positionFile).getSampleList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++){
                        values = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_LIST_SAMPLE,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(values);
                    }
                }
                //filesAll
                filesListOne = tabs.get(positionTab).getFilesAll();
                for (int positionFile = 0 ; positionFile < filesListOne.size() ; positionFile++){
                    ContentValues value1 = setContentValues(
                            device_id,
                            TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_ALL,
                            positionTab,0,positionFile,0,filesListOne.size(),filesListOne.get(positionFile)
                    );
                    list.add(value1);
                    //filesList
                    ArrayList<Files> filesList = tabs.get(positionTab).getFilesAll().get(positionFile).getFileList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                    {
                        ContentValues value2 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_ALL_LIST_FILE,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(value2);
                    }
                    filesList = tabs.get(positionTab).getFilesAll().get(positionFile).getFanartList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                    {
                        ContentValues value2 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_ALL_LIST_FANART,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(value2);
                    }
                    filesList = tabs.get(positionTab).getFilesAll().get(positionFile).getTrailerList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                    {
                        ContentValues value2 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_ALL_LIST_TRAILER,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(value2);
                    }
                    filesList = tabs.get(positionTab).getFilesAll().get(positionFile).getSampleList();
                    for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                    {
                        ContentValues value2 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_ONE,TABLE_TYPE_FILES_ALL_LIST_SAMPLE,
                                positionTab,0,positionFile,positionList,filesList.size(),filesList.get(positionList)
                        );
                        list.add(value2);
                    }
                }

                //tabsTwo files list
                for (int positionLeft = 0 ; positionLeft < tabs.get(positionTab).getTabTwo().size() ; positionLeft++){
                    //tabs
                    ContentValues values3 = new ContentValues();
                    values3.put(DEVICE_ID, device_id);
                    values3.put(TABS_GRADE, TABS_GRADE_TYPE_TWO);
                    values3.put(TABLE, TABLE_TYPE_TABS);
                    values3.put(POSITION_TOP_TAB, positionTab);
                    values3.put(POSITION_LEFT_TAB, positionLeft);
                    values3.put(RANK, tabs.get(positionTab).getTabTwo().get(positionLeft).getRank());
                    values3.put(FLAG, tabs.get(positionTab).getTabTwo().get(positionLeft).getFlag());
                    values3.put(LISTS, tabs.get(positionTab).getTabTwo().get(positionLeft).getLists());
                    values3.put(NAME, tabs.get(positionTab).getTabTwo().get(positionLeft).getName());
                    list.add(values3);
                    //files
                    ArrayList<Files> filesListTwo =
                            tabs.get(positionTab).getTabTwo().get(positionLeft)
                                    .getFiles();
                    for (int positionFile = 0 ; positionFile < filesListTwo.size() ; positionFile++){
                        ContentValues values1 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES,
                                positionTab,positionLeft,positionFile,0,
                                filesListTwo.size(),filesListTwo.get(positionFile)
                        );
                        list.add(values1);
                        //filesList
                        ArrayList<Files> filesList =
                                tabs.get(positionTab).getTabTwo().get(positionLeft)
                                        .getFiles().get(positionFile).getFileList();
                        for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                        {
                            ContentValues values2 = setContentValues(
                                    device_id,
                                    TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_LIST_FILE,
                                    positionTab,positionLeft,positionFile,positionList,
                                    filesList.size(),filesList.get(positionList)
                            );
                            list.add(values2);
                        }
                        filesList =
                                tabs.get(positionTab).getTabTwo().get(positionLeft)
                                        .getFiles().get(positionFile).getFanartList();
                        for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                        {
                            ContentValues values2 = setContentValues(
                                    device_id,
                                    TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_LIST_FANART,
                                    positionTab,positionLeft,positionFile,positionList,
                                    filesList.size(),filesList.get(positionList)
                            );
                            list.add(values2);
                        }
                        filesList =
                                tabs.get(positionTab).getTabTwo().get(positionLeft)
                                        .getFiles().get(positionFile).getTrailerList();
                        for (int positionList = 0 ; positionList < filesList.size() ; positionList++)
                        {
                            ContentValues values2 = setContentValues(
                                    device_id,
                                    TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_LIST_TRAILER,
                                    positionTab,positionLeft,positionFile,positionList,
                                    filesList.size(),filesList.get(positionList)
                            );
                            list.add(values2);
                        }
                        filesList =
                                tabs.get(positionTab).getTabTwo().get(positionLeft)
                                        .getFiles().get(positionFile).getSampleList();
                        for (int positionList = 0 ; positionList < filesList.size() ; positionFile++)
                        {
                            ContentValues values2 = setContentValues(
                                    device_id,
                                    TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_LIST_SAMPLE,
                                    positionTab,positionLeft,positionFile,positionList,
                                    filesList.size(),filesList.get(positionList)
                            );
                            list.add(values2);
                        }
                    }
                    //filesAll
                    /*filesListOne = tabs.get(positionTab).getTabTwo().get(positionLeft)
                            .getFilesAll();
                    for (int positionFile = 0 ; positionFile < filesListOne.size() ; positionFile++)
                    {
                        ContentValues values1 = setContentValues(
                                device_id,
                                TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_ALL,
                                positionTab,positionLeft,positionFile,0,
                                filesListOne.size(),filesListOne.get(positionFile)
                        );
                        list.add(values1);
                        //filesList
                        ArrayList<Files> filesList =
                                tabs.get(positionTab).getTabTwo().get(positionLeft)
                                        .getFilesAll().get(positionFile).getFileList();
                        for (int positionList = 0 ; positionFile < filesList.size() ; positionFile++)
                        {
                            ContentValues values2 = setContentValues(
                                    device_id,
                                    TABS_GRADE_TYPE_TWO,TABLE_TYPE_FILES_ALL_LIST_FILE,
                                    positionTab,positionLeft,positionFile,positionList,
                                    filesList.size(),filesList.get(positionList)
                            );
                            list.add(values2);
                        }
                    }*/
                }
            }
            //update time in
            ContentValues values = new ContentValues();
            values.put(DEVICE_ID, device_id);
            values.put(TABS_GRADE, TABS_GRADE_TYPE_POSTER_DATA);
            values.put(MOVIE_POSTER_WALL, bgImgPath);
            values.put(MOVIE_POSTER_LOGO, logoImgPath);
            values.put(UPDATE_TIME_IN, updateTime);
            values.put(UPDATE_TIME, getUpdate());
            list.add(values);
            Log.d("MySQLiteOpenHelper","delete");
            try {
                if(db == null || !db.isOpen()){
                    db = getWritableDatabase();
                }
                String deleteSql = "delete from " + TABLE_FILE +
                        " where " + DEVICE_ID + " = " + "\'" + device_id + "\'"/* +
                        " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_ONE + "\'"+
                        " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_TWO + "\'"+
                        " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TYPE_POSTER_DATA + "\'"*/;
                db.execSQL(deleteSql,new String[]{});
                db.beginTransaction();
                for(ContentValues value : list){
                    if(db.insert(TABLE_FILE,null,value) < 0){
                        Log.d("MySQLiteOpenHelper"," < 0");
                        return;
                    }
                }
                db.setTransactionSuccessful();
                Log.d("MySQLiteOpenHelper","update 结束");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
                tabs.clear();
                tabs = null;
            }
        }
    }

    /**
     *
     * @param ticket 资源下载列表ticket 种子下载列表dl_ticket
     * @return 资源下载列表ticket对应的名称 种子下载列表dl_ticket对应的网络id
     */
    public String queryTicket(String ticket){
        if(db==null || !db.isOpen()){
            db = getReadableDatabase();
        }
        String sql = "select * from " + TABLE_FILE +
                " where " + DEVICE_ID + " = " + "\'" + ticket + "\'" +
                " and " + TABS_GRADE + " = " + "\'" + TABS_GRADE_TICKET + "\'";
        Cursor cursor = db.rawQuery(sql,null);
        String res = "";
        if (cursor != null && cursor.moveToNext()){
            res = cursor.getString(cursor.getColumnIndex(DATA));
        }
        cursor.close();
        db.close();
        return res;
    }

    public void updateTicket(String ticket,
                       String data){
        synchronized (this){
            if(db == null || !db.isOpen()){
                db = getWritableDatabase();
            }
            String deleteSql = "delete from " + TABLE_FILE +
                    " where " + DEVICE_ID + " = " + "\'" + ticket + "\'";
            db.execSQL(deleteSql,new String[]{});
            // 创建ContentValues对象
            ContentValues values = new ContentValues();

            // background img path
            values.put(DEVICE_ID, ticket);
            values.put(TABS_GRADE, TABS_GRADE_TICKET);
            values.put(DATA, data);
            db.insert(TABLE_FILE,null,values);
            db.close();
        }
    }

    private String getUpdate(){
        Calendar calendar = Calendar.getInstance();
        String created = calendar.get(Calendar.YEAR) + "年"
                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
                + calendar.get(Calendar.DAY_OF_MONTH) + "日";
        return created;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
