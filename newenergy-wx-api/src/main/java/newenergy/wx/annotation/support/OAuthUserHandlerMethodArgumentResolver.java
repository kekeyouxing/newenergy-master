package newenergy.wx.annotation.support;

import newenergy.wx.annotation.OAuthUser;
import newenergy.wx.product.manager.UserTokenManager;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 验证token，获取openId
 * @author yangq
 * @date 2019-04-18
 */
public class OAuthUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String OAuth_TOKEN_KEY = "X-NewEnergy-Token";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(String.class) && methodParameter.hasParameterAnnotation(OAuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        String token = nativeWebRequest.getHeader(OAuth_TOKEN_KEY);
        if (token == null || token.isEmpty()){
            return null;
        }
        String openid = UserTokenManager.getOpenId(token);
        return UserTokenManager.getOpenId(token);
    }
}
