BasicStuffRecyclerview
============

An Android library allowing to add custom dividers between ViewHolders from different viewTypes. Moreover, you can add (custom or not) sticky headers depending on viewTypes. 

![](basicstuffrecyclerview_demo.gif "Demo")

Features
============
* Set dividers decoration between ViewHolders from the same/different viewType (only for LinearLayoutManager).
* Set first/last decorations depending on first/last ViewHolder (only for LinearLayoutManager).
* Set Sticky headers depending on viewTypes and custom them (or not). (only for LinearLayoutManager in vertical orientation).

Usage
============

In your app `build.gradle`:

```
implementation 'com.github.sokarcreative:BasicStuffRecyclerview:$latest_version'
```
In your project `build.gradle`:
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
Example
============
```
// If you want to use dividers
recyclerView.addItemDecoration(LinearItemDecoration(recyclerView.adapter as LinearDividersListener))

// If you want to use sticky headers
recyclerView.addItemDecoration(StickyHeaderLinearItemDecoration(recyclerView.adapter as LinearStickyHeadersListener).also {
    recyclerView.addOnItemTouchListener(it)
})

// Optional but recommended after changing items list in adapter since reordering affects dividers from LinearItemDecoration
// Anyway, no need to call it if you use notifyDataSetChanged()
recyclerView.invalidateItemDecorations()
```
Methods you may override
============
## Dividers
```
@Nullable
@Override
public Drawable getFirstDecoration(int viewType) {
    return super.getFirstDecoration(viewType);
}

@Nullable
@Override
public Drawable getFirstDividerDecoration(int viewType, int previousViewType) {
    return super.getFirstDividerDecoration(viewType, previousViewType);
}

@Nullable
@Override
public Drawable getDividerDecoration(int viewType) {
    return super.getDividerDecoration(viewType);
}

@Nullable
@Override
public Drawable getLastDividerDecoration(int viewType, int nextViewType) {
    return super.getLastDividerDecoration(viewType, nextViewType);
}

@Nullable
@Override
public Drawable getLastDecoration(int viewType) {
    return super.getLastDecoration(viewType);
}

```
## Sticky headers
```
@Override
public boolean isStickyHeader(int viewType) {
    return super.isStickyHeader(viewType);
}

@NotNull
@Override
public View onCreateAndBindStickyView(@NotNull RecyclerView parent, int position) {
    return super.onCreateAndBindStickyView(parent, position);
}

@Override
public void onStickyViewClick(@NotNull RecyclerView parent, int position) {
    super.onStickyViewClick(parent, position);
}
```

License
============

Copyright (C) 2017 sokarcreative
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
