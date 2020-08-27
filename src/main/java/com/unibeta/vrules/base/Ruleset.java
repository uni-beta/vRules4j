package com.unibeta.vrules.base;

import java.util.List;

public class Ruleset {

    private String id;
    private List<Rule> rules;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public List<Rule> getRules() {

        return rules;
    }

    public void setRules(List<Rule> rules) {

        this.rules = rules;
    }

}
