package lawa.services;

import lawa.dataModel.Bezirk;
import lawa.dataModel.Comparer;
import lawa.dataModel.DiffNode;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static lawa.dataModel.ChangeType.Added;
import static lawa.dataModel.ChangeType.Deleted;
import static org.assertj.core.api.Assertions.assertThat;

public class DifferServiceTest {

    @Test
    public void it_should_return_diffeNode_for_one_change_added_and_deleted() {
        Bezirk bezirk1 = new Bezirk("bezirk1", LocalDate.of(2018, 10, 21), 3);
        Bezirk bezirk2 = new Bezirk("bezirk2", LocalDate.of(2018, 10, 21), 5);
        Bezirk bezirk3 = new Bezirk("bezirk3", LocalDate.of(2018, 10, 21), 8);
        Bezirk bezirk4 = new Bezirk("bezirk4", LocalDate.of(2018, 10, 21), 12);

        ArrayList<Bezirk> bezirke_old = new ArrayList<>();
        bezirke_old.add(bezirk1);
        bezirke_old.add(bezirk2);
        bezirke_old.add(bezirk3);

        ArrayList<Bezirk> bezirke_new = new ArrayList<>();
        bezirke_new.add(bezirk1);
        bezirke_new.add(bezirk2);
        bezirke_new.add(bezirk4);

        List<DiffNode> diffNodes = Comparer.FindDiffs(bezirke_new, bezirke_old);

        assertThat(diffNodes.size()).isEqualTo(2);
        assertThat(diffNodes.get(0).getChangeType()).isEqualTo(Added);
        assertThat(diffNodes.get(1).getChangeType()).isEqualTo(Deleted);
    }

}
