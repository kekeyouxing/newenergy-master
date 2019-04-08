package newenergy.core.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
	public static Object fail() {
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("errno", -1);
		obj.put("errmsg", "错误");
		return obj;
	}
	public static Object fail(int errno, String errmsg) {
		Map<String, Object> obj = new HashMap<String ,Object>();
		obj.put("errno", errno);
		obj.put("errmsg", errmsg);
		return obj;
	}
	public static Object badArgument() {
		return fail(401, "参数不对");
	}
	
	public static Object ok() {
		Map<String, Object> obj = new HashMap<>();
		obj.put("errno",0);
		obj.put("errmsg", "成功");
		return obj;
	}

	public static Object ok(Object data){
		Map<String, Object> obj = new HashMap<>();
		obj.put("errno", 0);
		obj.put("msg", "成功");
		obj.put("data", data);
		return obj;
	}

	public static Object ok(Map<String, Object> data) {
		Map<String, Object> obj = new HashMap<>();
		obj.put("errno",0);
		obj.put("errmsg", "成功");
		obj.put("data", data);
		return obj;
	}

	public static Object unlogin(){
		return fail(501, "请登录");
	}

	public static Object unauthz(){
		return fail(506,"无操作权限");
	}

	public static Object updatedDataFailed() {
		return fail(505, "更新数据失败");
	}
}
