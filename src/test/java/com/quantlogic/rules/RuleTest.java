package com.quantlogic.rules;

import com.quantlogic.entity.VanillaOption;
import com.quantlogic.util.ReflectionUtils;
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
    public void testGenerateEqualsStmt() throws Exception {
        String s = "$tickerSymbol == \"OTC123\"";
        ReflectionUtils utils =  ReflectionUtils.INSTANCE;
        String s1 = utils.generateEqualsStatement(VanillaOption.class, "vanillaOpton", s.replaceAll("\"", ""));
        Assert.assertEquals("vanillaOpton.getTickerSymbol().equals(String.valueOf(\"OTC123\"))", s1);
     }

}
