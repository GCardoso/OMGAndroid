package com.example.guilhermecardoso.omgandroid;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import entity.Image;

/**
 * Created by Italo on 22/07/2015.
 */
public class TableHelper {

    Context context;
    static int count = 0;

    public TableHelper(Context context) {
        this.context = context;
    }

    protected void createTable(TableLayout mainTable) {
        TableRow tableRowHeader = new TableRow(context);
        tableRowHeader.setId(10);
        tableRowHeader.setBackgroundColor(Color.GRAY);
        tableRowHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView labelInfo = new TextView(context);
        labelInfo.setId(20);
        labelInfo.setText("Info Sobre a Imagem");
        labelInfo.setTextColor(Color.WHITE);
        labelInfo.setPadding(5, 5, 5, 5);
        tableRowHeader.addView(labelInfo);// add the column to the table row here

        mainTable.addView(tableRowHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    protected void addRow(TableLayout mainTable,Image img) {
        TableRow newRow = new TableRow(context);
        if (count % 2 != 0) {
            newRow.setBackgroundColor(Color.GRAY);
        } else {
            newRow.setBackgroundColor(Color.DKGRAY);
        }

        newRow.setId(100 + count);
        newRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        newRow.setGravity(Gravity.TOP);

        TextView labelNome = new TextView(context);
        labelNome.setId(200 + count);
        labelNome.setText(img.getName() + " ");
        labelNome.setPadding(2, 0, 5, 0);
        labelNome.setTextColor(Color.WHITE);
        newRow.addView(labelNome);

        View v = new View(context);
        v.setLayoutParams(new TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT));
        v.setBackgroundColor(Color.rgb(150, 50, 150));
        newRow.addView(v);


        TextView labelXYZ = new TextView(context);
        labelXYZ.setId(200 + count);
        labelXYZ.setText(img.getAccelerometerX() + " " + img.getAccelerometerY() + " " + img.getAccelerometerZ() + " ");
        labelXYZ.setTextColor(Color.WHITE);
        newRow.addView(labelXYZ);


        View v2 = new View(context);
        v2.setLayoutParams(new TableRow.LayoutParams(5, TableRow.LayoutParams.MATCH_PARENT));
        v2.setBackgroundColor(Color.rgb(150, 50, 150));
        newRow.addView(v2);

        TextView labelGPS = new TextView(context);
        labelGPS.setId(300 + count);
        labelGPS.setText(img.getLatitude() + " " + img.getLongitude());
        labelGPS.setTextColor(Color.WHITE);


        newRow.setGravity(Gravity.LEFT);
        newRow.addView(labelGPS);

// finally add this to the table row
        mainTable.addView(newRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        count++;
    }

}
