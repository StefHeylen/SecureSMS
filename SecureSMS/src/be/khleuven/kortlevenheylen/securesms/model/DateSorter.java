package be.khleuven.kortlevenheylen.securesms.model;

import java.util.Comparator;

public class DateSorter implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
        return m1.getDate().compareTo(m2.getDate());
    }
}
