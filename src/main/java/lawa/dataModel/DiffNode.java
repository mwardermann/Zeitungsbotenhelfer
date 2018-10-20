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
}
