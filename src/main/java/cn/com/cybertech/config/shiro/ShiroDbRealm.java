package cn.com.cybertech.config.shiro;

import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebPerimissionService;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.EncryptUtils;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ShiroDbRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroDbRealm.class);

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private WebPerimissionService webPerimissionService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        LOGGER.debug("doGetAuthenticationInfo:" + authcToken);
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String[] param = token.getHost().split(":"); //0:公司id  1:平台标识
        WebUser user = webUserService.getWebUserByPhone(token.getUsername(), Integer.valueOf(param[0]));
        if (user == null) {
            return null;
        }
        if (user.getState() == 0) {
            throw new DisabledAccountException();
        }
        String valiedToken = EncryptUtils.MD5Encode(param[1] + user.getPhone() + user.getCompanyId());
        return new SimpleAuthenticationInfo(valiedToken, user.getPassword(), getName());
    }

    public void cleanUserSession(String username) {
        Session sessionCurr = SecurityUtils.getSubject().getSession();
        LOGGER.info("currSession:{}", sessionCurr.getId());
        //apache shiro获取所有在线用户
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            String loginUsername = String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY));//获得session中已经登录用户的名字
            if (username.equals(loginUsername)) {  //这里的username也就是当前登录的username
                if (!session.getId().equals(sessionCurr.getId())) {
                    LOGGER.info("remove session id:{}", session.getId());
                    session.setTimeout(0);  //这里就把session清除，
                }
            }
        }
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LOGGER.info("doGetAuthorizationInfo");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        List<String> roles = new ArrayList<String>();
        roles.add("admin");
        info.addRoles(roles);
        return info;
    }
}