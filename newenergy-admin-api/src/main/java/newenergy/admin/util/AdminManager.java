package newenergy.admin.util;

import newenergy.db.domain.NewenergyAdmin;

public class AdminManager {

    private static NewenergyAdmin admin = null;

    public static NewenergyAdmin getAdmin(){
        return admin;
    }

    public static void setAdmin(NewenergyAdmin oldAdmin){
        admin = oldAdmin;
    }

}
