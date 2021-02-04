package com.quantlogic.rules;

import com.quantlogic.codegen.EntityRuleBuilder;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.Instrument;
import com.quantlogic.entity.VanillaOption;
import com.quantlogic.entity.Volatility;
import com.quantlogic.util.ReflectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

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
        String s1 = utils.generateEqualsStatement(VanillaOption.class, "vanillaOpton", s.replaceAll("\"", ""));
        Assert.assertEquals("vanillaOpton.getTickerSymbol().equals(String.valueOf(\"OTC123\"))", s1);
     }

     @Test
     public void testAutogenRules() throws Exception{
         String s = "getSource().getTickerSymbol().equals(String.valueOf(\"OTC123\"))";
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
}
