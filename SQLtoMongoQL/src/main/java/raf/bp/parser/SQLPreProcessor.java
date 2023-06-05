package raf.bp.parser;

import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.sqlextractor.concrete.FromExtractor;

import java.util.ArrayList;
import java.util.List;

public class SQLPreProcessor {

    public String process(String query) {
        /* should take queries with aliased tables and replace them with full names */

        CSQLFromTable mainTable = null;
        ArrayList<CSQLFromTable> joinTables = new ArrayList<>();

        ArrayList<String> fromTableNames = new ArrayList<>();
        ArrayList<String> joinTableNames = new ArrayList<>();
        boolean from = false;
        boolean join = false;
        boolean onUsing = false;
        for (String word : query.split(" ")) {
            if (word.equalsIgnoreCase("from")) {
                from = true;
                join = false;
                onUsing = false;
                continue;
            }
            if (word.equalsIgnoreCase("join"))  {
                from = false;
                join = true;
                onUsing = false;

                if (!fromTableNames.isEmpty()) {

                    if (fromTableNames.size() > 1) mainTable = new CSQLFromTable(fromTableNames.get(0), fromTableNames.get(1));
                    else mainTable = new CSQLFromTable(fromTableNames.get(0), "");

                    fromTableNames.clear();
                }
                continue;
            }
            if (word.equalsIgnoreCase("on") || word.equalsIgnoreCase("using"))  {
                from = false;
                join = false;
                onUsing = true;
                if (!joinTableNames.isEmpty()) {

                    CSQLFromTable temp;
                    if (joinTableNames.size() > 1) temp = new CSQLFromTable(joinTableNames.get(0), joinTableNames.get(1));
                    else temp = new CSQLFromTable(joinTableNames.get(0), "");

                    joinTables.add(temp);

                    joinTableNames.clear();
                }

                continue;
            }

            if (from) {
                fromTableNames.add(word);
            } if (join) {
                joinTableNames.add(word);
            }

        }

        return replaceAliases(query, mainTable, joinTables);
    }

    public String replaceAliases(String query, CSQLFromTable mainTable, ArrayList<CSQLFromTable> joinTables) {

        System.out.println("Main Table: " + mainTable);
        System.out.println("Joined table: " + joinTables);
        StringBuilder sb = new StringBuilder();
        for (String word :  query.split(" ")) {

            if (word.contains(mainTable.getAlias() + ".")) {
                word = word.replace(mainTable.getAlias() + ".", "");
            } else if (word.contains(mainTable.getTableName() + ".")) {
                word = word.replace(mainTable.getTableName() + ".", "");
            }

            for (CSQLFromTable joinTable : joinTables) {

                if (word.contains(joinTable.getAlias() + ".")) {
                    word = word.replace(joinTable.getAlias() + ".", joinTable.getTableName() + ".");
                }
            }

            sb.append(word).append(" ");
        }

        String resultingQuery = sb.toString();
        resultingQuery = resultingQuery.replace(mainTable.getTableName() + " " + mainTable.getAlias(), mainTable.getTableName());

        for (CSQLFromTable joinTable : joinTables) {
            resultingQuery = resultingQuery.replace(joinTable.getTableName() + " " + joinTable.getAlias(), joinTable.getTableName());
        }

        return resultingQuery;
    }

    public static void main(String[] args) {
        SQLPreProcessor preProcessor = new SQLPreProcessor();

        ArrayList<String> queries = new ArrayList<>(List.of(
                "SELECT e.first_name, last_name, employees.salary, departments.department_name, d.department_id FROM employees e " +
                        "JOIN departments d ON employees.department_id = d.department_id " +
                        "WHERE e.salary > 10000 ORDER BY salary desc, first_name desc",
                "SELECT Customers.customer_id, Customers.customer_name, o.order_id, Orders.order_date, Products.product_name, p.price " +
                        "FROM Customers c " +
                        "JOIN Orders o ON Customers.customer_id = Orders.customer_id " +
                        "JOIN Products p ON Orders.order_id = p.product_id " +
                        "WHERE Customers.customer_id = 1 " +
                        "ORDER BY o.order_date DESC;"

        ));

        for (String query : queries) {
            System.out.println(preProcessor.process(query));
        }

    }
}
