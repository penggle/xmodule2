package com.penglecode.xmodule.common.security.support;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.penglecode.xmodule.common.consts.GlobalConstants;
import com.penglecode.xmodule.common.security.consts.SecurityApplicationConstants;
import com.penglecode.xmodule.common.util.CollectionUtils;

/**
 * 多应用RoleVoter
 * 
 * 拷贝自#RoleVoter，仅重写了extractAuthorities方法
 * 
 * @author 	pengpeng
 * @date	2019年5月23日 下午1:33:32
 */
public class MultiAppRoleVoter implements AccessDecisionVoter<Object> {
	// ~ Instance fields
	// ================================================================================================

	private String rolePrefix = SecurityApplicationConstants.DEFAULT_ROLE_VOTE_PREFIX;

	// ~ Methods
	// ========================================================================================================

	public String getRolePrefix() {
		return rolePrefix;
	}

	/**
	 * Allows the default role prefix of <code>ROLE_</code> to be overridden. May be set
	 * to an empty value, although this is usually not desirable.
	 *
	 * @param rolePrefix the new prefix
	 */
	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	public boolean supports(ConfigAttribute attribute) {
		if ((attribute.getAttribute() != null)
				&& attribute.getAttribute().startsWith(getRolePrefix())) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * This implementation supports any type of class, because it does not query the
	 * presented secure object.
	 *
	 * @param clazz the secure object
	 *
	 * @return always <code>true</code>
	 */
	public boolean supports(Class<?> clazz) {
		return true;
	}

	public int vote(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) {
		if (authentication == null) {
			return ACCESS_DENIED;
		}
		int result = ACCESS_ABSTAIN;
		Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);

		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				result = ACCESS_DENIED;

				// Attempt to find a matching granted authority
				for (GrantedAuthority authority : authorities) {
					if (attribute.getAttribute().equals(authority.getAuthority())) {
						return ACCESS_GRANTED;
					}
				}
			}
		}

		return result;
	}

	/**
	 * 重写获取当前应用下用户拥有的角色列表
	 * @param authentication
	 * @return
	 */
	protected Collection<? extends GrantedAuthority> extractAuthorities(
			Authentication authentication) {
		//TODO
		//Assert.isInstanceOf(OAuth2Authentication.class, authentication, String.format("Expect the current authentication is typeof %s", OAuth2Authentication.class));
		
		Collection<GrantedAuthority> userAuthorities = new LinkedHashSet<>();
		Collection<? extends GrantedAuthority> allUserAuthorities = authentication.getAuthorities();
		if(!CollectionUtils.isEmpty(allUserAuthorities)) {
			for(GrantedAuthority authority : allUserAuthorities) {
				Assert.isTrue(authority instanceof MultiAppGrantedAuthority, String.format("The type of user's GrantedAuthority must be %s", MultiAppGrantedAuthority.class));
				if(GlobalConstants.MVVM_APP_CONFIG.getCurrent().getAppId().equals(((MultiAppGrantedAuthority) authority).getApplication())) {
					userAuthorities.add(authority);
				}
			}
		}
		return userAuthorities;
	}
}