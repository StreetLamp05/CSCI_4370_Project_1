package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        if (!rel1.getAttrs().equals(rel2.getAttrs()) || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations aren't compatible.");
        }

        Relation unionResult = emptyRelationWithSchema(rel1.getAttrs(),rel1.getTypes());

        Set<List<Cell>> seen = new HashSet<>();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);

            if (seen.add(row)) {
                unionResult.insert(row);
            }
        }

        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);

            if (seen.add(row)) {
                unionResult.insert(row);
            }
        }

        return unionResult;
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        if (!rel1.getAttrs().equals(rel2.getAttrs()) || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations aren't compatible.");
        }

        Relation intersectResult = emptyRelationWithSchema(rel1.getAttrs(), rel1.getTypes());

        Set<List<Cell>> rel2Rows = new HashSet<>();

        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);
            rel2Rows.add(row);
        }

        Set<List<Cell>> seen = new HashSet<>();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);
            
            if (rel2Rows.contains(row) && seen.add(row)) {
                intersectResult.insert(row);
            }
        }

        return intersectResult;
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        if (!rel1.getAttrs().equals(rel2.getAttrs()) || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations aren't compatible.");
        }

        Relation diffResult = emptyRelationWithSchema(rel1.getAttrs(), rel1.getTypes());

        Set<List<Cell>> rel2Rows = new HashSet<>();

        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);
            rel2Rows.add(row);
        }

        Set<List<Cell>> seen = new HashSet<>();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);
            
            if (!rel2Rows.contains(row) && seen.add(row)) {
                diffResult.insert(row);
            }
        }

        return diffResult;
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
        if (!commonAttrs(rel1, rel2).isEmpty()) {
            throw new IllegalArgumentException("Relations have common attributes.");
        }

        List<String> resultAttrs = new ArrayList<>(rel1.getAttrs());
        resultAttrs.addAll(rel2.getAttrs());

        List<Type> resultTypes = new ArrayList<>(rel1.getTypes());
        resultTypes.addAll(rel2.getTypes());

        Relation cartesianResult = emptyRelationWithSchema(resultAttrs, resultTypes);

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);

            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                List<Cell> combinedRow = concatCells(row1, row2);
                cartesianResult.insert(combinedRow);
            }
        }

        return cartesianResult;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        List<String> common = commonAttrs(rel1, rel2);
        List<Integer> keyIndices1 = resolveAttrIndices(rel1, common);
        List<Integer> keyIndices2 = resolveAttrIndices(rel2, common);
        List<String> keepAttrs = attrsOnlyIn(rel2, rel1);
        List<Integer> keepIndices = resolveAttrIndices(rel2, keepAttrs);
        List<String> attrs = new ArrayList<>(rel1.getAttrs());
        attrs.addAll(keepAttrs);
        List<Type> types = new ArrayList<>(rel1.getTypes());
        types.addAll(attrTypesAt(rel2, keepIndices));
        Relation result = emptyRelationWithSchema(attrs, types);
        Map<List<Cell>, List<List<Cell>>> groups = groupRowsByKey(rel2, keyIndices2);
        for (int i = 0; i < rel1.getSize(); ++i) {
            List<Cell> row = rel1.getRow(i);
            List<Cell> key = extractCells(row, keyIndices1);
            for (List<Cell> match : groups.getOrDefault(key, List.of())) {
                result.insert(concatCells(row, extractCells(match, keepIndices)));
            }
        }
        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        List<String> common = commonAttrs(rel1, rel2);
        if (!common.isEmpty()) {
            throw new IllegalArgumentException("Relations to join have a common attribute: "
                    + common.get(0));
        }
        List<String> attrs = new ArrayList<>(rel1.getAttrs());
        attrs.addAll(rel2.getAttrs());
        List<Type> types = new ArrayList<>(rel1.getTypes());
        types.addAll(rel2.getTypes());
        Relation result = emptyRelationWithSchema(attrs, types);
        for (int i = 0; i < rel1.getSize(); ++i) {
            List<Cell> row1 = rel1.getRow(i);
            for (int j = 0; j < rel2.getSize(); ++j) {
                List<Cell> combined = concatCells(row1, rel2.getRow(j));
                if (p.check(combined)) {
                    result.insert(combined);
                }
            }
        }
        return result;
    }

    /**
     * Returns the attributes that appear in both rel1 and rel2, in the order they
     * appear in rel1.
     */
    private List<String> commonAttrs(Relation rel1, Relation rel2) {
        List<String> common = new ArrayList<>();
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                common.add(attr);
            }
        }
        return common;
    }

    /**
     * Returns the attributes of rel that do not appear in other, in the order they
     * appear in rel.
     */
    private List<String> attrsOnlyIn(Relation rel, Relation other) {
        List<String> only = new ArrayList<>();
        for (String attr : rel.getAttrs()) {
            if (!other.hasAttr(attr)) {
                only.add(attr);
            }
        }
        return only;
    }

    /**
     * Returns the types of the columns of rel at the given indices, in order.
     */
    private List<Type> attrTypesAt(Relation rel, List<Integer> indices) {
        List<Type> types = rel.getTypes();
        List<Type> newTypes = new ArrayList<>();
        for (int idx : indices) {
            newTypes.add(types.get(idx));
        }
        return newTypes;
    }

    /**
     * Groups the rows of rel into buckets keyed by the cells at the given column
     * indices.
     */
    private Map<List<Cell>, List<List<Cell>>> groupRowsByKey(Relation rel,
            List<Integer> keyIndices) {
        Map<List<Cell>, List<List<Cell>>> groups = new HashMap<>();
        for (int i = 0; i < rel.getSize(); ++i) {
            List<Cell> row = rel.getRow(i);
            List<Cell> key = extractCells(row, keyIndices);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    /**
     * Builds a new row by appending the cells of row2 after the cells of row1.
     */
    private List<Cell> concatCells(List<Cell> row1, List<Cell> row2) {
        List<Cell> newRow = new ArrayList<>(row1);
        newRow.addAll(row2);
        return newRow;
    }

}