package hw1;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class Query {

    private String q;

    public Query(String q) {
        this.q = q;
    }

    public Relation execute() {
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(q);
        } catch (JSQLParserException e) {
            System.out.println("Unable to parse query");
            e.printStackTrace();
        }
        Select selectStatement = (Select) statement;
        PlainSelect sb = (PlainSelect) selectStatement.getSelectBody();

        // Start by retrieving the main table
        FromItem fromItem = sb.getFromItem();
        Table mainTable = (Table) fromItem;
        Relation mainRelation = tableToRelation(mainTable);

        // Handle Joins
        List<Join> joins = sb.getJoins();
        if (joins != null) {
            for (Join join : joins) {
                Table joinTable = (Table) join.getRightItem();
                Relation joinRelation = tableToRelation(joinTable);
                Expression joinExpression = join.getOnExpression();
                if (joinExpression instanceof EqualsTo) {
                    EqualsTo equalsTo = (EqualsTo) joinExpression;
                    Column leftColumn = (Column) equalsTo.getLeftExpression();
                    Column rightColumn = (Column) equalsTo.getRightExpression();

                    mainRelation = mainRelation.join(joinRelation,
                    		mainRelation.getDesc().nameToId(leftColumn.getColumnName()), 
                    		joinRelation.getDesc().nameToId(rightColumn.getColumnName()));
                }
            }
        }

        // Handle WHERE conditions
        Expression whereExp = sb.getWhere();
        if (whereExp != null) {
            WhereExpressionVisitor whereVisitor = new WhereExpressionVisitor();
            whereExp.accept(whereVisitor);
            
            String fieldName = whereVisitor.getLeft();  // Get the name of the field
            RelationalOperator op = whereVisitor.getOp(); // Get the relational operator
            Field operand = whereVisitor.getRight(); // Get the operand

            int fieldIndex = mainRelation.getDesc().nameToId(fieldName); // Convert the field name to its index

            mainRelation = mainRelation.select(fieldIndex, op, operand);
        }
        List<SelectItem> selectItems = sb.getSelectItems();
        

     // Handle SELECT items
        ColumnVisitor colVisitor = new ColumnVisitor();
        ArrayList<Integer> projectFields = new ArrayList<>();

        for (SelectItem item : selectItems) {
            item.accept(colVisitor);
            
            String selectedColumn = colVisitor.isAggregate() ? colVisitor.getColumn() : item.toString();

            // If '*' is used
            if (selectedColumn.equals("*")) {
                for (int i = 0; i < mainRelation.getDesc().numFields(); i++) {
                    projectFields.add(i);
                }
                break;
            } 
            else {
                // If it's a regular column or the result of an aggregate operation
                int fieldIndex = selectedColumn.equals("*") && colVisitor.isAggregate() ? 0 : mainRelation.getDesc().nameToId(selectedColumn);
                if (!projectFields.contains(fieldIndex)) {
                    projectFields.add(fieldIndex);
                }
            }
        }

        mainRelation = mainRelation.project(projectFields);

        // Check if there's a need for aggregation
        List<Expression> groupByColumns = sb.getGroupByColumnReferences();
        boolean hasGroupBy = groupByColumns != null && !groupByColumns.isEmpty();
        AggregateOperator aggOp = null;
        String aggregateFunctionName = null;
        List<Function> aggregateFunctions = new ArrayList<>();
        for (SelectItem item : selectItems) {
            if (item instanceof SelectExpressionItem) {
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) item;
                Expression expression = selectExpressionItem.getExpression();
                if (expression instanceof Function) {
                    aggregateFunctionName = ((Function) expression).getName().toUpperCase();
                    break;
                }
            }
        }
        
        if(aggregateFunctionName != null) {
        	aggOp = stringToAggregateOperator(aggregateFunctionName);
        	System.out.print(hasGroupBy);
        	mainRelation = mainRelation.aggregate(aggOp, hasGroupBy);
        }



        return mainRelation;
    }
    public Relation tableToRelation(Table mainTable) {
        Catalog catalog = Database.getCatalog();
        int tableId = catalog.getTableId(mainTable.getName()); // Get table id from name
        HeapFile heapFile = catalog.getDbFile(tableId); // Get HeapFile using the table id
        ArrayList<Tuple> allTuples = heapFile.getAllTuples();
        TupleDesc tupleDesc = heapFile.getTupleDesc();
        
        return new Relation(allTuples, tupleDesc);
    }
    public AggregateOperator stringToAggregateOperator(String function) {
        return AggregateOperator.valueOf(function.toUpperCase());
    }
}

