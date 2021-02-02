package mu;
public class TestAntlr extends QuantlogicBaseVisitor<String> {
    @Override
    public String visitBooleanAtom(QuantlogicParser.BooleanAtomContext ctx) {
        return super.visitBooleanAtom(ctx);
    }

    @Override
    public String visitIdAtom(QuantlogicParser.IdAtomContext ctx) {
        return super.visitIdAtom(ctx);
    }

    @Override
    public String visitStat(QuantlogicParser.StatContext ctx) {
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
        return super.visitFunctionAtom(ctx);
    }

    @Override
    public String visitOrExpr(QuantlogicParser.OrExprContext ctx) {
        return super.visitOrExpr(ctx);
    }

    @Override
    public String visitNilAtom(QuantlogicParser.NilAtomContext ctx) {
        return super.visitNilAtom(ctx);
    }

    @Override
    public String visitParse(QuantlogicParser.ParseContext ctx) {
        return super.visitParse(ctx);
    }

    @Override
    public String visitAdditiveExpr(QuantlogicParser.AdditiveExprContext ctx) {
        return super.visitAdditiveExpr(ctx);
    }

    @Override
    public String visitRelationalExpr(QuantlogicParser.RelationalExprContext ctx) {
        return super.visitRelationalExpr(ctx);
    }

    @Override
    public String visitNumberAtom(QuantlogicParser.NumberAtomContext ctx) {
        return super.visitNumberAtom(ctx);
    }

    @Override
    public String visitParExpr(QuantlogicParser.ParExprContext ctx) {
        return super.visitParExpr(ctx);
    }

    @Override
    public String visitNotExpr(QuantlogicParser.NotExprContext ctx) {
        return super.visitNotExpr(ctx);
    }

    @Override
    public String visitUnaryMinusExpr(QuantlogicParser.UnaryMinusExprContext ctx) {
        return super.visitUnaryMinusExpr(ctx);
    }

    @Override
    public String visitElementExpr(QuantlogicParser.ElementExprContext ctx) {
        return super.visitElementExpr(ctx);
    }

    @Override
    public String visitStringAtom(QuantlogicParser.StringAtomContext ctx) {
        return super.visitStringAtom(ctx);
    }

    @Override
    public String visitMultiplicationExpr(QuantlogicParser.MultiplicationExprContext ctx) {
        return super.visitMultiplicationExpr(ctx);
    }

    @Override
    public String visitBlock(QuantlogicParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public String visitEqualityExpr(QuantlogicParser.EqualityExprContext ctx) {
        System.out.println("Equality Operation "+ctx.getStart().getText()+" , "+ctx.getStop().getText());
        return super.visitEqualityExpr(ctx);
    }

    @Override
    public String visitAndExpr(QuantlogicParser.AndExprContext ctx) {
        return super.visitAndExpr(ctx);
    }
}
