package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.navigationViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.google.android.material.tabs.TabLayout", "UISegmentedControlSquare", "View") { node ->
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
//                setToColor(node, "app:tabBackground") {
//                }
//                setToColor(node, "app:tabRippleColor") {
//                }
                if(!setToColor(node, "app:tabTextColor"){ it, s ->
                        appendln(
                            """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .selected
                )"""
                        )
                        appendln(
                            """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .normal
                )"""
                        )
                        appendln("view.materialTabStyle(color: $it)")
                }) {
                    val it = "UIColor.black"
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .selected
                )"""
                    )
                    appendln(
                        """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .normal
                )"""
                    )
                    appendln("view.materialTabStyle(color: $it)")
                }
            },
            ViewType(
                "com.lightningkite.butterfly.views.android.PreviewVariedFlipper",
                "PreviewVariedFlipper",
                "FrameLayout"
            ) {},
            ViewType("androidx.recyclerview.widget.RecyclerView", "UICollectionView", "View",
                iosConstructor = "UICollectionView(frame: .zero, collectionViewLayout: UICollectionView.ReversibleFlowLayout())") { node ->
//                appendln("view.separatorStyle = .none")
                if(node.allAttributes["android:background"]?.substringBefore('/')?.contains("color") != true){
                    appendln("view.backgroundColor = UIColor.clear")
                }
            },
            ViewType("com.lightningkite.butterfly.views.android.VerticalRecyclerView", "UICollectionView", "androidx.recyclerview.widget.RecyclerView",
                iosConstructor = "UICollectionView(frame: .zero, collectionViewLayout: UICollectionView.ReversibleFlowLayout())") { node ->
//                val pos = (node.allAttributes.get("app:dividerPositions")?.split('|') ?: node.allAttributes.get("dividerPositions")?.split('|')) ?: listOf()
//                when{
//                    pos.contains("start") -> appendln("//Separator position 'start' not supported yet")
//                    pos.contains("between") -> appendln("view.separatorStyle = .singleLine")
//                    pos.contains("end") -> appendln("//Separator position 'end' not supported yet")
//                    else -> appendln("view.separatorStyle = .none")
//                }
//
//                setToColor(node, "app:dividerColor"){ it, s ->
//                    appendln("view.separatorColor = $it")
//                } || setToColor(node, "dividerColor"){ it, s ->
//                    appendln("view.separatorColor = $it")
//                }
//                (node.attributeAsSwiftDimension("app:dividerSize") ?: node.attributeAsSwiftDimension("dividerSize"))?.let {
//                    appendln("//It is not possible to have a different divider size currently, though requested.")
//                }
//                (node.attributeAsSwiftDimension("app:dividerHorizontalPadding") ?: node.attributeAsSwiftDimension("dividerHorizontalPadding") ?: "0").let {
//                    appendln("view.separatorInset = UIEdgeInsets(top: 0, left: $it, bottom: 0, right: $it)")
//                }
            },
            ViewType("com.rd.PageIndicatorView", "UIPageControl", "View") { node ->
                setToColor(node, "app:piv_selectedColor"){ it, s ->
                    appendln("view.currentPageIndicatorTintColor = $it")
                }
                setToColor(node, "app:piv_unselectedColor"){ it, s ->
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
