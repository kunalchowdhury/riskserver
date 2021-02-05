package com.quantlogic.rule.grammar;

import com.quantlogic.rules.QuantlogicBaseVisitor;
import com.quantlogic.rules.QuantlogicParser;

public class TestAntlr extends QuantlogicBaseVisitor<String> {

    @Override
    public String visitBooleanAtom(QuantlogicParser.BooleanAtomContext ctx) {
        System.out.println("1");
        return super.visitBooleanAtom(ctx);
    }

    @Override
    public String visitIdAtom(QuantlogicParser.IdAtomContext ctx) {
        System.out.println("2");
        return super.visitIdAtom(ctx);
    }

    @Override
    public String visitStat(QuantlogicParser.StatContext ctx) {
        System.out.println("STAT");
        return super.visitStat(ctx);
    }

    @Override
    public String visitAltoperator(QuantlogicParser.AltoperatorContext ctx) {
        System.out.println("Alt Operator "+ ctx.getStop().getText());
        return super.visitAltoperator(ctx);
    }

    @Override
    public String visitAssignment(QuantlogicParser.AssignmentContext ctx) {
        System.out.println("Assignment for "+ctx.ID().getText());
        return super.visitAssignment(ctx);
    }

    @Override
    public String visitTernaryoperator(QuantlogicParser.TernaryoperatorContext ctx) {
        System.out.println("Ternary Operator start "+ ctx.getStop().getText());
        return super.visitTernaryoperator(ctx);
    }

    @Override
    public String visitFunctionAtom(QuantlogicParser.FunctionAtomContext ctx) {
        System.out.println("4");
        return super.visitFunctionAtom(ctx);
    }
/*
    @Override
    public String visitOrExpr(QuantlogicParser.OrExprContext ctx) {
        System.out.println(" OR ");
        return super.visitOrExpr(ctx);
    }*/

    @Override
    public String visitNilAtom(QuantlogicParser.NilAtomContext ctx) {
        System.out.println("6");
        return super.visitNilAtom(ctx);
    }

    @Override
    public String visitParse(QuantlogicParser.ParseContext ctx) {
        System.out.println("7");
        return super.visitParse(ctx);
    }

    @Override
    public String visitAdditiveExpr(QuantlogicParser.AdditiveExprContext ctx) {
        System.out.println("8");
        return super.visitAdditiveExpr(ctx);
    }

    @Override
    public String visitRelationalExpr(QuantlogicParser.RelationalExprContext ctx) {
        System.out.println("9");
        return super.visitRelationalExpr(ctx);
    }

    @Override
    public String visitNumberAtom(QuantlogicParser.NumberAtomContext ctx) {
        System.out.println("10");
        return super.visitNumberAtom(ctx);
    }

    @Override
    public String visitParExpr(QuantlogicParser.ParExprContext ctx) {
        System.out.println("11");
        return super.visitParExpr(ctx);
    }

    @Override
    public String visitNotExpr(QuantlogicParser.NotExprContext ctx) {
        System.out.println("12");
        return super.visitNotExpr(ctx);
    }

    @Override
    public String visitUnaryMinusExpr(QuantlogicParser.UnaryMinusExprContext ctx) {
        System.out.println("13");
        return super.visitUnaryMinusExpr(ctx);
    }

    @Override
    public String visitElementExpr(QuantlogicParser.ElementExprContext ctx) {
        System.out.println("14");
        return super.visitElementExpr(ctx);
    }

    @Override
    public String visitStringAtom(QuantlogicParser.StringAtomContext ctx) {
        System.out.println("15");
        return super.visitStringAtom(ctx);
    }

    @Override
    public String visitMultiplicationExpr(QuantlogicParser.MultiplicationExprContext ctx) {
        System.out.println("16");
        return super.visitMultiplicationExpr(ctx);
    }

    @Override
    public String visitBlock(QuantlogicParser.BlockContext ctx) {
        System.out.println("17");
        return super.visitBlock(ctx);
    }

    @Override
    public String visitEqualityExpr(QuantlogicParser.EqualityExprContext ctx) {
        System.out.println("Equality Operation "+ctx.getStart().getText()+" , "+ctx.getStop().getText());
        return super.visitEqualityExpr(ctx);
    }

    @Override
    public String visitOrSorExpr(QuantlogicParser.OrSorExprContext ctx) {
        System.out.println("OP = "+ctx.getChild(1));
        return super.visitOrSorExpr(ctx);
    }

    @Override
    public String visitAndSandExpr(QuantlogicParser.AndSandExprContext ctx) {
        System.out.println("OP = "+ctx.getChild(1));
        return super.visitAndSandExpr(ctx);
    }

    /* @Override
    public String visitAndExpr(QuantlogicParser.AndExprContext ctx) {
        System.out.println("AND");
        return super.visitAndExpr(ctx);
    }
*/


    /*@Override
    public String visitSandExpr(QuantlogicParser.SandExprContext ctx) {
        System.out.println("got SAND");
        return super.visitSandExpr(ctx);
    }

    @Override
    public String visitSorExpr(QuantlogicParser.SorExprContext ctx) {
        System.out.println("got SOR");
        return super.visitSorExpr(ctx);
    }*/
}
