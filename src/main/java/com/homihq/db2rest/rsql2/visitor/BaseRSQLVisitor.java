package com.homihq.db2rest.rsql2.visitor;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbWhere;
import com.homihq.db2rest.rsql2.operator.handler.OperatorHandler;
import com.homihq.db2rest.rsql2.operator.handler.RSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BaseRSQLVisitor implements RSQLVisitor<String, Object> {

    private final DbWhere dbWhere;
    private final Dialect dialect;

    @Override
    public String visit(AndNode andNode, Object o) {
        return "( " + andNode.getChildren().stream().map(node -> node.accept(this))
            .collect(Collectors.joining(" AND ")) + " ) ";
    }

    @Override
    public String visit(OrNode orNode, Object o) {
        return "( " +
            orNode.getChildren().stream().map(node -> node.accept(this)).collect(Collectors.joining(" OR ")) +
            " ) ";
    }

    @Override
    public String visit(ComparisonNode node, Object o) {
        ComparisonOperator op = node.getOperator();

        log.debug("Handling column - {}", node.getSelector());

        DbColumn dbColumn = this.dbWhere.table().buildColumn(node.getSelector());

        Class<?> type = dbColumn.column().getType().getTypeMappedClass();


        OperatorHandler operatorHandler = RSQLOperatorHandlers.getOperatorHandler(op.getSymbol());
        if (operatorHandler == null) {
            throw new IllegalArgumentException(String.format("Operator '%s' is invalid", op.getSymbol()));
        }

        if (op.isMultiValue()) {
            return operatorHandler.handle(dialect, dbColumn, node.getArguments(), type, this.dbWhere.paramMap());
        }
        else {
            return operatorHandler.handle(dialect,dbColumn, node.getArguments().get(0), type, this.dbWhere.paramMap());
        }

    }



}