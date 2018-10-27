package com.cifo.rgonzalezgall.mybooks.helper;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BookContent class for providing sample content for user interfaces created by Raul
 */
public class BookContent {

    public static List<BookContent.BookItem> getBooks(){
        return BookItem.listAll(BookItem.class);
    }

    public static boolean exists(BookItem bookitem){
        boolean found = false;
        List<BookItem> book =BookItem.find(BookItem.class, "title = ?", new String(bookitem.getTitle()));
        // Si hay libro con el mismo título, el libro existe.
        if (!book.isEmpty()) {
            found = true;
        }
        return found;
    }
//
//    /**
//     * An array of sample (BookItem) items.
//     */
    public static final List<BookContent.BookItem> ITEMS = new ArrayList<BookContent.BookItem>();
//
//    /**
//     * A map of sample (BookItem) items, by ID.
//     */
//    public static final Map<Integer, BookContent.BookItem> ITEM_MAP = new HashMap<>();
//
//    private static final int COUNT = 25;
//
//    static {
//        // Add COUNT sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            //BookItem item = createDummyItem(i);
//            //Add to the List
//            //ITEMS.add(item);
//            //Add to the map
//            //ITEM_MAP.put(item.getIdentificador(), item);
//        }
//    }

    /*
    Create a BookItem
     */
    /*private static BookItem createDummyItem(int position) {
        Date d = new java.util.Date();
        return new BookItem(position, "Titulo" + position, "Autor" + position, d,"Descripcion" + position,
                "img_" + position);
    }*/

    /**
     * A dummy BookItem representing a simple book
     */
    public static class BookItem extends SugarRecord {

        public BookItem(){

        }
        public BookItem(String identificador, String title, String author, String publication_date, String description, String url_image){
            this.identificador = identificador;
            this.title = title;
            this.author = author;
            this.publication_date = publication_date;
            this.description = description;
            this.url_image = url_image;
        }

        private String identificador;
        private String title;
        private String author;
        private String publication_date;
        private String description;
        private String url_image;

        public String getIdentificador() {
            return identificador;
        }

        public void setIdentificador(String identificador) {
            this.identificador = identificador;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getPublication_date() {
            return publication_date;
        }

        public void setPublication_date(String publication_date) {
            this.publication_date = publication_date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl_image() {
            return url_image;
        }

        public void setUrl_image(String url_image) {
            this.url_image = url_image;
        }

    }
}