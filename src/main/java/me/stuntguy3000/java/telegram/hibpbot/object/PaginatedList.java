package me.stuntguy3000.java.telegram.hibpbot.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * @author stuntguy3000
 */
@Data
public class PaginatedList {
    private int currentPage = 1;
    private HashMap<Integer, List<String>> pages = new HashMap<>();

    /**
     * Create a new PaginatedList Object
     *
     * @param contentList List the list of all Strings to be paginated
     * @param perPage Int the amount of items per page
     */
    public PaginatedList(List<String> contentList, int perPage) {
        int stringAmount = 0;
        int pageID = 0;
        List<String> currentPage = new ArrayList<>();

        for (String string : contentList) {
            stringAmount ++;
            currentPage.add(string);

            if (stringAmount == perPage) {
                pageID++;
                pages.put(pageID, new ArrayList<>(currentPage));
                currentPage.clear();
                stringAmount = 0;
            }
        }

        if (stringAmount > 0) {
            if (pageID == 0) {
                pageID++;
            }

            pages.put(pageID, new ArrayList<>(currentPage));
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

    public String switchToNextPage() {
        currentPage++;
        if (pages.size() < currentPage) {
            currentPage = 1;
        }

        return process(pages.get(currentPage));
    }

    public String switchToPreviousPage() {
        currentPage--;
        if (currentPage < 1) {
            currentPage = pages.size();
        }

        return process(pages.get(currentPage));
    }

    public String getCurrentPageContent() {
        return process(pages.get(currentPage));
    }

    private String process(List<String> content) {
        if (content == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String line : content) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
