package newenergy.db.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by HUST Corey on 2019-04-02.
 * 逻辑操作的模板类
 * 可以直接继承该类实现：添加记录，逻辑修改，逻辑删除
 * T为domain包里的Entity实体类类型
 *
 * 限制：
 * 主键为Integer类型
 * 实体类中包括主键id和安全属性
 * 实体类的字段名为：id, safeChangedTime, safeChangedUserid, safeDelete, safeParent, 具有对应的set & get方法
 *
 * 参考 DeviceRequireService
 */

public class LogicOperation<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 添加记录
     * @param record record中不含id
     * @param userid 操作者用户id
     * @param repository 实体类T对应的JpaRepository
     * @return  添加后的记录
     */
    protected T addRecord(T record, Integer userid, JpaRepository<T,Integer> repository){
        setSafeChangedTime(LocalDateTime.now(),record);
        setSafeChangedUserid(userid,record);
        setSafeDelete(0,record);
        setSafeParent(null,record);
        return repository.save(record);
    }

    /**
     * 逻辑修改
     * @param record record中需要包括id
     * @param userid 操作者用户id
     * @param repository 实体类T对应的JpaRepository
     * @return 修改后的新记录
     */
    protected T updateRecord(T record, Integer userid, JpaRepository<T,Integer> repository){
        Integer id = getId(record);
        if(Objects.isNull(id))
            return null;
        T origin = repository.findById(id).orElse(null);
        if(Objects.isNull(origin))
            return null;
        setSafeDelete(1,origin);

        setSafeDelete(0,record);
        setSafeChangedTime(LocalDateTime.now(),record);
        setSafeChangedUserid(userid,record);
        setSafeParent(id,record);
        setId(null, record);
        return repository.save(record);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     * @param repository 实体类T对应的JpaRepository
     */
    protected void deleteRecord(Integer id, Integer userid, JpaRepository<T,Integer> repository){
        T record = repository.findById(id).orElse(null);
        if(!Objects.isNull(record)){
            T copy = (T)getCopy(record);
            if(!Objects.isNull(copy)){
                T newRecord = updateRecord(copy,userid,repository);
                setSafeDelete(1,newRecord);
                repository.save(newRecord);
            }
        }
    }
    private Integer getId(T t) {
        Integer result = null;
        Class<?> klass = t.getClass();
        try {
            Method method = klass.getMethod("getId");
            result = (Integer) method.invoke(t);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
        return result;
    }
    private void setId(Integer id, T t){
        Class<?> klass = t.getClass();
        try {
            Method method = klass.getMethod("setId",Integer.class);
            method.invoke(t,id);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
    }


    private  Object getCopy(Object src){
        Object obj = null;
        if(Objects.isNull(src)) return null;
        Class<?> klass = src.getClass();
        try{
            obj = klass.newInstance();
            Field[] fields = klass.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                field.set(obj,field.get(src));
            }
        }catch (Exception e){
            logger.error("copy failed");
        }
        return obj;
    }

    private LocalDateTime getSafeChangedTime(T t) {
        LocalDateTime changedTime = null;
        Class<?> klass = t.getClass();
        try {
            Method method = klass.getMethod("getSafeChangedTime");
            changedTime = (LocalDateTime)method.invoke(t);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
        return changedTime;
    }

    private void setSafeChangedTime(LocalDateTime safeChangedTime, T t) {
         Class<?> klass = t.getClass();
         try{
             Method method = klass.getMethod("setSafeChangedTime",LocalDateTime.class);
             method.invoke(t,safeChangedTime);
         }catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException e){
             logger.error("method invoke failed");
         }
    }

    private Integer getSafeChangedUserid(T t) {
        Integer result = null;
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("getSafeChangedUserid");
            result = (Integer)method.invoke(t);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
        return result;
    }

    private void setSafeChangedUserid(Integer safeChangedUserid, T t) {
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("setSafeChangedUserid",Integer.class);
            method.invoke(t,safeChangedUserid);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
    }

    private Integer getSafeDelete(T t) {
        Integer result = null;
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("getSafeDelete");
            result = (Integer)method.invoke(t);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
        return result;
    }

    private void setSafeDelete(Integer safeDelete, T t) {
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("setSafeDelete",Integer.class);
            method.invoke(t,safeDelete);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
    }

    private Integer getSafeParent(T t) {
        Integer result = null;
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("getSafeParent");
            result = (Integer)method.invoke(t);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
        return result;
    }

    private void setSafeParent(Integer safeParent, T t) {
        Class<?> klass = t.getClass();
        try{
            Method method = klass.getMethod("setSafeParent",Integer.class);
            method.invoke(t,safeParent);
        }catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e){
            logger.error("method invoke failed");
        }
    }
}
