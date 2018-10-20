package lawa.dataModel;

import java.util.ArrayList;
import java.util.List;

public interface Diffable<T> {
    String getId();

    List<String> getChangedFields(Diffable<?> other);

    ArrayList<? extends Diffable<?>> getChildren();
}
