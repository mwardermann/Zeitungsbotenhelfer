package lawa.dataModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Comparer {
     public static List<DiffNode> FindDiffs(ArrayList<? extends Diffable<?>> newList, ArrayList<? extends Diffable<?>> oldList) {
        Set<String> newMap = newList.stream().map(Diffable::getId).collect(Collectors.toSet());
         Map<String, ? extends Optional<? extends Diffable<?>>> oldMap =
                 oldList.stream().collect(Collectors.groupingBy(Diffable::getId, Collectors.minBy((o1, o2) -> 1)));

        ArrayList<DiffNode> diffs = new ArrayList<>();

        for (Diffable<?> diffable: newList){
            String id = diffable.getId();

            Optional<? extends Diffable<?>> oldDiffable = oldMap.getOrDefault(id, null);

            if (oldDiffable == null || !oldDiffable.isPresent()){
                diffs.add(new DiffNode(diffable, ChangeType.Added));
            }
            else {
                List<String> changedFields = diffable.getChangedFields(oldDiffable.get());

                List<DiffNode> childNodes = FindDiffs(diffable.getChildren(), oldDiffable.get().getChildren());

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
