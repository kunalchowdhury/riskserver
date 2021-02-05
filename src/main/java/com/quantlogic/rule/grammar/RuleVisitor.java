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

    private boolean visit = false;
    private String ternOp;
    private Stack<Operation> opstack = new Stack<>();
    private Stack<String> statementStack = new Stack<>();
    private StringBuilder sb ;
    private ParseTree.ParseNode node ;
    private ParseTree.ParseNode curNode;

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
            System.out.println(predicate);
            try {
                Optional<String> function = ReflectionUtils.INSTANCE.generateMethodCallStatement(ternOp.replaceAll("\"",""));
                if(function.isPresent()) {
                    entityRuleBuilder.withSourceAndTarget(Pair.of(srcCls.getCanonicalName(), "src"), Pair.of(targetCls.getCanonicalName(), "target"));
                    entityRuleBuilder.withPredicate(predicate, function.get());
                    EntityRule<T, U> build = entityRuleBuilder.build();
                    System.out.println(build);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //  ParseTree.ParseNode.inOrder(node);
          //  entityRuleBuilder.build();
        }
        entityRuleBuilder = new EntityRuleBuilder<>(this.entityRuleSet);
        sb = new StringBuilder();
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
        /*String curStr = sb.toString();
        boolean endsWithOp = Arrays.stream(Operation.values()).anyMatch(s -> curStr.endsWith(s.op));
        boolean stackNotEmpty = !opstack.empty();
        String stmt = ctx.getStart().getText() + " == " + ctx.getStop().getText();
        System.out.println("Equality Operation " + stmt);
        if(stackNotEmpty){
            this.sb.append("(");
        }
        this.sb.append("(").append(str).append(")");
        if(stackNotEmpty){
            this.sb.append(opstack.pop().op);
        }else if(endsWithOp){
            this.sb.append(")");
        }else {
            this.sb.append(")");
        }
        System.out.println(" -- > "+sb);
        return super.visitEqualityExpr(ctx);*/

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

    /*@Override
    public EntityRuleBuilder<T, U> visitOrExpr(QuantlogicParser.OrExprContext ctx) {
        addNode(Operation.OR.op);
        return super.visitOrExpr(ctx);
    }*/

    /*@Override
    public EntityRuleBuilder<T, U> visitSandExpr(QuantlogicParser.SandExprContext ctx) {
        addNode(Operation.SAND.op);
        return super.visitSandExpr(ctx);
    }

    @Override
    public EntityRuleBuilder<T, U> visitSorExpr(QuantlogicParser.SorExprContext ctx) {
        addNode(Operation.SOR.op);
        return super.visitSorExpr(ctx);
    }
*/
    /*@Override
    public EntityRuleBuilder<T, U> visitAndExpr(QuantlogicParser.AndExprContext ctx) {
        addNode(Operation.AND.op);
        return super.visitAndExpr(ctx);
    }*/

    @Override
    public EntityRuleBuilder<T, U> visitAndSandExpr(QuantlogicParser.AndSandExprContext ctx) {
        opstack.push(Operation.AND);
        return super.visitAndSandExpr(ctx);
    }

    private void addNode(String s){
        if(node == null){
            node = new ParseTree.ParseNode(s);
            this.curNode = node;
        }else if(curNode.left == null){
            curNode.left = new ParseTree.ParseNode(s);
        }else if(curNode.right == null){
            curNode.right = new ParseTree.ParseNode(s);
            curNode = curNode.right;
        }
    }

    public enum Operation{
        AND("&&"), OR("||"), SAND("&&&"), SOR("|||");
        String op;
        Operation(String op) {
            this.op = op;
        }
    }
}
