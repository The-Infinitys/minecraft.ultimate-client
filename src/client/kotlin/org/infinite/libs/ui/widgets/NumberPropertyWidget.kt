package org.infinite.libs.ui.widgets

import org.infinite.libs.core.features.property.NumberProperty

class NumberPropertyWidget<T>(
    x: Int,
    y: Int,
    width: Int,
    height: Int = DEFAULT_WIDGET_HEIGHT,
    property: NumberProperty<T>,
) : PropertyWidget<NumberProperty<T>>(x, y, width, height, property)
    where T : Number, T : Comparable<T>
