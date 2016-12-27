package com.lelive.iosactionsheetpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lelive.iosactionsheet.IOSActionSheet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                new IOSActionSheet.Builder(MainActivity.this)
                        .otherButtonTitles(new ArrayList<IOSActionSheet.ItemModel>() {
                            {
                                add(new IOSActionSheet.ItemModel("删除" , IOSActionSheet.ItemModel.ITEM_TYPE_WARNING));
                                add(new IOSActionSheet.ItemModel("选择"));
                            }
                        })
                        .titleStr("确定删除?")
                        .subTitleStr("删除后操作不能回退")
                        .itemClickListener(new IOSActionSheet.IActionSheetListener() {
                            @Override
                            public void onActionSheetItemClick(IOSActionSheet actionSheet, int itemPosition, IOSActionSheet.ItemModel itemModel) {
                                Toast.makeText(MainActivity.this, "点击的是第几项 = " + itemPosition, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.button2:
                IOSActionSheet sheet = new IOSActionSheet(MainActivity.this);
                sheet.setOtherButtonTitles(new ArrayList<IOSActionSheet.ItemModel>() {
                    {
                        add(new IOSActionSheet.ItemModel("删除" , IOSActionSheet.ItemModel.ITEM_TYPE_WARNING));
                        add(new IOSActionSheet.ItemModel("选择"));
                    }
                });
                sheet.setTitleStr("确定删除?");
                sheet.setCanceledOnTouchOutside(false);
                sheet.setItemClickListener(new IOSActionSheet.IActionSheetListener() {
                    @Override
                    public void onActionSheetItemClick(IOSActionSheet actionSheet, int itemPosition, IOSActionSheet.ItemModel itemModel) {
                        Toast.makeText(MainActivity.this, "点击的是 == " + itemModel.getItemTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                sheet.show();
                break;
            case R.id.button3:
                new IOSActionSheet.Builder(MainActivity.this)
                        .styleId(R.style.IOSActionSheetStyleCustom).otherButtonTitles(new ArrayList<IOSActionSheet.ItemModel>() {
                            {
                                add(new IOSActionSheet.ItemModel("删除", IOSActionSheet.ItemModel.ITEM_TYPE_WARNING));
                                add(new IOSActionSheet.ItemModel("选择"));
                            }
                        })
                        .cancleTitle("淡定")
                        .haveCancleBtn(true)
                        .titleStr("确定删除?")
                        .subTitleStr("删除后操作不能回退")
                        .show();
                break;
        }
    }
}
