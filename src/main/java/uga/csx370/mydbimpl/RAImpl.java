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
        Relation result = new RelationBuilder()
                .attributeNames(rel.getAttrs())
                .attributeTypes(rel.getTypes())
                .build();
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
        List<Integer> indices = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        for (String attr : attrs) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException("Attribute does not exist: " + attr);
            }
            int idx = rel.getAttrIndex(attr);
            indices.add(idx);
            types.add(rel.getTypes().get(idx));
        }
        Relation result = new RelationBuilder()
                .attributeNames(new ArrayList<>(attrs))
                .attributeTypes(types)
                .build();
        for (int i = 0; i < rel.getSize(); ++i) {
            List<Cell> row = rel.getRow(i);
            List<Cell> newRow = new ArrayList<>();
            for (int idx : indices) {
                newRow.add(row.get(idx));
            }
            result.insert(newRow);
        }
        return result;
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