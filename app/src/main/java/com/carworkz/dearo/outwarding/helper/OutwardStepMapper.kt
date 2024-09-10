package com.carworkz.dearo.outwarding.helper

import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*

class OutwardStepMapper
{

    companion object
    {
        const val TITLE_LABOURS = "Labours"
        const val TITLE_PARTS = "Parts"
        const val TITLE_PACKAGES = "Service Packages"
        const val TITLE_PROFORMA_SUMMARY = "Summary"
        const val TITLE_ESTIMATOR_SUMMARY = "Estimate Details"
        const val TITLE_SPLIT_INVOICE_SUMMARY = "Split Details"
        const val TITLE_SPLIT_OTHER_CHARGES = "Other Charges"
        const val TITLE_THIRD_PARTY = "Third Party Details"

        fun costEstimateToOutwardStep(costEstimate: CostEstimate, packages: List<ServicePackage>?,isFromCustomerApprovalActivity:Boolean): ArrayList<OutwardStep>
        {
            val list = arrayListOf<OutwardStep>()
            var labourTotal = 0.0
            var partsTotal = 0.0
            var servicePackageTotal = 0.0
            //LABOUR
            list.add(OutwardSection("$TITLE_LABOURS(${costEstimate.labours?.size ?: 0})", 0.0))
            if (costEstimate.labours?.isNotEmpty() == true)
            {
                costEstimate.labours?.forEach {
                    val tax: Float = if (SharedPrefHelper.isGstEnabled()) ((it.tax.cgst + it.tax.sgst).toFloat() / 100.0f) else 0.0f // 0.18
                    val itemSubTotal = it.finalRate
                    val itemDiscountAmount = if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else (itemSubTotal * (it.discount.amount / 100)))
                    val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
                    val itemTaxAmount = if (SharedPrefHelper.isGstEnabled() && tax > 0.0) (itemAmountAfterDiscount * tax) else 0.0

                    labourTotal += itemAmountAfterDiscount + itemTaxAmount
                }
                list.find { it is OutwardSection && it.title.contains(TITLE_LABOURS, true) }.let {
                    it as OutwardSection
                    it.total = labourTotal
                }
                costEstimate.labours?.forEach {
                    list.add(labourToOutwardStep(it))
                }
            }
            //PARTS
            list.add(OutwardSection("$TITLE_PARTS(${costEstimate.parts?.size ?: 0})", 0.0))
            if (costEstimate.parts?.isNotEmpty() == true)
            {
                costEstimate.parts?.forEach {
                    val tax: Float = if (SharedPrefHelper.isGstEnabled()) ((it.tax.cgst + it.tax.sgst).toFloat() / 100.0f) else 0.0f // 0.18
                    val rate = it.price // price = 22.00 rate = 18.644
                    it.rate = rate
                    val itemSubTotal = rate  * it.quantity
                    val itemDiscountAmount = if (it.discount.amount == 0.0) 0.0 else (if (it.discount.mode == Discount.MODE_PRICE) it.discount.amount else (itemSubTotal * (it.discount.amount / 100)))
                    val itemAmountAfterDiscount = itemSubTotal - itemDiscountAmount
                    val itemTaxAmount = if (SharedPrefHelper.isGstEnabled() && tax > 0.0) (itemAmountAfterDiscount * tax) else 0.0

                    partsTotal += itemAmountAfterDiscount //+ itemTaxAmount

//                    partsTotal += it.price * it.quantity
                }

                list.find { it is OutwardSection && it.title.contains(TITLE_PARTS, true) }.let {
                    it as OutwardSection
                    it.total = partsTotal
                }
                costEstimate.parts?.forEach {
                    list.add(partToOutwardStep(it))
                }
            }


            if(isFromCustomerApprovalActivity.not()) {
                //Add Service package Parts and labours
                if (SharedPrefHelper.isPackagesEnabled())
                {
                    list.add(OutwardSection("$TITLE_PACKAGES(${packages?.size})", 0.0))
                    if (packages?.isNotEmpty() == true)
                    {
                        packages.forEach { servicePackage ->
                            //iterating each package
                            servicePackageTotal += servicePackage.amount

                            //Adding package for tittle only
                            val item = packageToOutwardStep(servicePackage)
                            list.add(item)

                            //Add Labours
                            if (servicePackage.labours?.isNotEmpty() == true)
                            {
                                servicePackage.labours?.forEach { labour ->
                                    list.add(labourToOutwardStep(labour).also { it.type = OutwardItem.TYPE_SER_PKG_LABOUR }.also { it.packageId = servicePackage.id })
                                }
                            }

                            //Add Parts
                            if (servicePackage.parts?.isNotEmpty() == true)
                            {
                                servicePackage.parts?.forEach {
                                    list.add(partToOutwardStep(it, item.id!!))
                                }
                            }
                        }
                        list.find { it is OutwardSection && it.title.contains(TITLE_PACKAGES, true) }.let {
                            it as OutwardSection
                            it.total = servicePackageTotal
                        }
                    }
                }


                list.add(OutwardSection(TITLE_ESTIMATOR_SUMMARY, null))
                list.add(
                    OutwardSummary(
                        TITLE_ESTIMATOR_SUMMARY,
                        partsTotal,
                        labourTotal,
                        servicePackageTotal,
                        partsTotal + labourTotal + servicePackageTotal
                    )
                )
            }
            return list
        }


        fun outwardStepToCostEstimate(items: List<OutwardItem>): CostEstimate
        {
            val costEstimate = CostEstimate()
            val labours = items.filter { it.type == OutwardItem.TYPE_LABOUR }
            if (labours.isNotEmpty())
            {
                costEstimate.labours = mutableListOf()
                labours.forEach {
                    val labour = Labour()
                    labour.id = it.id
                    labour.uid = it.uid
                    labour.tax = it.tax
                    labour.price = it.rate
                    labour.rate = it.rate
                    labour.quantity = it.quantity.toInt()
                    labour.text = it.text
                    labour.brand = it.brand
                    labour.discount = it.discount
                    labour.source = it.source
                    labour.vehicleType = it.vehicleType
                    labour.surcharge = it.surcharge
                    labour.reduction = it.reduction
                    labour.amount = it.amount ?: 0.0
                    labour.labourType = it.labourType
                    labour.finalRate = it.finalRate
                    labour.isApproved=it.isApproved.let { if(it==null) true else it==true }
                    labour.vendor = it.vendor
                    costEstimate.labours!!.add(labour)
                }
            }

            val parts = items.filter { it.type == OutwardItem.TYPE_PART }
            if (parts.isNotEmpty())
            {
                costEstimate.parts = mutableListOf()
                parts.forEach {
                    val part = Part()
                    part.id = it.id
                    part.uid = it.uid
                    part.tax = it.tax
                    part.price = it.price
                    part.description = it.description
                    part.partNumber = it.partNumber
                    part.brand = it.brand
                    part.rate = it.price
                    part.unit = it.unit
                    part.quantity = it.quantity
                    part.text = it.text
                    part.discount = it.discount
                    part.source = it.source
                    part.vehicleType = it.vehicleType
                    part.amount = it.amount ?: 0.0
                    part.isApproved=it.isApproved.let { if(it==null) true else it==true }
                    costEstimate.parts!!.add(part)
                }
            }

            val servPackage = items.filter { it.type == OutwardItem.TYPE_SERVICE_PKG }
            if (servPackage.isNotEmpty())
            {
                costEstimate.packages = mutableListOf()
                servPackage.forEach { sp ->
                    //here we need to separate parts and labours for service package
                    val servPackageParts = items.filter { it.type == OutwardItem.TYPE_SER_PKG_PART && it.packageId == sp.id }
                    val servPackageLabours = items.filter { it.type == OutwardItem.TYPE_SER_PKG_LABOUR && it.packageId == sp.id }

                    val serPackage = outwardStepToPackage(sp, servPackageParts, servPackageLabours)
                    costEstimate.packages!!.add(serPackage)
                }
            }

            return costEstimate
        }

        fun partToOutwardStep(part: Part): OutwardItem
        {
            val item = OutwardItem()
            item.id = part.id
            item.hsn = part.hsn
            item.taxPercent = part.taxPercent
            item.discountAmount = part.discountAmount
            item.uid = part.uid
            item.amount = part.amount
            item.price = part.price
            item.partNumber = part.partNumber
            item.description = part.description
            item.rate = part.rate
            item.quantity = part.quantity
            item.units = part.units as ArrayList<String>?
            item.unit = part.unit
            item.text = part.text
            item.tax = part.tax
            item.brand = part.brand
            item.discount = part.discount
            item.source = part.source
            item.stock = part.stock?.toDouble()!!
            item.split = part.split
            item.vehicleType = part.vehicleType
            item.type = OutwardItem.TYPE_PART
            if (SharedPrefHelper.isCompositeEnabled())
            {
                item.tax.cgst = 3.0
                item.tax.sgst = 3.0
            }
//            if (part.finalRate == 0.0)
//            {
//                part.finalRate = part.price
//            }
            item.finalRate = part.price
            item.isApproved = part.isApproved.let { if(it==null) true else it==true }
            item.isFOC=part.isFOC
            return item
        }

        fun partToOutwardStep(part: ServicePackagesParts, packageId: String): OutwardItem
        {
            val item = OutwardItem()
            item.id = part.part.id
            item.hsn = part.part.hsn
            item.packageId = packageId
//            item.taxPercent = part.taxPercent
//            item.discountAmount = part.discountAmount
//            item.uid = part.uid
//            item.amount = part.amount
//            item.price = part.price
            item.partNumber = part.partNumber
            item.description = part.description
//            item.rate = part.rate
            item.quantity = part.quantity
//            item.units = part.units as ArrayList<String>?
//            item.unit = part.unit
            item.text = part.part.text
//            item.tax = part.tax
            item.brand = part.brand
//            item.discount = part.discount
//            item.source = part.source
            item.stock = part.stock.toDouble()
//            item.split = part.split
//            item.vehicleType = part.vehicleType
            item.type = OutwardItem.TYPE_SER_PKG_PART
            /*   if (SharedPrefHelper.isCompositeEnabled())
               {
                   item.tax.cgst = 3.0
                   item.tax.sgst = 3.0
               }*/
            item.partNumbers = part.partNumbers
            return item
        }

        fun labourToOutwardStep(labour: Labour): OutwardItem
        {
            val item = OutwardItem()
            item.discountAmount = labour.discountAmount
            item.taxPercent = labour.taxPercent
            item.id = labour.id
            item.uid = labour.uid
            item.amount = labour.amount
            item.price = labour.price
            item.brand = labour.brand
            item.rate = labour.rate
            item.quantity = labour.quantity.toFloat()
            item.text = labour.text
            item.tax = labour.tax
            item.tax.sac = "998729"
            item.tax.sgst = 9.0
            item.tax.cgst = 9.0
            item.source = labour.source
            item.discount = labour.discount
            item.split = labour.split
            item.vehicleType = labour.vehicleType
            item.surcharge = labour.surcharge
            item.labourType = labour.labourType
            item.type = OutwardItem.TYPE_LABOUR
            item.reduction = labour.reduction
            if (SharedPrefHelper.isCompositeEnabled())
            {
                item.tax.cgst = 3.0
                item.tax.sgst = 3.0
            }
            if (labour.finalRate == 0.0)
            {
                labour.finalRate = labour.rate
            }
            item.finalRate = labour.finalRate
            item.vendor = labour.vendor
            item.isApproved = labour.isApproved.let { if(it==null) true else it==true }
            item.isFOC=labour.isFOC
            return item
        }

        fun invoiceToOutwardStep(invoice: Invoice): ArrayList<OutwardStep>
        {
            val list = arrayListOf<OutwardStep>()

            //Labours [MAIN]
            list.add(OutwardSection("$TITLE_LABOURS(${invoice.labours?.size ?: 0})", 0.0))
            if (invoice.labours?.isNotEmpty() == true)
            {
                var total = 0.0
                invoice.labours?.forEach { total += it.amount!! }
                list.find { it is OutwardSection && it.title.contains(TITLE_LABOURS, true) }.let {
                    it as OutwardSection
                    it.total = total
                }
                invoice.labours?.forEach {
                    list.add(labourToOutwardStep(it))
                }
            }
            //PARTS [MAIN}
            list.add(OutwardSection("$TITLE_PARTS(${invoice.parts?.size ?: 0})", 0.0))
            if (invoice.parts?.isNotEmpty() == true)
            {
                var total = 0.0
                invoice.parts?.forEach { total += it.amount }
                list.find { it is OutwardSection && it.title.contains(TITLE_PARTS, true) }.let {
                    it as OutwardSection
                    it.total = total
                }
                invoice.parts?.forEach {
                    list.add(partToOutwardStep(it))
                }
            }

            if (invoice.packages?.isNotEmpty() == true)
            {
                var servicePackageTotal = 0.0

                list.add(OutwardSection("$TITLE_PACKAGES(${invoice.packages?.size ?: 0})", 0.0))
                if (invoice.packages?.isNotEmpty() == true)
                {
                    invoice.packages?.forEach { servicePackage ->
                        //iterating each package
                        servicePackageTotal += servicePackage.amount

                        //Adding package for tittle only
                        val item = packageToOutwardStep(servicePackage)
                        list.add(item)

                        //Add Labours
                        if (servicePackage.labours?.isNotEmpty() == true)
                        {
                            servicePackage.labours?.forEach { labour ->
                                list.add(labourToOutwardStep(labour).also { it.type = OutwardItem.TYPE_SER_PKG_LABOUR }.also { it.packageId = servicePackage.id })
                            }
                        }

                        //Add Parts
                        if (servicePackage.parts?.isNotEmpty() == true)
                        {
                            servicePackage.parts?.forEach {
                                list.add(partToOutwardStep(it, item.id!!))
                            }
                        }
                    }
                    list.find { it is OutwardSection && it.title.contains(TITLE_PACKAGES, true) }.let {
                        it as OutwardSection
                        it.total = servicePackageTotal
                    }
                }
            }
            //End Package


            //Proforma Summary
            list.add(OutwardSection(TITLE_PROFORMA_SUMMARY, null))
            list.add(OutwardSummary(TITLE_PROFORMA_SUMMARY, invoice.summary?.totalAmountBeforeTax, invoice.summary?.totalTaxAmount, invoice.summary?.totalDiscount, invoice.summary?.totalAmountAfterTax))
            list.add(OutwardSection(TITLE_THIRD_PARTY, null))
            list.add(OutwardActionItem(invoice.thirdPartyDetails?.apply {
                isThirdParty = invoice.isThirdParty
            }))
            return list
        }

        fun invoiceToSplitOutwardStep(invoice: Invoice): ArrayList<OutwardStep>
        {
            var insuranceAmount = 0.0
            var customerAmount = 0.0
            val estimationList = arrayListOf<OutwardStep>()
            if (invoice.labours?.isNotEmpty() == true)
            {
                estimationList.add(OutwardSection("$TITLE_LABOURS(${invoice.labours?.size
                    ?: 0})", 0.0))
                var total = 0.0
                invoice.labours?.forEach { total += it.amount!! }

                estimationList.find { it is OutwardSection && it.title.contains(TITLE_LABOURS, true) }.let {
                    it as OutwardSection
                    it.total = total
                }
                invoice.labours?.forEach {
                    val labour = labourToOutwardStep(it)
                    labour.split = it.split
                    customerAmount += it.split?.customerPay?.amount ?: 0.0
                    insuranceAmount += it.split?.insurancePay?.amount ?: 0.0
                    labour.type = OutwardItem.TYPE_SPLIT_LABOUR
                    estimationList.add(labour)
                }
            }
            if (invoice.parts?.isNotEmpty() == true)
            {
                estimationList.add(OutwardSection("$TITLE_PARTS(${invoice.parts?.size ?: 0})", 0.0))
                var total = 0.0
                invoice.parts?.forEach { total += it.amount }
                estimationList.find { it is OutwardSection && it.title.contains(TITLE_PARTS, true) }.let {
                    it as OutwardSection
                    it.total = total
                }
                invoice.parts?.forEach {
                    val part = partToOutwardStep(it)
                    part.split = it.split
                    customerAmount += it.split?.customerPay?.amount ?: 0.0
                    insuranceAmount += it.split?.insurancePay?.amount ?: 0.0
                    part.type = OutwardItem.TYPE_SPLIT_PART
                    estimationList.add(part)
                }
            }
            if (invoice.packages?.isNotEmpty() == true)
            {
                invoice.packages?.forEach {
                    estimationList.add(packageToOutwardStep(it))
                }
            }
            estimationList.add(OutwardSection(TITLE_SPLIT_OTHER_CHARGES, 0.0))
            val otherChangesItem = OutwardItem()
            if (SharedPrefHelper.isCompositeEnabled())
            {
                otherChangesItem.tax.cgst = 3.0
                otherChangesItem.tax.sgst = 3.0
            }
            otherChangesItem.salvageValue = invoice.salvageValue ?: 0.0
            otherChangesItem.excessClauseValue = invoice.excessClauseValue ?: 0.0
            otherChangesItem.type = OutwardItem.TYPE_SPLIT_OTHER_CHARGES
            estimationList.add(otherChangesItem)
            return estimationList
        }

        fun packageToOutwardStep(servicePackage: ServicePackage): OutwardItem
        {
            val item = OutwardItem()
            item.id = servicePackage.id
            // item.packageId = servicePackage.packageId
            // item.code = servicePackage.code
            // item.category = servicePackage.category
            // item.ownerId = servicePackage.ownerId
            // item.ownerType = servicePackage.ownerType
            item.name = servicePackage.name
            item.description = servicePackage.description
            // item.attributes = servicePackage.attributes
            // item.services = servicePackage.services
            // item.hsn = servicePackage.hsn
            // item.makeSlug = servicePackage.makeSlug
            // item.modelSlug = servicePackage.modelSlug
            // item.fuelType = servicePackage.fuelType
            // item.engineOilType = servicePackage.engineOilType
            // item.offerPrice = servicePackage.offerPrice
            // item.actualPrice = servicePackage.actualPrice
            item.amount = servicePackage.amount
            item.pkgTaxableAmount = servicePackage.taxableAmount
            if (servicePackage.tax != null)
            {
                item.tax = servicePackage.tax!!
            }
            // item.time = servicePackage.time
            if (servicePackage.parts != null)
            {
                item.parts = servicePackage.parts as ArrayList<ServicePackagesParts>
            }
            item.rates = servicePackage.rates
            // item.isSelected = servicePackage.isSelected
            item.type = OutwardItem.TYPE_SERVICE_PKG
            item.isApproved=servicePackage.isApproved
            return item
        }

        fun outwardStepToPackage(item: OutwardItem, servPackageParts: List<OutwardItem>, servPackageLabours: List<OutwardItem>): ServicePackage
        {
            val servicePackage = ServicePackage()
            servicePackage.id = item.id
            servicePackage.name = item.name
            servicePackage.description = item.description
            servicePackage.amount = item.amount!!
            servicePackage.taxableAmount = item.pkgTaxableAmount
            servicePackage.tax = item.tax

            //add all parts into service package
            val parts = mutableListOf<ServicePackagesParts>()
            servPackageParts.forEach {
                parts.add(outwardStepToSerPkgPart(it))
            }
            servicePackage.parts = parts

            //add all labours into service package
            val labours = mutableListOf<Labour>()
            servPackageLabours.forEach {
                labours.add(outwardStepToLabour(it))
            }
            servicePackage.labours = labours


            servicePackage.rates = item.rates
//            servicePackage.isSelected=item.isSelected
            item.type = OutwardItem.TYPE_SERVICE_PKG
            item.isApproved=servicePackage.isApproved
            return servicePackage
        }

        fun outwardStepToPart(item: OutwardItem): Part
        {
            val part = Part()
            part.id = item.id
            part.uid = item.uid
            part.amount = item.amount ?: 0.0
            part.price = item.price
            part.description = item.description
            part.partNumber = item.partNumber
            part.brand = item.brand
            part.rate = item.rate
            part.unit = item.unit
            part.quantity = item.quantity
            part.text = item.text
            part.tax = item.tax
            part.source = item.source
            part.discount = item.discount
            part.split = item.split
            part.vehicleType = item.vehicleType
            part.isApproved=item.isApproved.let { if(it==null) true else it==true }
            part.isFOC=item.isFOC
            return part
        }

        fun outwardStepToSerPkgPart(item: OutwardItem): ServicePackagesParts
        {
            val part = ServicePackagesParts(item.packageId, item.id, item.brand.id, item.partNumber, item.quantity, "", Part(), item.brand, item.stock.toFloat(), "", "", item.partNumbers,item.description)
//            part.id = item.id
//            part.uid = item.uid
//            part.amount = item.amount ?: 0.0
//            part.price = item.price
            part.partNumber = item.partNumber
            part.brand = item.brand
//            part.rate = item.rate
//            part.unit = item.unit
            part.quantity = item.quantity
//            part.text = item.text
//            part.tax = item.tax
//            part.source = item.source
//            part.discount = item.discount
//            part.split = item.split
//            part.vehicleType = item.vehicleType
            part.part = Part().apply {
                id = item.id
                text = item.text
                description = item.description
            }
            return part
        }

        fun outwardStepToLabour(item: OutwardItem): Labour
        {
            val labour = Labour()
            labour.id = item.id
            labour.uid = item.uid
            labour.amount = item.amount ?: 0.0
            labour.price = item.price
            labour.rate = item.rate
            labour.brand = item.brand
            labour.text = item.text
            labour.tax = item.tax
            labour.source = item.source
            labour.discount = item.discount
            labour.split = item.split
            labour.vehicleType = item.vehicleType
            labour.surcharge = item.surcharge
            labour.labourType = item.labourType
            labour.finalRate = item.finalRate
            labour.vendor = item.vendor
            labour.isApproved=item.isApproved.let { if(it==null) true else it==true }
            labour.isFOC=item.isFOC
            return labour
        }

        fun outwardStepToInvoice(items: List<OutwardStep>): Invoice
        {
            val invoice = Invoice()
            //Main labours
            val labours: List<OutwardItem> = items.asSequence().filter { it is OutwardItem && (it.type == OutwardItem.TYPE_LABOUR || it.type == OutwardItem.TYPE_SPLIT_LABOUR) }.map { it as OutwardItem }.toList()
            if (labours.isNotEmpty())
            {
                invoice.labours = mutableListOf()
                labours.forEach {
                    val labour = Labour()
                    labour.id = it.id
                    labour.uid = it.uid
                    labour.tax = it.tax
                    labour.price = it.rate//changed on 22 sep by mehul for estimate with GST (API returns rate same as price)
                    labour.rate = it.rate
                    labour.quantity = it.quantity.toInt()
                    labour.text = it.text
                    labour.source = it.source
                    labour.amount = it.amount ?: 0.0
                    labour.discount = it.discount
                    labour.split = it.split
                    labour.vehicleType = it.vehicleType
                    labour.surcharge = it.surcharge
                    labour.reduction = it.reduction
                    labour.labourType = it.labourType
                    labour.finalRate = it.finalRate
                    labour.vendor = it.vendor
                    labour.isApproved=it.isApproved
                    labour.isFOC=it.isFOC
                    invoice.labours!!.add(labour)
                }
            }

            //Main parts
            val parts: List<OutwardItem> = items.asSequence().filter { it is OutwardItem && (it.type == OutwardItem.TYPE_PART || it.type == OutwardItem.TYPE_SPLIT_PART) }.map { it as OutwardItem }.toList()
            if (parts.isNotEmpty())
            {
                invoice.parts = mutableListOf()
                parts.forEach {
                    val part = Part()
                    part.id = it.id
                    part.uid = it.uid
                    if (it.tax.hsn.equals(HSN.SELECT))
                    {
                        it.tax.hsn = null
                    }
                    part.tax = it.tax
                    part.price = it.price
                    part.rate = it.rate
                    part.quantity = it.quantity
                    part.source = it.source
                    part.text = it.text
                    part.partNumber = it.partNumber
                    part.amount = it.amount ?: 0.0
                    part.discount = it.discount
                    part.description = it.description
                    part.units = it.units
                    part.unit = it.unit
                    part.brand = it.brand
                    part.split = it.split
                    part.vehicleType = it.vehicleType
                    part.isApproved=it.isApproved
                    part.isFOC=it.isFOC
                    invoice.parts!!.add(part)
                }
            }

            //---------------------------------------------------------------------

            val servPackage = items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }

            if (servPackage.isNotEmpty())
            {
                invoice.packages = mutableListOf()
                servPackage.forEach { sp ->
                    sp as OutwardItem
                    //here we need to separate parts and labours for service package
                    val servPackageParts = items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SER_PKG_PART && it.packageId == sp.id }
                    val servPackageLabours = items.filter { it is OutwardItem && it.type == OutwardItem.TYPE_SER_PKG_LABOUR && it.packageId == sp.id }

                    val serPackage = outwardStepToPackage(sp, servPackageParts as List<OutwardItem>, servPackageLabours as List<OutwardItem>)
                    invoice.packages!!.add(serPackage)
                }
            }


            val servicePkg: List<OutwardItem> = items.asSequence().filter { it is OutwardItem && it.type == OutwardItem.TYPE_SERVICE_PKG }.map { it as OutwardItem }.toList()
            if (servicePkg.isNotEmpty())
            {
                invoice.packageIds = mutableListOf()
                servicePkg.forEach {
                    invoice.packageIds!!.add(it.id!!)
                }
            }

            //----------------------------------------------------------------------
            val otherCharges: List<OutwardItem> = items.asSequence().filter { it is OutwardItem && it.type == OutwardItem.TYPE_SPLIT_OTHER_CHARGES }.map { it as OutwardItem }.toList()
            otherCharges.forEach {
                invoice.salvageValue = it.salvageValue
                invoice.excessClauseValue = it.excessClauseValue
            }
            //-----------------------------------------------------------------------
            val thirdParty = items.asSequence().filter { it is OutwardActionItem }.map { it as OutwardActionItem }.toList().firstOrNull()
            invoice.thirdPartyDetails = thirdParty?.thirdParty
            //------------------------------------------------------------------------
            return invoice
        }
    }
}