package fr.ifremer.nemo.ws.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Properties;

/**
 * Configuration class inherited by the ws_nautilus and the ws_nemo projects
 */
@EnableSwagger2
@EnableWebMvc
public class WebappConfigurationBase extends WebMvcConfigurerAdapter implements ServletContextListener {

    private static ServletContext servletContext;

    private AnnotationConfigWebApplicationContext applicationContext;

    /**
     * When the servlet context gets initialized, create a Spring application context and bind the spring dispatcher servlet
     *
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.scan(getClass().getPackage().getName());
        ServletRegistration.Dynamic servletRegistration = servletContextEvent.getServletContext().addServlet("dispatcher", new DispatcherServlet(applicationContext));
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        applicationContext.close();
    }

    /**
     * These handlers are required by springfox
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    private static final String KEY_JNDI_RESOURCENAME = "jndi.resourcename";

    private static final String KEY_JDBC_DRIVERCLASSNAME = "jdbc.driverclassname";

    private static final String KEY_JDBC_URL = "jdbc.url";

    private static final String KEY_JDBC_USERNAME = "jdbc.username";

    private static final String KEY_JDBC_PASSWORD = "jdbc.password";

    private static final String KEY_JDBC_POOL_MIN = "jdbc.pool.min";

    private static final String KEY_JDBC_POOL_MAX = "jdbc.pool.max";

    @Bean
    DataSource dataSource() throws Exception {

        URL propsUrl = servletContext.getResource("WEB-INF/database.properties");
        Properties props = new Properties();
        props.load(propsUrl.openStream());

        // if we use a JNDI datasource
        if (props.containsKey(KEY_JNDI_RESOURCENAME)) {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            return (DataSource) envCtx.lookup(props.getProperty(KEY_JNDI_RESOURCENAME));
        }

        // config jdbc
        HikariConfig config = new HikariConfig();
        if (props.containsKey(KEY_JDBC_DRIVERCLASSNAME)) {
            config.setDriverClassName(props.getProperty(KEY_JDBC_DRIVERCLASSNAME));
        }
        config.setJdbcUrl(props.getProperty(KEY_JDBC_URL));
        if (props.containsKey(KEY_JDBC_USERNAME)) {
            config.setUsername(props.getProperty(KEY_JDBC_USERNAME));
            config.setPassword(props.getProperty(KEY_JDBC_PASSWORD, ""));
        }
        if (props.containsKey(KEY_JDBC_POOL_MAX)) {
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty(KEY_JDBC_POOL_MAX, "1")));
        }
        if (props.containsKey(KEY_JDBC_POOL_MIN)) {
            config.setMinimumIdle(Integer.parseInt(props.getProperty(KEY_JDBC_POOL_MIN, "0")));
        }
        return ProxyUtil.newLazyInitializingProxy(DataSource.class, () -> new HikariDataSource(config));
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }
}
