package newenergy.wx.annotation.support;

import newenergy.wx.annotation.Recharge;
import newenergy.wx.product.manager.UserTokenManager;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RechargeHandlerMethodArgumenResolver implements HandlerMethodArgumentResolver {
    public static final String OAuth_TOKEN_KEY = "X-NewEnergy-Token";
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(String.class) && methodParameter.hasParameterAnnotation(Recharge.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token = nativeWebRequest.getHeader(OAuth_TOKEN_KEY);
        if (token == null || token.isEmpty()){
            return null;
        }
        return UserTokenManager.getNickname(token);
    }
}
