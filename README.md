# IOSActionSheet
Android 仿IOS ActionSheet UI样式 ,通过纯代码实现 ,不用导入额外的图片 ,可定制化能力强

## Screenshot

![](https://github.com/xinle/IOSActionSheet/blob/master/screenshot/Screenshot_1483025140.jpg)

![](https://github.com/xinle/IOSActionSheet/blob/master/screenshot/Screenshot_1483025334.jpg)

<!--![](http://p1.bqimg.com/567571/9e824eccc1fceb97.jpg)

![](http://p1.bqimg.com/567571/7e66d7e7a06a10bb.jpg)-->

## 使用
- 方式 1. 拷贝Libs工程里面的IOSActionSheet 和style.xml到自己的工程里面
- 方式 2. JCenter支持应该就这两天

## 范例

- 使用Builder方式创建

```java
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
```

- 普通方式弹出

```java
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
```

- 使用style改变样式

```java
new IOSActionSheet.Builder(MainActivity.this)
    .styleId(R.style.IOSActionSheetStyleCustom)
    .otherButtonTitles(new ArrayList<IOSActionSheet.ItemModel>() {
        {
            add(new IOSActionSheet.ItemModel("删除", IOSActionSheet.ItemModel.ITEM_TYPE_WARNING));
            add(new IOSActionSheet.ItemModel("选择"));
        }
    })
    .cancleTitle("取消")
    .haveCancleBtn(true)
    .titleStr("确定删除?")
    .subTitleStr("删除后操作不能回退")
    .show();
```

样式文件(其中下面的样式可以不用写全,即希望修改什么就填什么)

```xml
<style name="IOSActionSheetStyleCustom">
    <item name="ias_background">@color/ias_background</item>
    <item name="ias_chooseBackground">@color/colorPrimaryDark</item>

    <item name="ias_titleTextColor">@color/ias_titleTextColor</item>
    <item name="ias_cancelButtonTextColor">@color/color1</item>
    <item name="ias_otherButtonTextColor">@color/color2</item>
    <item name="ias_warningButtonTextColor">@color/color4</item>
    <item name="ias_checkButtonTextColor">@color/ias_checkButtonTextColor</item>

    <item name="ias_titleTextSize">19sp</item>
    <item name="ias_subTitleTextSize">16sp</item>
    <item name="ias_cancleButtonTextSize">20sp</item>
    <item name="ias_otherButtonTextSize">12sp</item>
    <item name="ias_warningButtonTextSize">14sp</item>

    <item name="ias_lineHeight">70dp</item>
    <item name="ias_cancelButtonMarginTop">5dp</item>
    <item name="ias_radius">0dp</item>
    <item name="ias_padding">0dp</item>
</style>
```
