package Model;

/**
 * Created by Maayan on 04-Jan-18.
 */

public interface IEvent {
    public int Open();
    public int Close();
    public int Share();
    public int Edit();
    public int AddNewUser();
    public int sync();
}
