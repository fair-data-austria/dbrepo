package at.tuwien.reflection;

import at.tuwien.exception.FileStorageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@Log4j2
public class ReflectContext {

    public static void register(ApplicationContext context, String code) throws ClassNotFoundException,
            FileStorageException {
        register(context, null, code);
    }

    public static void register(ApplicationContext context, String beanName, String code) throws ClassNotFoundException,
            FileStorageException {
        final Class<?> clazz = ReflectClassLoader.load("at.tuwien.userdb.Table", code);
        if (beanName == null) {
            beanName = clazz.getName();
        }
        final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context;
        final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        if (beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.removeBeanDefinition(beanName);
        }
        final BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        beanFactory.registerBeanDefinition(beanName, definitionBuilder.getRawBeanDefinition());
        log.debug("registered bean {}", beanName);
    }

    public static <T> T getBean(ApplicationContext context, String beanName) {
        return (T) ((ConfigurableApplicationContext) context).getBeanFactory()
                .getBean(beanName);
    }

}
