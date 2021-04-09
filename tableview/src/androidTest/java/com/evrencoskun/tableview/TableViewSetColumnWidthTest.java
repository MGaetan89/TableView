package com.evrencoskun.tableview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.evrencoskun.tableview.adapter.recyclerview.CellRecyclerView;
import com.evrencoskun.tableview.layoutmanager.CellLayoutManager;
import com.evrencoskun.tableview.layoutmanager.ColumnLayoutManager;
import com.evrencoskun.tableview.test.TestActivity;
import com.evrencoskun.tableview.test.adapters.SimpleTestAdapter;
import com.evrencoskun.tableview.test.data.SimpleData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TableViewSetColumnWidthTest {

    @Rule
    public ActivityScenarioRule<TestActivity> mActivityRule =
            new ActivityScenarioRule<>(TestActivity.class);

    private TableView tableView;

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        SimpleData data = new SimpleData(10);
        SimpleTestAdapter adapter = new SimpleTestAdapter();

        tableView = new TableView(context);
        tableView.setAdapter(adapter);

        adapter.setAllItems(data.getColumnHeaders(), data.getRowHeaders(), data.getCells());

        mActivityRule.getScenario()
                .onActivity(activity -> activity.setContentView(tableView));

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    @Test
    public void setColumnWidth() {
        int column = 3;
        int initialWidth = tableView.getColumnHeaderRecyclerView().getChildAt(column).getWidth();
        int newWidth = initialWidth * 2;

        mActivityRule.getScenario()
                .onActivity(activity -> tableView.setColumnWidth(column, newWidth));

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        validateColumnWidth(tableView, column, newWidth);

        mActivityRule.getScenario()
                .onActivity(activity -> tableView.remeasureColumnWidth(column));

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        validateColumnWidth(tableView, column, initialWidth);
    }

    @Test
    public void setColumnWidthWithHiddenColumn() {
        int column = 2;
        int newWidth = 300;

        mActivityRule.getScenario()
                .onActivity(activity -> {
                    tableView.hideColumn(2);
                    tableView.hideColumn(3);
                    tableView.setColumnWidth(column, newWidth);
                });

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        validateColumnWidth(tableView, column, newWidth);
    }

    private void validateColumnWidth(
            @NonNull TableView tableView,
            int columnPosition,
            int expectedWidth
    ) {
        View columnHeaderChild = tableView.getColumnHeaderRecyclerView().getChildAt(columnPosition);
        CellLayoutManager cellLayoutManager = tableView.getCellLayoutManager();

        assertEquals("Wrong column header width", expectedWidth, columnHeaderChild.getWidth());

        for (int i = cellLayoutManager.findFirstVisibleItemPosition(); i < cellLayoutManager.findLastVisibleItemPosition() + 1; i++) {
            CellRecyclerView cellRecyclerView = (CellRecyclerView) cellLayoutManager.findViewByPosition(i);
            assertNotNull(cellRecyclerView);

            ColumnLayoutManager columnLayoutManager = (ColumnLayoutManager) cellRecyclerView.getLayoutManager();
            assertNotNull(columnLayoutManager);

            View cell = columnLayoutManager.findViewByPosition(columnPosition);
            assertNotNull(cell);

            assertEquals("Wrong column width at row " + i, expectedWidth, cell.getWidth());
        }
    }
}
