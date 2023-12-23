package utils;

import query.FilterExpTree;
import query.FilterNode;
import query.LogicalOperator;
import query.UniversalQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryUtils {

    public static List<String> getFieldsForQuery(UniversalQuery query) {
        ArrayList<String> fieldsList = new ArrayList<>(query.getFieldsList());
        fieldsList.addAll(getFieldsFromFilterNode(query.getFilter()));
        return fieldsList;
    }

    private static List<String> getFieldsFromFilterNode(FilterNode filterNode) {
        if (filterNode.hasExpTree()) {
            ArrayList<String> fields = new ArrayList<>();
            for (FilterNode node : filterNode.getExpTree().getNodesList()) {
                fields.addAll(getFieldsFromFilterNode(node));
            }
            return fields;
        } else {
            return Collections.singletonList(filterNode.getFilterExp().getField());
        }
    }

    public static String convertToLogicalExpression(FilterNode node) {
        if (node.hasFilterExp()) {
            // If the node contains a single filter
            FilterNode.FilterExp filter = node.getFilterExp();
            return filter.getField() + " " + filter.getOperator() + " " + filter.getValue();
        } else if (node.hasExpTree()) {
            // If the node contains an expression tree
            FilterExpTree expTree = node.getExpTree();
            List<FilterNode> nodes = expTree.getNodesList();
            LogicalOperator operator = expTree.getOperator();

            StringBuilder expression = new StringBuilder();
            String logicalOperator = (operator == LogicalOperator.AND) ? " AND " : " OR ";

            for (int i = 0; i < nodes.size(); i++) {
                if (i != 0) {
                    expression.append(logicalOperator);
                }
                expression.append("(").append(convertToLogicalExpression(nodes.get(i))).append(")");
            }
            return expression.toString();
        }
        return "";
    }
}
