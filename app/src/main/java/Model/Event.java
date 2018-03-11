package Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Maayan on 04-Jan-18.
 */

public class Event implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String content;
    private String title;
    private String date;
    private List<Integer> usersIds;
    private String description;
    private int adminId;

    public Event(String content, String title, List<Integer> usersIds, String description, int adminId) {
        this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Math.abs((this.adminId + this.date).hashCode());

    }

    public int getAdminId() {
        return adminId;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content.concat("\n" + content);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
