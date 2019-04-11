package newenergy.db.template;

import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-04-11.
 */
public class PredicateFactory {
    static class SafePredicate{
        private LocalDateTime safeChangedTime;
        private Integer safeChangedUserid;
        private Integer safeDelete;
        private Integer safeParent;

        public LocalDateTime getSafeChangedTime() {
            return safeChangedTime;
        }

        public void setSafeChangedTime(LocalDateTime safeChangedTime) {
            this.safeChangedTime = safeChangedTime;
        }

        public Integer getSafeChangedUserid() {
            return safeChangedUserid;
        }

        public void setSafeChangedUserid(Integer safeChangedUserid) {
            this.safeChangedUserid = safeChangedUserid;
        }

        public Integer getSafeDelete() {
            return safeDelete;
        }

        public void setSafeDelete(Integer safeDelete) {
            this.safeDelete = safeDelete;
        }

        public Integer getSafeParent() {
            return safeParent;
        }

        public void setSafeParent(Integer safeParent) {
            this.safeParent = safeParent;
        }
    }
    public static SafePredicate getAlivePredicate(){
        SafePredicate safePredicate = new SafePredicate();
        safePredicate.setSafeDelete(0);
        return safePredicate;
    }
}
