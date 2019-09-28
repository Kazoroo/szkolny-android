package pl.szczodrzynski.edziennik.ui.modules.login;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import pl.szczodrzynski.edziennik.data.db.modules.login.LoginStore;
import pl.szczodrzynski.edziennik.data.db.modules.profiles.Profile;

public class LoginProfileObject {
    LoginStore loginStore = null;
    List<Profile> profileList = new ArrayList<>();
    List<Boolean> selectedList = new ArrayList<>();

    public LoginProfileObject(@NonNull LoginStore loginStore, @NonNull List<Profile> profileList) {
        this.loginStore = loginStore;
        this.profileList = profileList;
        for (Profile ignored : profileList) {
            selectedList.add(true);
        }
    }

    public LoginProfileObject addProfile(Profile profile) {
        profileList.add(profile);
        selectedList.add(true);
        return this;
    }
}
