package com.github.vrcca.goos.application.view.ui;

import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionSniper;
import com.github.vrcca.goos.domain.Item;
import com.github.vrcca.goos.domain.SniperSnapshot;
import com.github.vrcca.goos.domain.exception.Defect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static com.github.vrcca.goos.application.view.ui.SnipersTableModel.textFor;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SnipersTableModelTest {

    @Mock
    private TableModelListener listener;

    @Mock
    private Auction auction;

    private SnipersTableModel model;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        model = new SnipersTableModel();
    }

    @Before
    public void attachModelListener() throws Exception {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() throws Exception {
        assertThat(model.getColumnCount(), is(equalTo(Column.values().length)));
    }

    @Test
    public void setsSniperValuesInColumns() throws Exception {
        // given
        final AuctionSniper sniper = new AuctionSniper(new Item("item id", 444), auction);
        model.sniperAdded(sniper);

        // when
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
        model.sniperStateChanged(bidding);

        // then
        assertRowMatchesSnapshot(0, bidding);
        verify(listener, atLeastOnce()).tableChanged(withAnInsertionAtRow(0));
    }

    @Test
    public void setsUpColumnHeadings() throws Exception {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingASniper() throws Exception {
        // given
        final AuctionSniper sniper = new AuctionSniper(new Item("item123", 111), auction);
        assertThat(model.getRowCount(), is(0));

        // when
        model.sniperAdded(sniper);

        // then
        assertThat(model.getRowCount(), is(1));
        assertRowMatchesSnapshot(0, sniper.getSnapshot());
        verify(listener).tableChanged(withAnInsertionAtRow(0));
    }

    @Test
    public void holdsSnipersInAdditionOrder() throws Exception {
        // when
        model.sniperAdded(new AuctionSniper(new Item("item 0", 111), auction));
        model.sniperAdded(new AuctionSniper(new Item("item 1", 222), auction));

        // then
        assertThat(cellValue(0, Column.ITEM_IDENTIFIER), is("item 0"));
        assertThat(cellValue(1, Column.ITEM_IDENTIFIER), is("item 1"));
    }

    @Test
    public void updatesCorrectRowForSniper() throws Defect {
        // given
        final AuctionSniper sniper1 = new AuctionSniper(new Item("item 0", 111), auction);
        final AuctionSniper sniper2 = new AuctionSniper(new Item("item 1", 222), auction);
        model.sniperAdded(sniper1);
        model.sniperAdded(sniper2);
        final SniperSnapshot bidding = sniper1.getSnapshot().bidding(100, 20);

        // when
        model.sniperStateChanged(bidding);

        // then
        assertRowMatchesSnapshot(0, bidding);
        assertRowMatchesSnapshot(1, sniper2.getSnapshot());

    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertRowColumnEquals(row, Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertRowColumnEquals(row, Column.LAST_PRICE, snapshot.lastPrice);
        assertRowColumnEquals(row, Column.LAST_BID, snapshot.lastBid);
        assertRowColumnEquals(row, Column.SNIPER_STATE, textFor(snapshot.state));
    }

    private TableModelEvent withAnInsertionAtRow(int row) {
        return refEq(new TableModelEvent(model, row));
    }

    private void assertRowColumnEquals(int rowIndex, Column column, Object expected) {
        assertEquals(expected, cellValue(rowIndex, column));
    }

    private Object cellValue(int rowIndex, Column column) {
        final int columnIndex = column.ordinal();
        return model.getValueAt(rowIndex, columnIndex);
    }

    private TableModelEvent withARowChangedEvent() {
        return refEq(new TableModelEvent(model, 0));
    }
}