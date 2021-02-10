package com.quantlogic.riskserver;

import com.quantlogic.entity.BlackVarianceVolatilitySurface;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.VanillaOption;
import com.quantlogic.enumtype.DayCount;
import com.quantlogic.enumtype.ExcerciseType;
import com.quantlogic.enumtype.OptionType;
import com.quantlogic.enumtype.USMarketType;
import com.quantlogic.rediscache.repository.RuleRepository;
import com.quantlogic.rule.grammar.RuleVisitor;
import com.quantlogic.rulegraph.JoinNode;
import com.quantlogic.rulegraph.RootNode;
import com.quantlogic.rulegraph.TerminalNode;
import com.quantlogic.rulegraph.TypeNode;
import com.quantlogic.rules.EntityRuleSet;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.quantlogic")
@ConfigurationPropertiesScan("com.quantlogic")
@EnableJpaRepositories(basePackages = "com.quantlogic")
@SpringBootTest
class RiskserverApplicationTests {


    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RootNode<VanillaOption, BlackVarianceVolatilitySurface> rootNode;

    @BeforeEach
    public void setup(){
        this.ruleRepository.saveRule("RULESET1", "volQQQ_1_1 = ($underlying == \"QQQ\" && ($tickerSymbol == \"OTC123\" || $volatility == 0.36)) ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
        this.ruleRepository.saveRule("RULESET1", "volSPX_2_2 = ($tickerSymbol == \"QQQ\") ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
        this.ruleRepository.saveRule("RULESET1", "volAAPL_3_1 = ($tickerSymbol == \"AAPL\") ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)" +
                "END");
    }


    private VanillaOption getVanillaOption(){
        return new VanillaOption(123, "VanillaOptionQQQ",
                34.6, "QQQ",
                0.3, 0.4, 0.36,
                System.currentTimeMillis(), System.currentTimeMillis(), DayCount.ACTUAL_365_FIXED,
                OptionType.CALL, ExcerciseType.AMERICAN, 1,
        "OTC123", System.currentTimeMillis());
    }
    private void zeroAll(java.util.Calendar calendar) {
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
    }
    private void set(java.util.Calendar calendar, int year, int month, int day, Long[] expirations, int idx){
        zeroAll(calendar);
        calendar.set(year, month, day);
        expirations[idx] = calendar.getTime().getTime();
    }
    private BlackVarianceVolatilitySurface getVolSurface(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        zeroAll(calendar);
        calendar.set(15, java.util.Calendar.MAY, 15);
        long valuationDate = calendar.getTime().getTime();

        Long[] expirations = new Long[5];
        set(calendar, 2013, java.util.Calendar.DECEMBER, 20, expirations, 0);
        set(calendar, 2014, java.util.Calendar.JANUARY, 17, expirations, 1);
        set(calendar, 2014, java.util.Calendar.MARCH, 21, expirations, 2);
        set(calendar, 2014, java.util.Calendar.JUNE, 20, expirations, 3);
        set(calendar, 2014, java.util.Calendar.SEPTEMBER, 19, expirations, 4);

        Double[][] vols = new Double[][]{
                {0.15640, 0.15433, 0.16079 , 0.16394, 0.17383},
                {0.15343, 0.15240, 0.15804 , 0.16255, 0.17303},
                {0.15128, 0.14888, 0.15512 , 0.15944, 0.17038},
                {0.14798, 0.14906, 0.15522 , 0.16171, 0.16156},
                {0.14580, 0.14576, 0.15364 , 0.16037, 0.16042}
        };

        return new BlackVarianceVolatilitySurface(100,
                "QQQ_Black_Variance", 1,
                valuationDate,
                valuationDate,
                USMarketType.NYSE,
                expirations,
                new Double[]{1650.0, 1660.0, 1670.0, 1675.0, 1680.0},
                DayCount.ACTUAL_365_FIXED,
                vols);
    }

    @Test
    void contextLoads() {
        System.out.println(rootNode);
        EntityRuleSet<VanillaOption, BlackVarianceVolatilitySurface> entityRuleSet = new EntityRuleSet<VanillaOption, BlackVarianceVolatilitySurface>() {
            private final Set<EntityRule<VanillaOption, BlackVarianceVolatilitySurface>> set = new HashSet<>();
            @Override
            public int ruleSetId() {
                return 1;
            }

            @Override
            public void addEntityRule(EntityRule<VanillaOption, BlackVarianceVolatilitySurface> entityRule) {
                rootNode.getListener().onRule(entityRule);
                set.add(entityRule);
            }

            @Override
            public Collection<EntityRule<VanillaOption, BlackVarianceVolatilitySurface>> get() {
                return set;
            }
        };

        RuleVisitor<VanillaOption, BlackVarianceVolatilitySurface> ruleVisitor = new RuleVisitor<>(entityRuleSet, VanillaOption.class, BlackVarianceVolatilitySurface.class);
        VanillaOption vanillaOption = getVanillaOption();
        TypeNode<VanillaOption, BlackVarianceVolatilitySurface> left = rootNode.getTypeNodeTypeNodePair().getLeft();
        TypeNode<VanillaOption, BlackVarianceVolatilitySurface> right = rootNode.getTypeNodeTypeNodePair().getRight();
        JoinNode<VanillaOption, BlackVarianceVolatilitySurface> joinNodeInstrument = new JoinNode<>(rootNode, left, rootNode.getLevel());
        TerminalNode<VanillaOption, BlackVarianceVolatilitySurface> terminalNode = new TerminalNode<VanillaOption, BlackVarianceVolatilitySurface>(right);

        rootNode.exec();
        left.exec();
        right.exec();

        ruleRepository.getRuleSet("RULESET1").forEach(s -> {
            com.quantlogic.rules.QuantlogicLexer lexer = new com.quantlogic.rules.QuantlogicLexer(new ANTLRInputStream(s));
            com.quantlogic.rules.QuantlogicParser parser = new com.quantlogic.rules.QuantlogicParser(new CommonTokenStream(lexer));
            ParseTree tree = parser.parse();
            ruleVisitor.visit(tree);

        });
        Pair<VanillaOption, Integer> retVal = joinNodeInstrument.apply(vanillaOption);
        String key = terminalNode.apply(Pair.of(Pair.of(retVal.getLeft(), getVolSurface()), retVal.getRight()));
        Assertions.assertEquals("FlatVol|QQQ|20201202|.SPX", key);
        Assertions.assertEquals(3, entityRuleSet.get().size());
    }

    @AfterEach
    public void destroy() {
        this.ruleRepository.deleteRule("RULESET1");
    }



}
