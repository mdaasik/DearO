package com.carworkz.dearo.predeliverycheck

import com.carworkz.dearo.domain.entities.ChecklistItem
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class ChecklistItemPOJO(title: String?, items: MutableList<ChecklistItem>?) :
    ExpandableGroup<ChecklistItem>(title, items)