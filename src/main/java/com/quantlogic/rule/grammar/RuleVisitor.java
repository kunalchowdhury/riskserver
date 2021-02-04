package com.quantlogic.rule.grammar;

import com.quantlogic.codegen.EntityRuleBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.rules.QuantlogicBaseVisitor;
import com.quantlogic.rules.QuantlogicParser;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public class RuleVisitor<T extends Entity, U extends Entity> extends QuantlogicBaseVisitor<EntityRuleBuilder<T, U>> {
    private final Class<EntityRuleBuilder> entityRuleBuilderCls;
    private EntityRuleBuilder<T, U> entityRuleBuilder;

    private boolean visit = false;
    private String ternOp;

    public RuleVisitor(Class<EntityRuleBuilder> entityRuleBuilderCls) {
        this.entityRuleBuilderCls = entityRuleBuilderCls;
    }

    @Override
    public EntityRuleBuilder<T, U> visitBooleanAtom(QuantlogicParser.BooleanAtomContext ctx) {
        return super.visitBooleanAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitIdAtom(QuantlogicParser.IdAtomContext ctx) {
        return super.visitIdAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitStat(QuantlogicParser.StatContext ctx) {
        if(!visit){
            visit = true;
            try {
                entityRuleBuilder = entityRuleBuilderCls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }else {
            visit = false;
            entityRuleBuilder.build();
        }
        return super.visitStat(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAltoperator(QuantlogicParser.AltoperatorContext ctx) {
        return super.visitAltoperator(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAssignment(QuantlogicParser.AssignmentContext ctx) {
        String[] strings = ctx.ID().getText().split("_");
        try {
            this.entityRuleBuilder.withRuleName(strings[0]);
            this.entityRuleBuilder.withRuleId(strings[1]);
            this.entityRuleBuilder.withRuleWeight(strings[2]);
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
        return super.visitAssignment(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitTernaryoperator(QuantlogicParser.TernaryoperatorContext ctx) {
        this.ternOp = ctx.getStop().getText();
        return super.visitTernaryoperator(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitFunctionAtom(QuantlogicParser.FunctionAtomContext ctx) {
        return super.visitFunctionAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitOrExpr(QuantlogicParser.OrExprContext ctx) {
        return super.visitOrExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitNilAtom(QuantlogicParser.NilAtomContext ctx) {
        return super.visitNilAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitParse(QuantlogicParser.ParseContext ctx) {
        return super.visitParse(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAdditiveExpr(QuantlogicParser.AdditiveExprContext ctx) {
        return super.visitAdditiveExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitRelationalExpr(QuantlogicParser.RelationalExprContext ctx) {
        return super.visitRelationalExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitNumberAtom(QuantlogicParser.NumberAtomContext ctx) {
        return super.visitNumberAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitParExpr(QuantlogicParser.ParExprContext ctx) {
        return super.visitParExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitNotExpr(QuantlogicParser.NotExprContext ctx) {
        return super.visitNotExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitUnaryMinusExpr(QuantlogicParser.UnaryMinusExprContext ctx) {
        return super.visitUnaryMinusExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitElementExpr(QuantlogicParser.ElementExprContext ctx) {
        return super.visitElementExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitStringAtom(QuantlogicParser.StringAtomContext ctx) {
        return super.visitStringAtom(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitMultiplicationExpr(QuantlogicParser.MultiplicationExprContext ctx) {
        return super.visitMultiplicationExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitBlock(QuantlogicParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitEqualityExpr(QuantlogicParser.EqualityExprContext ctx) {
        return super.visitEqualityExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAndExpr(QuantlogicParser.AndExprContext ctx) {
        return super.visitAndExpr(ctx);
    }
}
