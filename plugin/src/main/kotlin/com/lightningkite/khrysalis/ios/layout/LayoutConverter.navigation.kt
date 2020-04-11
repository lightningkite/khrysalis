package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.navigationViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.google.android.material.tabs.TabLayout", "UISegmentedControl", "View") { node ->
                appendln("view.tintColor = .clear")
                appendln("view.backgroundColor = .clear")

                node.allAttributes["app:tabMode"]?.let {
                    when (it) {
                        "scrollable" -> appendln("view.apportionsSegmentWidthsByContent = true")
                        "fixed" -> appendln("view.apportionsSegmentWidthsByContent = false")
                        else -> appendln("view.apportionsSegmentWidthsByContent = false")
                    }
                }
                node.allAttributes["app:tabGravity"]?.let {
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
                (node.attributeAsSwiftColor("app:tabTextColor") ?: "UIColor.black").let {
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .normal
                )"""
                    )
                }
                (node.attributeAsSwiftColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .selected
                )"""
                    )
                }
                (node.attributeAsSwiftColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                    appendln("view.addIndicator(color: $it)")
                }
            },
            ViewType(
                "com.lightningkite.khrysalis.views.android.PreviewVariedFlipper",
                "PreviewVariedFlipper",
                "FrameLayout"
            ) {},
            ViewType("androidx.recyclerview.widget.RecyclerView", "UITableView", "View") {
                appendln("view.separatorStyle = .none")
            },
            ViewType("com.lightningkite.khrysalis.views.android.VerticalRecyclerView", "UITableView", "androidx.recyclerview.widget.RecyclerView") { node ->
                val pos = (node.allAttributes.get("app:dividerPositions")?.split('|') ?: node.allAttributes.get("dividerPositions")?.split('|')) ?: listOf()
                when{
                    pos.contains("start") -> appendln("//Separator position 'start' not supported yet")
                    pos.contains("between") -> appendln("view.separatorStyle = .singleLine")
                    pos.contains("end") -> appendln("//Separator position 'end' not supported yet")
                    else -> appendln("view.separatorStyle = .none")
                }

                (node.attributeAsSwiftColor("app:dividerColor") ?: node.attributeAsSwiftColor("dividerColor"))?.let {
                    appendln("view.separatorColor = $it")
                }
                (node.attributeAsSwiftDimension("app:dividerSize") ?: node.attributeAsSwiftDimension("dividerSize"))?.let {
                    appendln("//It is not possible to have a different divider size currently, though requested.")
                }
                (node.attributeAsSwiftDimension("app:dividerHorizontalPadding") ?: node.attributeAsSwiftDimension("dividerHorizontalPadding") ?: "0").let {
                    appendln("view.separatorInset = UIEdgeInsets(top: 0, left: $it, bottom: 0, right: $it)")
                }
                if(node.allAttributes["android:background"] == null){
                    appendln("view.backgroundColor = UIColor.clear")
                }
            },
            ViewType("com.rd.PageIndicatorView", "UIPageControl", "View") { node ->
                node.attributeAsSwiftColor("app:piv_selectedColor")?.let {
                    appendln("view.currentPageIndicatorTintColor = $it")
                }
                node.attributeAsSwiftColor("app:piv_unselectedColor")?.let {
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
            }
        ),
        skipTypes = listOf(
            "androidx.swiperefreshlayout.widget.SwipeRefreshLayout",
            "android.support.v4.widget.SwipeRefreshLayout",
            "SwipeRefreshLayout"
        )
    )
