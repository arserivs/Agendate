package com.arsoft.agendate.views;

/**
 * Created by larcho on 8/24/16.
 */
public class SpinnerItem {

    private String id;
    private String label;

    public SpinnerItem(final String id, final String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getId() {
        return id;
    }
}
