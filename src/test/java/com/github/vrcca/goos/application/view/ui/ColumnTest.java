package com.github.vrcca.goos.application.view.ui;

import com.github.vrcca.goos.domain.SniperSnapshot;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ColumnTest {

    private SniperSnapshot snapshot;

    @Before
    public void setUp() throws Exception {
        snapshot = SniperSnapshot
                .joining("item-1234")
                .bidding(111, 222);
    }

    @Test
    public void shouldPrintIdentifier() throws Exception {
        // given
        final Column column = Column.ITEM_IDENTIFIER;

        // when
        final Object value = column.valueIn(snapshot);

        // then
        assertThat(value, is("item-1234"));
    }

    @Test
    public void shouldPrintLastPrice() throws Exception {
        // given
        final Column column = Column.LAST_PRICE;

        // when
        final Object value = column.valueIn(snapshot);

        // then
        assertThat(value, is(111));
    }

    @Test
    public void shouldPrintLastBid() throws Exception {
        // given
        final Column column = Column.LAST_BID;

        // when
        final Object value = column.valueIn(snapshot);

        // then
        assertThat(value, is(222));
    }

    @Test
    public void shouldPrintState() throws Exception {
        // given
        final Column column = Column.SNIPER_STATE;

        // when
        final Object value = column.valueIn(snapshot);

        // then
        assertThat(value, is("Bidding"));
    }
}