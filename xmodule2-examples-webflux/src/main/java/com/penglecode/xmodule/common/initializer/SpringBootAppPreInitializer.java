package com.penglecode.xmodule.common.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.penglecode.xmodule.common.util.SpringUtils;

/**
 * Spring应用启动之初始化程序
 * 该类配置方式如下：
 * 1、 在web.xml中以contextInitializerClasses上下文参数配置
 * 		<context-param>
 * 			<param-name>contextInitializerClasses</param-name>
 * 			<param-value>your.package.SpringAppPreBootingInitializer</param-value>
 * 		</context-param>
 * 
 * 2、 在Springboot的application.properties中配置
 * 		context.initializer.classes=your.package.SpringAppPreBootingInitializer
 * 
 * @author 	pengpeng
 * @date	2019年9月21日 下午16:40:31
 */
public class SpringBootAppPreInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootAppPreInitializer.class);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		LOGGER.info(">>> Spring上下文初始化程序, applicationContext = {}", applicationContext);
		SpringUtils.setApplicationContext(applicationContext);
	}

}
