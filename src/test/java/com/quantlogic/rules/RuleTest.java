package com.quantlogic.rules;

import com.quantlogic.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class RuleTest {

    @Test
    public void testGenerateMethodCallStmt() throws Exception {
        String s = "com.quantlogic.util.VolKeygenUtil.getFlatVolKey(QQQ, 20201202, .SPX)";
        ReflectionUtils utils =  ReflectionUtils.INSTANCE;
        Optional<String> s1 = utils.generateMethodCallStatement(s);
        Assert.assertTrue(s1.isPresent());
        Assert.assertEquals("com.quantlogic.util.VolKeygenUtilgetFlatVolKey.(String.valueOf(\"QQQ\"),String.valueOf(\"20201202\"),String.valueOf(\".SPX\"))", s1.get());

    }

}
