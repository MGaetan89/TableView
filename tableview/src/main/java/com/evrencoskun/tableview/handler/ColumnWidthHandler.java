/*
 * MIT License
 *
 * Copyright (c) 2021 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.evrencoskun.tableview.handler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.CellRecyclerView;
import com.evrencoskun.tableview.layoutmanager.ColumnHeaderLayoutManager;
import com.evrencoskun.tableview.layoutmanager.ColumnLayoutManager;

/**
 * Created by evrencoskun on 25.04.2018.
 */

public class ColumnWidthHandler {
    @NonNull
    private final ITableView mTableView;

    public ColumnWidthHandler(@NonNull ITableView tableView) {
        mTableView = tableView;
    }

    public void setColumnWidth(int columnPosition, int width) {

        // Firstly set the column header cache map
        mTableView.getColumnHeaderLayoutManager().setCacheWidth(columnPosition, width);

        // Set each of cell items that is located on the column position
        mTableView.getCellLayoutManager().setCacheWidth(columnPosition, width);

        // Invalidate the column and its header to take the new width into account
        invalidateColumn(columnPosition);
        invalidateColumnHeader(columnPosition);
    }

    public void remeasureColumnWidth(int columnPosition) {

        // Firstly reset the column header cache map
        mTableView.getColumnHeaderLayoutManager().removeCachedWidth(columnPosition);

        // Reset each of cell items that is located on the column position
        mTableView.getCellLayoutManager().removeCacheWidth(columnPosition);

        // Invalidate the column to recalculate its width, and the width of the column header
        invalidateColumn(columnPosition);
        invalidateColumnHeader(columnPosition);
    }

    private void invalidateColumnHeader(int columnPosition) {

        ColumnHeaderLayoutManager columnHeaderLayoutManager = mTableView.getColumnHeaderLayoutManager();
        View columnHeader = columnHeaderLayoutManager.getChildAt(columnPosition);
        if (columnHeader != null) {
            columnHeaderLayoutManager.measureChild(columnHeader, 0, 0);
        }
    }

    private void invalidateColumn(int columnPosition) {

        CellRecyclerView cellRecyclerView = mTableView.getCellRecyclerView();
        for (int row = 0; row < cellRecyclerView.getChildCount(); row++) {
            RecyclerView recyclerView = (RecyclerView) cellRecyclerView.getChildAt(row);
            if (recyclerView != null) {
                ColumnLayoutManager columnLayoutManager = (ColumnLayoutManager) recyclerView.getLayoutManager();
                if (columnLayoutManager != null) {
                    View cell = columnLayoutManager.getChildAt(columnPosition);
                    if (cell != null) {
                        columnLayoutManager.measureChild(cell, 0, 0);
                    }
                }
            }
        }
    }
}
