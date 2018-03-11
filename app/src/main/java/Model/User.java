package Model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maayan on 10-Jan-18.
 */
//TODO handle with date

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private int id;//hash code from email
    private String joinDate;
    private String phoneNumber;
    private String email;
    private List<Integer> friendsIds;
    private String pictureUrl;
    private final boolean admin = false;


    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId(email);
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.joinDate = dateFormat.format(date);
        this.pictureUrl = null;
        if (friendsIds != null)
            this.friendsIds = friendsIds;
        else
            this.friendsIds = new LinkedList<Integer>();
    }

    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, String pictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId(email);
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.joinDate = dateFormat.format(date);
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        String str = "";
        str += "First Name: " + firstName + "\n";
        str += "Last Name: " + lastName + "\n";
        str += "Email: " + email + "\n";
        str += "Phone: " + phoneNumber + "\n";
        str += "Id: " + id + "\n";
        str += "Date: " + joinDate + "\n";
        if (pictureUrl != null)
            str += "PictureUrl:" + pictureUrl + "\n";
        str += "Friends Count:" + friendsIds.size() + "\n";
        int index = 1;
        for (int id : friendsIds) {
            str += "\t" + index + ") " + id + "\n";
            index++;
        }

        return str;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = generateId(email);
    }

    public String getJoinDate() {
        return joinDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String url) {
        this.pictureUrl = url;
    }

    public List<Integer> getFriendsIds() {
        return friendsIds;
    }

    public void addFriend(int id) {
        friendsIds.add(id);
    }

    public static int generateId(String email) {
        return Math.abs(email.hashCode());
    }
}
