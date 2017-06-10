package com.github.vrcca.goos.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Item {
    public final String identifier;
    public final int stopPrice;

    public Item(String identifier, int stopPrice) {
        this.identifier = identifier;
        this.stopPrice = stopPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return new EqualsBuilder()
                .append(identifier, item.identifier)
                .append(stopPrice, item.stopPrice)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(stopPrice)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifier", identifier)
                .append("stopPrice", stopPrice)
                .toString();
    }
}
