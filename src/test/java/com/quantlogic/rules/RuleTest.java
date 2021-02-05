package com.quantlogic.rules;

import com.quantlogic.codegen.EntityRuleBuilder;
import com.quantlogic.entity.*;
import com.quantlogic.rule.grammar.RuleVisitor;
import com.quantlogic.util.ReflectionUtils;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RuleTest {

    @Test
    public void testGenerateMethodCallStmt() throws Exception {
        String s = "com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)";
        ReflectionUtils utils =  ReflectionUtils.INSTANCE;
        Optional<String> s1 = utils.generateMethodCallStatement(s.replaceAll("\"",""));
        Assert.assertTrue(s1.isPresent());
        Assert.assertEquals("com.quantlogic.util.VolKeygenUtil.getFlatVolKey(String.valueOf(\"QQQ\"),String.valueOf(\"20201202\"),String.valueOf(\".SPX\"))", s1.get());

    }

    @Test
    public void testGenerateEqualsStmt() {
        String s = "$tickerSymbol == \"OTC123\"";
        ReflectionUtils utils =  ReflectionUtils.INSTANCE;
        String s1 = utils.generateEqualsStatement(VanillaOption.class, "vanillaOpton", s.replaceAll("\"", ""), true);
        Assert.assertEquals("vanillaOpton.getTickerSymbol().equals(String.valueOf(\"OTC123\"))", s1);
     }

    /*@Test
    public void testGenerateEqualsStmt() {
        String s = "$tickerSymbol == \"OTC123\"";
        ReflectionUtils utils =  ReflectionUtils.INSTANCE;
        String s1 = utils.generateEqualsStatement(VanillaOption.class, "vanillaOpton", s.replaceAll("\"", ""), true);
        Assert.assertEquals("vanillaOpton.getTickerSymbol().equals(String.valueOf(\"OTC123\"))", s1);
    }*/

     @Test
     public void testAutogenRules() throws Exception{
         String s = "(getSource().getVolatility().equals(Double.valueOf(\"0.36\")) || getSource().getTickerSymbol().equals(String.valueOf(\"OTC123\")) )  && getSource().getUnderlying().equals(String.valueOf(\"QQQ\")) ";
         String s1 = "com.quantlogic.util.VolKeygenUtil.getFlatVolKey(String.valueOf(\"QQQ\"),String.valueOf(\"20201202\"),String.valueOf(\".SPX\"))";
         EntityRuleBuilder<Instrument, Volatility> entityRuleBuilder = new EntityRuleBuilder<>(new DefaultInstrumentVolatilityRuleSet(1)) ;
         EntityRule<Instrument, Volatility> er = entityRuleBuilder
                 .withRuleName("Test100")
                 .withRuleId("100")
                 .withRuleWeight("2")
                 .withSourceAndTarget(Pair.of("com.quantlogic.entity.VanillaOption", "instrument"),
                 Pair.of("com.quantlogic.entity.BlackVarianceVolatilitySurface", "volatility")).withPredicate(s, s1).build();

         entityRuleBuilder.writeFile();
         Assert.assertNotNull(er);
     }

     @Test
     public void testRuleVisitor() throws IOException {
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

         EntityRuleBuilder<VanillaOption, BlackVarianceVolatilitySurface> entityRuleBuilder = new EntityRuleBuilder<>(entityRuleSet);
         RuleVisitor<VanillaOption, BlackVarianceVolatilitySurface> ruleVisitor = new RuleVisitor<>(entityRuleSet, VanillaOption.class, BlackVarianceVolatilitySurface.class);


        // ANTLRInputStream antlrInputStream = new ANTLRInputStream("volQQQ_1_1 = ($underlying == \"QQQ\" && ($tickerSymbol == \"OTC123\" || $volatility == 0.36)) ? com.quantlogic.util.VolKeygenUtil.getFlatVolKey(\"QQQ\", 20201202, .SPX)");
         com.quantlogic.rules.QuantlogicLexer lexer = new com.quantlogic.rules.QuantlogicLexer(new ANTLRFileStream("src/main/resources/rules/rulestore.quantlogic"));
         com.quantlogic.rules.QuantlogicParser parser = new com.quantlogic.rules.QuantlogicParser(new CommonTokenStream(lexer));
         ParseTree tree = parser.parse();
         ruleVisitor.visit(tree);
     }
}
