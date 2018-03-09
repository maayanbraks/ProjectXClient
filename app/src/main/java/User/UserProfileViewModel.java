package User;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import Model.User;

/**
 * Created by Maayan on 10-Jan-18.
 */

public class UserProfileViewModel extends ViewModel {
    private String userId;
    private LiveData<User> user;
    public void init(String userId) {
        this.userId = userId;
    }
    public LiveData<User> getUser() {
        return user;
    }
}
