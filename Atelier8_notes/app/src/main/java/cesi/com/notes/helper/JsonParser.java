package cesi.com.notes.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cesi.com.notes.model.Note;
import cesi.com.notes.model.User;

/**
 * Created by sca on 03/06/15.
 */
public class JsonParser {

    public static List<Note> getNotes(String json) throws JSONException {
        List<Note> notes = new LinkedList<Note>();
        JSONArray array = new JSONArray(json);
        JSONObject obj;
        Note msg;
        for(int i=0; i < array.length(); i++){
            obj = array.getJSONObject(i);
            msg = new Note(obj.optString("id"), obj.optString("username"), obj.optString("note"),
                    obj.optLong("date"), obj.optBoolean("done", false));
           notes.add(msg);
        }

        return notes;
    }

    public static String getToken(String response) throws JSONException {
        return new JSONObject(response).optString("token");
    }

    public static List<User> getUsers(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        List<User> users = new LinkedList<User>();
        JSONObject obj;
        User u;
        for(int i=0; i<array.length(); i++){
            obj = array.getJSONObject(i);
            u = new User(obj.optString("username"), obj.optLong("date"),
                     obj.optString("urlPhoto"));
            users.add(u);
        }
        return users;
    }


}















