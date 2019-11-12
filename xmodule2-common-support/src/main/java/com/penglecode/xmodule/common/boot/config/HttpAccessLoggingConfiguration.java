package com.penglecode.xmodule.common.boot.config;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.penglecode.xmodule.common.web.filter.AbstractHttpAccessLoggingFilter;
import com.penglecode.xmodule.common.web.support.HttpAccessLogDAO;

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnBean({HttpAccessLogDAO.class, AbstractHttpAccessLoggingFilter.class})
public class HttpAccessLoggingConfiguration extends AbstractSpringConfiguration {
	
	/**
	 * 手动SpringMVC/Jersey等Http访问日志记录过滤器
	 */
	@Bean
	public FilterRegistrationBean<Filter> httpAccessLoggingFilterRegistration(AbstractHttpAccessLoggingFilter httpAccessLoggingFilter) {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
	    filterRegistrationBean.setFilter(httpAccessLoggingFilter);
	    filterRegistrationBean.setEnabled(true);
	    filterRegistrationBean.setName("httpAccessLoggingFilter");
	    filterRegistrationBean.addUrlPatterns("/*");
	    filterRegistrationBean.setOrder(FilterRegistrationOrder.ORDER_HTTP_ACCESS_LOGGING_FILTER);
	    return filterRegistrationBean;
	}
	
}
