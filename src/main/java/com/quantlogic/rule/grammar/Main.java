package com.quantlogic.rule.grammar;


import com.quantlogic.rules.QuantlogicLexer;
import com.quantlogic.rules.QuantlogicParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[]{"src/main/resources/rules/rulestore.quantlogic"};
        }

        System.out.println("parsing: " + args[0]);

        QuantlogicLexer lexer = new QuantlogicLexer(new ANTLRFileStream(args[0]));
        QuantlogicParser parser = new QuantlogicParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        TestAntlr visitor = new TestAntlr();
        visitor.visit(tree);
    }

}