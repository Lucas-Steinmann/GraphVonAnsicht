package edu.kit.student.util;

import javafx.collections.ListChangeListener;

import java.util.Iterator;
import java.util.List;

public class ListSynchronization<T> implements ListChangeListener<T> {

    List<T> other;

    public ListSynchronization(List<T> other) {
        super();
        this.other = other;
    }

    @Override
    public void onChanged(Change<? extends T> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    if (c.getPermutation(i) > i) {
                        swap(other, i, c.getPermutation(i));
                    }
                }
            }
            else if (c.wasUpdated()) {
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    other.set(i, c.getList().get(i));
                }
            } else {
                for (int i = 0; i < c.getRemovedSize(); i++) {
                    other.remove(c.getFrom());
                }
                other.addAll(c.getFrom(), c.getAddedSubList());
            }
        }
        Iterator<T> syncit = other.listIterator();
        for (T item : c.getList()) {
            assert (syncit.hasNext() && syncit.next() == item) : "List Synchronization failed.";
        }
    }

    private <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.add(i, list.get(j));
        list.remove(i + 1);
        list.add(j, temp);
        list.remove(j + 1);
    }
}
