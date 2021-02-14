package com.quantlogic.snapshotorchestrator;

import com.quantlogic.repository.MemoryIndexRepository;
import com.quantlogic.repository.MemoryIndexRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@SpringBootApplication
@Configuration
class SnapshotOrchestratorApplicationTests {

    @Autowired
    private MemoryIndexRepository memoryIndexRepository;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SnapshotOrchestratorApplicationTests.class, args);
        MemoryIndexRepository bean = context.getBean(MemoryIndexRepositoryImpl.class);
        bean.mapMemoryAddress("VOLKEY1", "123");
    }
/*
    @Test
    void contextLoads() {
      memoryIndexRepository.saveRule("VOLKEY1", 123);
    }
*/

}
