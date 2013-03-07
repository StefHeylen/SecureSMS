package be.khleuven.kortlevenheylen.securesms.model;

import java.util.Comparator;
import java.util.Date;

public class DateSorter implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
    	Date d1 = m1.getDate();
    	Date d2 = m2.getDate();
    	int x = m1.getDate().compareTo(m2.getDate());
        return m1.getDate().compareTo(m2.getDate());
    }
}
