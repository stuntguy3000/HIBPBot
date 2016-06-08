package me.stuntguy3000.java.telegram.hibpbot.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

/**
 * @author stuntguy3000
 */
@Data
public class PaginatedList implements Iterator<List<String>> {
    private int currentPage;
    private HashMap<Integer, List<String>> pages = new HashMap<>();

    /**
     * Create a new PaginatedList Object
     *
     * @param contentList List the list of all Strings to be paginated
     * @param perPage Int the amount of items per page
     */
    public PaginatedList(List<String> contentList, int perPage) {
        int stringAmount = 0;
        List<String> currentPage = new ArrayList<>();

        for (String string : contentList) {
            stringAmount ++;
            currentPage.add(string);

            if (stringAmount == perPage) {
                pages.put(stringAmount, new ArrayList<>(currentPage));
                currentPage.clear();
                stringAmount = 0;
            }
        }

        if (stringAmount > 0) {
            pages.put(stringAmount, new ArrayList<>(currentPage));
        }
    }

    /**
     * Returns a page count
     *
     * @return Int the amount of pages
     */
    public int getPages() {
        return pages.size();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<String> next() {
        currentPage++;
        if (pages.size() < currentPage) {
            currentPage = 1;
        }

        return pages.get(currentPage);
    }

    @Override
    public void remove() {

    }
}
