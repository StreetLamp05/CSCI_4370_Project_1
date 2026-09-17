package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uga.csx370.mydb.Cell;
import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        Relation result = emptyRelationWithSchema(rel.getAttrs(), rel.getTypes());
        for (int i = 0; i < rel.getSize(); ++i) {
            List<Cell> row = rel.getRow(i);
            if (p.check(row)) {
                result.insert(row);
            }
        }
        return result;
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        List<Integer> indices = resolveAttrIndices(rel, attrs);
        List<Type> types = new ArrayList<>();
        for (int idx : indices) {
            types.add(rel.getTypes().get(idx));
        }
        Relation result = emptyRelationWithSchema(attrs, types);
        for (int i = 0; i < rel.getSize(); ++i) {
            result.insert(extractCells(rel.getRow(i), indices));
        }
        return result;
    }

    /**
     * Builds an empty relation using the given attribute names and types.
     */
    private Relation emptyRelationWithSchema(List<String> attrNames, List<Type> attrTypes) {
        return new RelationBuilder()
                .attributeNames(new ArrayList<>(attrNames))
                .attributeTypes(new ArrayList<>(attrTypes))
                .build();
    }

    /**
     * Resolves each attribute name to its column index in rel, in the given order.
     *
     * @throws IllegalArgumentException if any attribute in attrs is not present in rel.
     */
    private List<Integer> resolveAttrIndices(Relation rel, List<String> attrs) {
        List<Integer> indices = new ArrayList<>();
        for (String attr : attrs) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException("Attribute does not exist: " + attr);
            }
            indices.add(rel.getAttrIndex(attr));
        }
        return indices;
    }

    /**
     * Builds a new row containing only the cells at the given column indices, in order.
     */
    private List<Cell> extractCells(List<Cell> row, List<Integer> indices) {
        List<Cell> newRow = new ArrayList<>();
        for (int idx : indices) {
            newRow.add(row.get(idx));
        }
        return newRow;
    }

    @Override
    public Relation union(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'union'");
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'intersect'");
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'diff'");
    }

    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException("origAttr and renamedAttr must have "
                    + "matching argument counts.");
        }
        Map<String, String> renameMap = new HashMap<>();
        for (int i = 0; i < origAttr.size(); ++i) {
            String orig = origAttr.get(i);
            if (!rel.hasAttr(orig)) {
                throw new IllegalArgumentException("Attribute does not exist: " + orig);
            }
            renameMap.put(orig, renamedAttr.get(i));
        }
        List<String> newAttrs = new ArrayList<>();
        for (String attr : rel.getAttrs()) {
            newAttrs.add(renameMap.getOrDefault(attr, attr));
        }
        Relation result = new RelationBuilder()
                .attributeNames(newAttrs)
                .attributeTypes(rel.getTypes())
                .build();
        for (int i = 0; i < rel.getSize(); ++i) {
            result.insert(rel.getRow(i));
        }
        return result;
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cartesianProduct'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'join'");
    }

}