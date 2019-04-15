package newenergy.admin.util;

import newenergy.db.domain.NewenergyAdmin;

public class AdminManager {

    private static NewenergyAdmin admin = null;

    public static Integer getAdminId(){
        return admin.getId();
    }

    public static void setAdmin(NewenergyAdmin oldAdmin){
        admin = oldAdmin;
    }

}
