package Model;

import java.io.Serializable;

/**
 * Created by Maayan on 04-Jan-18.
 */

public class Event implements IEvent,Serializable{
    private static final long serialVersionUID=1L;
    private int _id;
    public String _nameEvent;
    public String _date;
    public String _participates;
    public String _conversation;
    public String _descp;

    public String get_descp() {
        return _descp;
    }

    public void set_descp(String _descp) {
        this._descp = _descp;
    }

    public Event(int _id, String _nameEvent, String _date, String _conversation, String _participates, String descp) {
        this._id = _id;
        this._nameEvent = _nameEvent;
        this._date = _date;
        this._conversation = _conversation;
        this._participates = _participates;

        _descp=descp;
    }

    public String get_participates() {
        return _participates;
    }

    public void set_participates(String _participates) {
        this._participates = _participates;
    }

    public String get_nameEvent() {

        return _nameEvent;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_nameEvent(String _nameEvent) {
        this._nameEvent = _nameEvent;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void set_conversation(String _conversation) {
        this._conversation = _conversation;
    }

    public String get_date() {
        return _date;
    }

    public String get_conversation() {
        return _conversation;
    }

    public int get_id() {

        return _id;
    }

    @Override
    public int Open() {
        return 0;
    }

    @Override
    public int Close() {
        return 0;
    }

    @Override
    public int Share() {
        return 0;
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id=" + _id +
                ", _nameEvent='" + _nameEvent + '\'' +
                ", _date='" + _date + '\'' +
                ", _participates='" + _participates + '\'' +
                ", _conversation='" + _conversation + '\'' +
                ", _descp='" + _descp + '\'' +
                '}';
    }

    @Override
    public int Edit() {
        return 0;
    }

    @Override
    public int AddNewUser() {
        return 0;
    }

    @Override
    public int sync() {
        return 0;
    }
}
