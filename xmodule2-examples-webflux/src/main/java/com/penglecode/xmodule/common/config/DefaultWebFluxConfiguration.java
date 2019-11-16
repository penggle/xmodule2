package com.penglecode.xmodule.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.penglecode.xmodule.common.support.DefaultWebfluxErrorAttributes;

/**
 * 默认的WebFlux配置，大部分copy自{@link #ErrorWebFluxAutoConfiguration}
 * 
 * @author 	pengpeng
 * @date	2019年6月5日 下午3:37:58
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DefaultWebFluxConfiguration {

	private final ServerProperties serverProperties;

	public DefaultWebFluxConfiguration(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}

	@Bean
	public DefaultErrorAttributes defaultErrorAttributes() {
		return new DefaultWebfluxErrorAttributes(this.serverProperties.getError().isIncludeException());
	}
	
}
