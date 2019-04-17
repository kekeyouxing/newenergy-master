package newenergy.admin.annotation;

//import newenergy.admin.util.AdminManager;
import newenergy.admin.util.AdminManager;
import newenergy.db.domain.NewenergyAdmin;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AdminLoginUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(NewenergyAdmin.class) && parameter.hasParameterAnnotation(AdminLoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                  NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
        return AdminManager.getAdmin();
    }
}
