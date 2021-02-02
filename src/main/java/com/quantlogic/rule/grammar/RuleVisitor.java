package com.quantlogic.rule.grammar;

import com.quantlogic.codegen.EntityRuleBuilder;
import com.quantlogic.rules.QuantlogicBaseVisitor;
import com.quantlogic.rules.QuantlogicParser;

public class RuleVisitor extends QuantlogicBaseVisitor<EntityRuleBuilder> {
    @Override
    public EntityRuleBuilder visitBooleanAtom(QuantlogicParser.BooleanAtomContext ctx) {
        return super.visitBooleanAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitIdAtom(QuantlogicParser.IdAtomContext ctx) {
        return super.visitIdAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitStat(QuantlogicParser.StatContext ctx) {
        return super.visitStat(ctx);
    }

    @Override
    public EntityRuleBuilder visitAltoperator(QuantlogicParser.AltoperatorContext ctx) {
        return super.visitAltoperator(ctx);
    }

    @Override
    public EntityRuleBuilder visitAssignment(QuantlogicParser.AssignmentContext ctx) {
        return super.visitAssignment(ctx);
    }

    @Override
    public EntityRuleBuilder visitTernaryoperator(QuantlogicParser.TernaryoperatorContext ctx) {
        return super.visitTernaryoperator(ctx);
    }

    @Override
    public EntityRuleBuilder visitFunctionAtom(QuantlogicParser.FunctionAtomContext ctx) {
        return super.visitFunctionAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitOrExpr(QuantlogicParser.OrExprContext ctx) {
        return super.visitOrExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitNilAtom(QuantlogicParser.NilAtomContext ctx) {
        return super.visitNilAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitParse(QuantlogicParser.ParseContext ctx) {
        return super.visitParse(ctx);
    }

    @Override
    public EntityRuleBuilder visitAdditiveExpr(QuantlogicParser.AdditiveExprContext ctx) {
        return super.visitAdditiveExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitRelationalExpr(QuantlogicParser.RelationalExprContext ctx) {
        return super.visitRelationalExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitNumberAtom(QuantlogicParser.NumberAtomContext ctx) {
        return super.visitNumberAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitParExpr(QuantlogicParser.ParExprContext ctx) {
        return super.visitParExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitNotExpr(QuantlogicParser.NotExprContext ctx) {
        return super.visitNotExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitUnaryMinusExpr(QuantlogicParser.UnaryMinusExprContext ctx) {
        return super.visitUnaryMinusExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitElementExpr(QuantlogicParser.ElementExprContext ctx) {
        return super.visitElementExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitStringAtom(QuantlogicParser.StringAtomContext ctx) {
        return super.visitStringAtom(ctx);
    }

    @Override
    public EntityRuleBuilder visitMultiplicationExpr(QuantlogicParser.MultiplicationExprContext ctx) {
        return super.visitMultiplicationExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitBlock(QuantlogicParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public EntityRuleBuilder visitEqualityExpr(QuantlogicParser.EqualityExprContext ctx) {
        return super.visitEqualityExpr(ctx);
    }

    @Override
    public EntityRuleBuilder visitAndExpr(QuantlogicParser.AndExprContext ctx) {
        return super.visitAndExpr(ctx);
    }
}
