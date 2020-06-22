# MaskView
Small library for creating masks using vector drawable.  Useful for creating camera document mask.

# Example
 - Phone left and preview at the right.
 - Run the example project to try it out. It also includes cameraX.
<p align="center"><img src="https://i.imgur.com/O61IhNE.png"/></p>


# Usage
```groovy
// build.gradle
dependencies {
    //TODO
    compile 'com.github.xpenatan:maskview:1.0.0'
}
```

```xml
<com.github.xpenatan.maskview.MaskView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:mv_backgroundColor="#AA000000"
    app:mv_pathView="@+id/myPathView" />

<com.github.xpenatan.maskview.PathView
    android:id="@+id/myPathView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:pv_previewColor="#FFFFFFFF"
    app:pv_scaleType="center"
    app:pv_vectorDrawable="@drawable/ic_mask_vector" />
```

MaskView attributes:
 * `mv_backgroundColor` Color of the background where the mask will be applied
 * `mv_pathView` The id of PathView which is required to apply the mask

 PathView attributes:
 * `pv_previewColor` Color to show only in preview window
 * `pv_scaleType` Scale type similar to ImageView scale type
 * `pv_vectorDrawable` The vector drawable to apply the mask shape

# Limitations
The parser only support simple drawable vector with only a single path
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="309.68265dp"
    android:height="481.29846dp"
    android:viewportWidth="309.68265"
    android:viewportHeight="481.29846">
  <path
      android:fillColor="#FF000000"
      android:pathData="M19.325,0L290.358,0A19.325,19.325 0,0 1,309.683 19.325L309.683,461.974A19.325,19.325 0,0 1,290.358 481.298L19.325,481.298A19.325,19.325 0,0 1,0 461.974L0,19.325A19.325,19.325 0,0 1,19.325 0z"
      android:strokeWidth="0.72418666"/>
</vector>
```