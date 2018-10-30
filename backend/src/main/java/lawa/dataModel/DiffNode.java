package lawa.dataModel;

import java.util.ArrayList;
import java.util.List;

public class DiffNode {
    private final Diffable<?> diffable;
    private final ChangeType changeType;
    private final List<String> changedFields;
    private final List<DiffNode> childNodes;
    DiffNode(Diffable<?> diffable, ChangeType changeType) {
        this(diffable, changeType, new ArrayList<>(), new ArrayList<>());
    }

    DiffNode(Diffable<?> diffable, ChangeType changeType, List<String> changedFields, List<DiffNode> childNodes) {
        this.diffable = diffable;
        this.changeType = changeType;
        this.changedFields = changedFields;
        this.childNodes = childNodes;
    }

    public Diffable<?> getDiffable() {
        return diffable;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public List<String> getChangedFields() {
        return changedFields;
    }

    public List<DiffNode> getChildNodes() {
        return childNodes;
    }
}
