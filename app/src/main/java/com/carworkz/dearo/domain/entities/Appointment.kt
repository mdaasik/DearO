package com.carworkz.dearo.domain.entities

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Created by kush on 24/11/17.
 */
class Appointment() : Parcelable {

    @Json(name = "appointmentId")
    internal var appointmentId: String? = null
    @Json(name = "id")
    internal var id: String? = null
    @Json(name = "workshopId")
    internal var workshopId: String? = null
    @Json(name = "customer")
    internal var customer = Customer()
    @Json(name = "vehicle")
    internal var vehicle = Vehicle()
    @Json(name = "packageIds")
    internal var packageIds: List<String>? = null
    @Json(name = "status")
    internal var status: String? = null
    @Json(name = "history")
    internal var history: AppointmentHistory? = null
    @Json(name = "inventory")
    internal var inventory: List<StockInventory>? = null
    @Json(name = "createdOn")
    internal var createdOn: String? = null
    @Json(name = "updatedOn")
    internal var updatedOn: String? = null
    @Json(name = "packages")
    internal var packages: List<ServicePackage>? = null
    @Json(name = "suggestedPackages")
    internal var suggestedPackages: Packages? = null
    @Json(name = "timeSlots")
    internal var timeSlots: List<TimeSlot>? = null
    @Json(name = "date")
    internal var date: String? = null
    @Json(name = "jobCard")
    internal var jobCard: JobCard? = null
    @Json(name = "address")
    internal var address: Address? = null
    @Json(name = "remarks")
    internal var remarks: String? = null
    @Json(name = "serviceAdvisorId")
    internal var serviceAdvisorId: String? = null
    @Json(name = "source")
    internal var source: String? = null
    @Json(name = "serviceAdvisor")
    internal var serviceAdvisor: WorkshopAdviser? = null
    // will determine whether Service Packages should be Shown or skipped
    internal var skipPage = false

    constructor(parcel: Parcel) : this() {
        appointmentId = parcel.readString()
        id = parcel.readString()
        workshopId = parcel.readString()
        customer = parcel.readParcelable(Customer::class.java.classLoader)!!
        vehicle = parcel.readParcelable(Vehicle::class.java.classLoader)!!
        serviceAdvisor = parcel.readParcelable(WorkshopAdviser::class.java.classLoader)
        packageIds = parcel.createStringArrayList()
        status = parcel.readString()
        inventory = parcel.createTypedArrayList(StockInventory)
        createdOn = parcel.readString()
        updatedOn = parcel.readString()
        packages = parcel.createTypedArrayList(ServicePackage)
        suggestedPackages = parcel.readParcelable(Packages::class.java.classLoader)
        date = parcel.readString()
        jobCard = parcel.readParcelable(JobCard::class.java.classLoader)
        address = parcel.readParcelable(Address::class.java.classLoader)
        skipPage = parcel.readByte() != 0.toByte()
        remarks = parcel.readString()
        serviceAdvisorId = parcel.readString()
        source = parcel.readString()
    }

    fun deepCopy(): Appointment {
        val appointment = Appointment()
        appointment.appointmentId = appointmentId
        appointment.id = id
        appointment.workshopId = workshopId
        appointment.customer = customer
        appointment.vehicle = vehicle.deepCopy()
        appointment.packageIds = packageIds
        appointment.status = status
        appointment.history = history
        appointment.inventory = inventory
        appointment.createdOn = createdOn
        appointment.updatedOn = updatedOn
        appointment.suggestedPackages = suggestedPackages
        appointment.timeSlots = timeSlots
        appointment.date = date
        appointment.jobCard = jobCard
        appointment.skipPage = skipPage
        appointment.address = address
        appointment.remarks = remarks
        appointment.serviceAdvisorId = serviceAdvisorId
        appointment.serviceAdvisor = serviceAdvisor
        return appointment
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appointmentId)
        parcel.writeString(id)
        parcel.writeString(workshopId)
        parcel.writeParcelable(customer, flags)
        parcel.writeParcelable(vehicle, flags)
        parcel.writeParcelable(serviceAdvisor, flags)
        parcel.writeStringList(packageIds)
        parcel.writeString(status)
        parcel.writeTypedList(inventory)
        parcel.writeString(createdOn)
        parcel.writeString(updatedOn)
        parcel.writeTypedList(packages)
        parcel.writeParcelable(suggestedPackages, flags)
        parcel.writeString(date)
        parcel.writeParcelable(jobCard, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeByte(if (skipPage) 1 else 0)
        parcel.writeString(remarks)
        parcel.writeString(serviceAdvisorId)
        parcel.writeString(source)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Appointment> {
        const val STATUS_PAST = "PAST"
        const val STATUS_TODAY = "TODAY"
        const val STATUS_UPCOMING = "UPCOMING"
        const val STATUS_REQUESTED = "REQUESTED"
        const val STATUS_CANCELLED = "CANCELLED"
        const val STATUS_CONFIRMED = "CONFIRMED"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val LIST_IN_PROGRESS = "INPROGRESS"

        override fun createFromParcel(parcel: Parcel): Appointment {
            return Appointment(parcel)
        }

        override fun newArray(size: Int): Array<Appointment?> {
            return arrayOfNulls(size)
        }
    }
    
     interface SOURCE {
        companion object {
            val GO_BUMPER : String = "GoBumpr"
        }
    }
}