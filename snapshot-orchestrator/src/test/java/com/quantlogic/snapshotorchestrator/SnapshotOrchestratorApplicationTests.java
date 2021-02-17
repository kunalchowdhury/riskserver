package com.quantlogic.snapshotorchestrator;

import com.quantlogic.mmap.MemoryMapManager;
import com.quantlogic.mmap.MemoryMapManagerImpl;
import com.quantlogic.repository.MemoryIndexRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collection;

@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@SpringBootTest
@Configuration
class SnapshotOrchestratorApplicationTests {

    @Autowired
    private MemoryIndexRepository memoryIndexRepository;

    @Autowired
    private MemoryMapManager memoryMapManager;

    @Test
    public void memoryMapTest() {
        ConfigurableApplicationContext context = SpringApplication.run(SnapshotOrchestratorApplicationTests.class);
        MemoryMapManagerImpl memoryMapManager = context.getBean(MemoryMapManagerImpl.class);
        Long reserveMemoryEngine1 = memoryMapManager.reserveMemory("Engine-1", 2, 2, 2);
        Assertions.assertNotNull(reserveMemoryEngine1);
        Long reserveMemoryEngine2 = memoryMapManager.reserveMemory("Engine-2", 2, 2, 2);
        Assertions.assertNotNull(reserveMemoryEngine2);
    }

    @Test
    public void memoryIndexRepoTest(){
        ConfigurableApplicationContext context = SpringApplication.run(SnapshotOrchestratorApplicationTests.class);
        SnapshotOrchestratorApplicationTests bean = context.getBean(SnapshotOrchestratorApplicationTests.class);
        bean.memoryIndexRepository.unmapMemoryAddress("VOLKEY1");
        bean.memoryIndexRepository.mapMemoryAddress("VOLKEY1", "8188188");
        bean.memoryIndexRepository.mapMemoryAddress("VOLKEY1", "90901001");
        Collection<Long> keys = bean.memoryIndexRepository.getMemoryAddresses("VOLKEY1");
        Assertions.assertTrue(keys.containsAll(Sets.newSet(90901001L, 8188188L)));
    }
}
