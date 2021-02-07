package com.quantlogic.rule.grammar;

import com.quantlogic.codegen.EntityRuleBuilder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.rules.EntityRuleSet;
import com.quantlogic.rules.QuantlogicBaseVisitor;
import com.quantlogic.rules.QuantlogicParser;
import com.quantlogic.util.ReflectionUtils;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.Stack;

public class RuleVisitor<T extends Entity, U extends Entity> extends QuantlogicBaseVisitor<EntityRuleBuilder<T, U>> {
    private final Class<T> srcCls;
    private final Class<U> targetCls;
    private final EntityRuleSet<T, U> entityRuleSet;
    private EntityRuleBuilder<T, U> entityRuleBuilder;

    private String ternOp;
    private final Stack<Operation> opstack = new Stack<>();
    private final Stack<String> statementStack = new Stack<>();

    public RuleVisitor(EntityRuleSet<T, U> entityRuleSet, Class<T> srcCls, Class<U> targetCls) {
        this.entityRuleSet = entityRuleSet;
        this.srcCls = srcCls;
        this.targetCls = targetCls;
    }

    @Override
    public EntityRuleBuilder<T, U> visitStat(QuantlogicParser.StatContext ctx) {
        if(!opstack.empty() || !statementStack.empty()){
            while (!opstack.empty()){
                String op = opstack.pop().op;
                String first = statementStack.pop();
                String second = statementStack.pop();
                String val = "(" + first + " "+op +  " " + second + " ) ";
                statementStack.push(val);
            }

            String predicate = statementStack.pop();
            try {
                Optional<String> function = ReflectionUtils.INSTANCE.generateMethodCallStatement(ternOp.replaceAll("\"",""));
                if(function.isPresent()) {
                    entityRuleBuilder.withSourceAndTarget(Pair.of(srcCls.getCanonicalName(), "src"), Pair.of(targetCls.getCanonicalName(), "target"));
                    entityRuleBuilder.withPredicate(predicate, function.get());
                    EntityRule<T, U> build = entityRuleBuilder.build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        entityRuleBuilder = new EntityRuleBuilder<>(this.entityRuleSet);
        return super.visitStat(ctx);
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
    public EntityRuleBuilder<T, U> visitAssignment(QuantlogicParser.AssignmentContext ctx) {
        String[] strings = ctx.ID().getText().split("_");
        if(strings.length == 3) {
            try {
                this.entityRuleBuilder.withRuleName(strings[0]);
                this.entityRuleBuilder.withRuleId(strings[1]);
                this.entityRuleBuilder.withRuleWeight(strings[2]);
            } catch (NotFoundException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return super.visitAssignment(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAltoperator(QuantlogicParser.AltoperatorContext ctx) {
        return super.visitAltoperator(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitEqualityExpr(QuantlogicParser.EqualityExprContext ctx) {
        String stmt = ctx.getStart().getText() + " == " + ctx.getStop().getText();
        String str = ReflectionUtils.INSTANCE.generateEqualsStatement(srcCls, "getSource()", stmt, true);
        statementStack.push(str);
        return super.visitEqualityExpr(ctx);
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
    public EntityRuleBuilder<T, U> visitOrSorExpr(QuantlogicParser.OrSorExprContext ctx) {
        opstack.push(Operation.OR);
        return super.visitOrSorExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitAndSandExpr(QuantlogicParser.AndSandExprContext ctx) {
        opstack.push(Operation.AND);
        return super.visitAndSandExpr(ctx);
    }

    public enum Operation{
        AND("&&"), OR("||"), SAND("&&&"), SOR("|||");
        String op;
        Operation(String op) {
            this.op = op;
        }
    }
}
