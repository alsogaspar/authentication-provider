package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import eu.europa.ec.simpl.client.feign.DaggerFeignSimplClientFactory;
import eu.europa.ec.simpl.client.feign.FeignSimplClient;
import eu.europa.ec.simpl.client.okhttp.DaggerOkHttpSimplClientFactory;
import eu.europa.ec.simpl.client.okhttp.OkHttpSimplClient;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class MtlsConfig implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope("mtls", mtlsScope());
    }

    @Bean
    public MtlsScope mtlsScope() {
        return new MtlsScope();
    }

    @Bean
    @Scope(value = "mtls", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AuthorityExchange authorityClient(MtlsClientBuilder mtlsClientBuilder) {
        return mtlsClientBuilder.buildAuthorityClient();
    }

    @Bean
    public FeignSimplClient feignSimplClient() {
        return DaggerFeignSimplClientFactory.create().get();
    }

    @Bean
    public OkHttpSimplClient okHttpSimplClient() {
        return DaggerOkHttpSimplClientFactory.create().get();
    }
}
