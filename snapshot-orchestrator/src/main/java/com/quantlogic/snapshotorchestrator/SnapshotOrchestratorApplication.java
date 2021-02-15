package com.quantlogic.snapshotorchestrator;

import com.quantlogic.repository.MemoryIndexRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
public class SnapshotOrchestratorApplication {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    private final MemoryIndexRepositoryImpl memoryIndexRepository;

    public SnapshotOrchestratorApplication(@Autowired MemoryIndexRepositoryImpl memoryIndexRepository) {
        this.memoryIndexRepository = memoryIndexRepository;
    }

    public static void main(String[] args) {
       // SpringApplication.run(SnapshotOrchestratorApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(SnapshotOrchestratorApplication.class, args);
        SnapshotOrchestratorApplication bean = context.getBean(SnapshotOrchestratorApplication.class);
        bean.memoryIndexRepository.unmapMemoryAddress("VOLKEY1");
        bean.memoryIndexRepository.mapMemoryAddress("VOLKEY1", "8188188");
        bean.memoryIndexRepository.mapMemoryAddress("VOLKEY1", "90901001");
        System.out.println(bean.memoryIndexRepository.getMemoryAddresses("VOLKEY1"));
    }

}
