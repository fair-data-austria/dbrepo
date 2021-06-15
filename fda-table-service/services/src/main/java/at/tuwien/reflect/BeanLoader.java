package at.tuwien.reflect;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class BeanLoader<T> {

    private final ApplicationContext applicationContext;

    @Autowired
    public BeanLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void registerBean(String name, Object instance) {
        final ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        if (beanFactory.containsBeanDefinition(name)) {
            beanFactory.removeBeanDefinition(name);
        }
        final BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(instance.getClass());
        beanFactory.registerBeanDefinition(name, definitionBuilder.getRawBeanDefinition());
        log.debug("registered bean {}", name);

    }

    public <S> S getBean(ApplicationContext context, String beanName) {
        return (S) ((ConfigurableApplicationContext) context).getBeanFactory()
                .getBean(beanName);
    }


}
