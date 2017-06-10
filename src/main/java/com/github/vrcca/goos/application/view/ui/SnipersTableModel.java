package com.github.vrcca.goos.application.view.ui;

import com.github.vrcca.goos.domain.AuctionSniper;
import com.github.vrcca.goos.domain.PortfolioListener;
import com.github.vrcca.goos.domain.SniperListener;
import com.github.vrcca.goos.domain.SniperSnapshot;
import com.github.vrcca.goos.domain.SniperState;
import com.github.vrcca.goos.domain.exception.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel
        extends AbstractTableModel
        implements SniperListener, PortfolioListener {

    private static String[] STATUS_TEXT = {
            "Joining",
            "Bidding",
            "Winning",
            "Losing",
            "Lost",
            "Won"
    };

    private List<SniperSnapshot> snapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect(String.format("Could not find snapshot for itemId %s. " +
                "Should always find a snapshot.", snapshot.itemId));
    }

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(this);
    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        this.snapshots.add(snapshot);
        int row = snapshots.size() - 1;
        fireTableRowsUpdated(row, row);
    }
}