package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*
import kotlin.math.PI

val LayoutConverter.Companion.navigationViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.google.android.material.tabs.TabLayout", "UISegmentedControl", "View") { node ->
                appendln("view.tintColor = .clear")
                appendln("view.backgroundColor = .clear")

                node.attributes["app:tabMode"]?.let {
                    when (it) {
                        "scrollable" -> appendln("view.apportionsSegmentWidthsByContent = true")
                        "fixed" -> appendln("view.apportionsSegmentWidthsByContent = false")
                        else -> appendln("view.apportionsSegmentWidthsByContent = false")
                    }
                }
                node.attributes["app:tabGravity"]?.let {
                    when (it) {
                        "center" -> appendln("view.apportionsSegmentWidthsByContent = true")
                        "fill" -> appendln("view.apportionsSegmentWidthsByContent = false")
                        else -> appendln("view.apportionsSegmentWidthsByContent = false")
                    }
                }
//                node.setToColor("app:tabBackground") {
//                }
//                node.setToColor("app:tabRippleColor") {
//                }
                (node.attributeAsColor("app:tabTextColor") ?: "UIColor.black").let {
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .normal
                )"""
                    )
                }
                (node.attributeAsColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .selected
                )"""
                    )
                }
                (node.attributeAsColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                    appendln("view.addIndicator(color: $it)")
                }
            },
            ViewType(
                "com.lightningkite.kwift.views.android.PreviewVariedFlipper",
                "PreviewVariedFlipper",
                "FrameLayout"
            ) {},
            ViewType("androidx.recyclerview.widget.RecyclerView", "UITableView", "View") {},
            ViewType("com.rd.PageIndicatorView", "UIPageControl", "View") { node ->
                node.attributeAsColor("app:piv_selectedColor")?.let {
                    appendln("view.currentPageIndicatorTintColor = $it")
                }
                node.attributeAsColor("app:piv_unselectedColor")?.let {
                    appendln("view.pageIndicatorTintColor = $it")
                }
            },
            ViewType(
                "androidx.viewpager.widget.ViewPager",
                "UICollectionView",
                "View",
                iosConstructor = "UICollectionView(frame: .zero, collectionViewLayout: ViewPagerLayout())"
            ) { node ->

                appendln("view.canCancelContentTouches = false")
                appendln("view.showsHorizontalScrollIndicator = false")
                appendln("view.backgroundColor = .clear")
            },
            ViewType("com.google.android.gms.maps.MapView", "MKMapView", "View") { node ->

            }
        ),
        skipTypes = listOf(
            "android.support.v4.widget.SwipeRefreshLayout",
            "SwipeRefreshLayout"
        )
    )
