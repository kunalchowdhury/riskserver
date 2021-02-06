package com.quantlogic.riskserver;

import com.quantlogic.entity.BlackVarianceVolatilitySurface;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.VanillaOption;
import com.quantlogic.rediscache.repository.RuleRepository;
import com.quantlogic.rule.grammar.RuleVisitor;
import com.quantlogic.rules.EntityRuleSet;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@SpringBootTest
class RiskserverApplicationTests {


    @Autowired
    private RuleRepository ruleRepository;

    @BeforeEach
    public void setup(){
        this.ruleRepository.saveRule("RULESET1", "volQQQ_1_1 = ($underlying == \"QQQ\" && ($tickerSymbol == \"OTC123\" || $volatility == 0.36)) ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
        this.ruleRepository.saveRule("RULESET1", "volSPX_1_2 = ($tickerSymbol == \"QQQ\") ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
        this.ruleRepository.saveRule("RULESET1", "volAAPL_2_1 = ($tickerSymbol == \"AAPL\") ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
    }


    @Test
    void contextLoads() {
        EntityRuleSet<VanillaOption, BlackVarianceVolatilitySurface> entityRuleSet = new EntityRuleSet<VanillaOption, BlackVarianceVolatilitySurface>() {
            private Set<EntityRule<VanillaOption, BlackVarianceVolatilitySurface>> set = new HashSet<>();
            @Override
            public int ruleSetId() {
                return 1;
            }

            @Override
            public void addEntityRule(EntityRule<VanillaOption, BlackVarianceVolatilitySurface> entityRule) {
                set.add(entityRule);
            }

            @Override
            public Collection<EntityRule<VanillaOption, BlackVarianceVolatilitySurface>> get() {
                return set;
            }
        };

        RuleVisitor<VanillaOption, BlackVarianceVolatilitySurface> ruleVisitor = new RuleVisitor<>(entityRuleSet, VanillaOption.class, BlackVarianceVolatilitySurface.class);

        ruleRepository.getRuleSet("RULESET1").forEach(s -> {
            com.quantlogic.rules.QuantlogicLexer lexer = new com.quantlogic.rules.QuantlogicLexer(new ANTLRInputStream(s));
            com.quantlogic.rules.QuantlogicParser parser = new com.quantlogic.rules.QuantlogicParser(new CommonTokenStream(lexer));
            ParseTree tree = parser.parse();
            ruleVisitor.visit(tree);
        });

        Assertions.assertEquals(3, entityRuleSet.get().size());
    }

    @AfterEach
    public void destroy() {
       this.ruleRepository.deleteRule("RULESET1");
    }



}
