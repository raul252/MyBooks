package com.cifo.rgonzalezgall.mybooks.helper;

import com.cifo.rgonzalezgall.mybooks.model.BookItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by Raul
 */
public class Helper {

    /**
     * An array of sample (BookItem) items.
     */
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();

    /**
     * A map of sample (BookItem) items, by ID.
     */
    public static final Map<Integer, BookItem> ITEM_MAP = new HashMap<Integer, BookItem>();

    private static final int COUNT = 25;

    static {
        // Add COUNT sample items.
        for (int i = 1; i <= COUNT; i++) {
            BookItem item = createDummyItem(i);
            //Add to the List
            ITEMS.add(item);
            //Add to the map
            ITEM_MAP.put(item.getIdentificador(), item);
        }
    }

    /*
    Create a BookItem
     */
    private static BookItem createDummyItem(int position) {
        Date d = new java.util.Date();
        return new BookItem(position, "Titulo" + position, "Autor" + position, d,"Descripcion" + position,
                "img_" + position);
    }
}