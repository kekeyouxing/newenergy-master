package newenergy.wx.template;

/**
 * Created by HUST Corey on 2019-04-22.
 */
public class Ret<T> {
    T value;
    int code;
    public T get(){
        return value;
    }
    public void set(T t){
        value = t;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public boolean ok(){
        return code==0;
    }
}
