package com.example.gateway;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

@Component
public class FilterBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        replace(listableBeanFactory, "routingFilter", CustomNettyRoutingFilter.class);
    }

    private static void replace(DefaultListableBeanFactory listableBeanFactory, String name, Class<?> beanClass) {
        listableBeanFactory.removeBeanDefinition(name);
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        listableBeanFactory.registerBeanDefinition(name, beanDefinition);
    }
}
