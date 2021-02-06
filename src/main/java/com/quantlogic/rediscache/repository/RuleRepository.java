package com.quantlogic.rediscache.repository;

import java.util.Collection;

public interface RuleRepository {

    void saveRule(String ruleSet, String rule);
    void deleteRule(String ruleSet);
    Collection<String> getRuleSet(String ruleSet);


}
