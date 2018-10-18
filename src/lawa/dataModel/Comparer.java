package lawa.dataModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Comparer {
     public static List<DiffNode> FindDiffs(ArrayList<? extends Diffable<?>> newList, ArrayList<? extends Diffable<?>> oldList) {
         Set<String> newMap = newList.stream().map(Diffable::getId).collect(Collectors.toSet());
        Map<String, Diffable<?>> oldMap = oldList.stream().collect(Collectors.toMap(Diffable::getId, a -> a));

        ArrayList<DiffNode> diffs = new ArrayList<>();

        for (Diffable<?> diffable: newList){
            String id = diffable.getId();

            Diffable<?> oldDiffable = oldMap.getOrDefault(id, null);
            if (oldDiffable == null){
                diffs.add(new DiffNode(diffable, ChangeType.Added));
            }
            else {
                List<String> changedFields = diffable.getChangedFields(oldDiffable);

                List<DiffNode> childNodes = FindDiffs(diffable.getChildren(), oldDiffable.getChildren());

                if (!changedFields.isEmpty() || !childNodes.isEmpty()){
                    diffs.add(new DiffNode(diffable, ChangeType.Modified, changedFields, childNodes));
                }
            }
        }

         Stream<? extends Diffable<?>> deletedDiffables = oldList.stream().filter(b -> !newMap.contains(b.getId()));
         Stream<DiffNode> deletedNodes = deletedDiffables.map(b -> new DiffNode(b, ChangeType.Deleted));

         diffs.addAll((deletedNodes.collect(Collectors.toList())));

        return diffs;
    }


}
