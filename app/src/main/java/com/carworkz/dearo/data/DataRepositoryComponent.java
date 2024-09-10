package com.carworkz.dearo.data;

import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerComponent;
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerPresenterModule;
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsComponent;
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsPresenterModule;
import com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo.AddMissingVehicleInfoComponent;
import com.carworkz.dearo.addjobcard.addeditvehicle.addmissinginfo.AddMissingVehicleInfoPresenterModule;
import com.carworkz.dearo.addjobcard.completejobcard.CompleteJobCardComponent;
import com.carworkz.dearo.addjobcard.completejobcard.CompleteJobCardPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.accidental.AccidentalComponent;
import com.carworkz.dearo.addjobcard.createjobcard.accidental.AccidentalPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress.InsuranceAddressSelectionComponent;
import com.carworkz.dearo.addjobcard.createjobcard.accidental.selectionaddress.InsuranceAddressSelectionPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchComponent;
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.damage.DamageComponent;
import com.carworkz.dearo.addjobcard.createjobcard.damage.DamagePresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.CapturePictureComponent;
import com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage.ClickPicturePresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery.DamageGalleryComponent;
import com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery.DamageGalleryPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.EstimateComponent;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.EstimatePresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature.DigitalSignatureComponent;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.digitalsignature.DigitalSignaturePresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberComponent;
import com.carworkz.dearo.addjobcard.createjobcard.estimate.partnumberselection.searchpartnumber.SearchPartNumberPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.inspection.InspectionPresenterComponent;
import com.carworkz.dearo.addjobcard.createjobcard.inspection.InspectionPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.inventory.InventoryComponent;
import com.carworkz.dearo.addjobcard.createjobcard.inventory.InventoryPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.JobsComponent;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCComponent;
import com.carworkz.dearo.addjobcard.createjobcard.jobs.viewjc.ViewJCPresenterModule;
import com.carworkz.dearo.addjobcard.createjobcard.voice.VoiceComponent;
import com.carworkz.dearo.addjobcard.createjobcard.voice.VoicePresenterModule;
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardComponent;
import com.carworkz.dearo.addjobcard.quickjobcard.QuickJobCardPresenterModule;
import com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns.QuickConcernComponent;
import com.carworkz.dearo.addjobcard.quickjobcard.quickconcerns.QuickConcernPresenterModule;
import com.carworkz.dearo.amc.AMCPresenterModule;
import com.carworkz.dearo.amc.AmcComponent;
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsComponent;
import com.carworkz.dearo.amc.amcsolddetails.AmcDetailsPresenterModule;
import com.carworkz.dearo.analytics.AnalyticsModule;
import com.carworkz.dearo.appointments.appointmentdetails.AppointmentCardDetailsComponent;
import com.carworkz.dearo.appointments.appointmentdetails.AppointmentCardDetailsPresenterModule;
import com.carworkz.dearo.appointments.createappointment.appointmentDetails.AppointmentDetailsComponent;
import com.carworkz.dearo.appointments.createappointment.appointmentDetails.AppointmentDetailsPresenterModule;
import com.carworkz.dearo.appointments.createappointment.servicePackages.ServicePackageComponent;
import com.carworkz.dearo.appointments.createappointment.servicePackages.ServicePackagePresenterModule;
import com.carworkz.dearo.appointments.createappointment.timeSlot.TimeSlotComponent;
import com.carworkz.dearo.appointments.createappointment.timeSlot.TimeSlotPresenterModule;
import com.carworkz.dearo.appointments.createappointment.updateInfo.UpdateInfoComponent;
import com.carworkz.dearo.appointments.createappointment.updateInfo.UpdateInfoPresenterModule;
import com.carworkz.dearo.appointments.reschedule.RescheduleComponent;
import com.carworkz.dearo.appointments.reschedule.ReschedulePresenterModule;
import com.carworkz.dearo.appointments.status.AppointmentStatusComponent;
import com.carworkz.dearo.appointments.status.AppointmentStatusPresenterModule;
import com.carworkz.dearo.cardslisting.CardListingComponent;
import com.carworkz.dearo.cardslisting.CardListingPresenterModule;
import com.carworkz.dearo.cronjobs.forceupdatestatus.ForceUpdateComponent;
import com.carworkz.dearo.cronjobs.forceupdatestatus.ForceUpdatePresenterModule;
import com.carworkz.dearo.cronjobs.imageupload.ImageUploadComponent;
import com.carworkz.dearo.cronjobs.imageupload.ImageUploadPresenterModule;
import com.carworkz.dearo.cronjobs.userconfig.UserConfigComponent;
import com.carworkz.dearo.cronjobs.userconfig.UserConfigPresenterModule;
import com.carworkz.dearo.customerapproval.CustomerApprovalComponent;
import com.carworkz.dearo.customerapproval.CustomerApprovalModule;
import com.carworkz.dearo.customerfeedback.CustomerFeedbackComponent;
import com.carworkz.dearo.customerfeedback.CustomerFeedbackPresenterModule;
import com.carworkz.dearo.dashboard.DashboardComponent;
import com.carworkz.dearo.dashboard.DashboardPresenterModule;
import com.carworkz.dearo.injection.ApplicationComponent;
import com.carworkz.dearo.injection.ApplicationScoped;
import com.carworkz.dearo.invoices.addItem.labour.AddLabourComponent;
import com.carworkz.dearo.invoices.addItem.labour.AddLabourPresenterModule;
import com.carworkz.dearo.invoices.addItem.part.AddPartComponent;
import com.carworkz.dearo.invoices.addItem.part.AddPartPresenterModule;
import com.carworkz.dearo.invoices.invoiceremarks.InvoiceRemarkComponent;
import com.carworkz.dearo.invoices.invoiceremarks.InvoiceRemarksPresenterModule;
import com.carworkz.dearo.login.WebPageActivity;
import com.carworkz.dearo.login.addmobilenumber.LoginComponent;
import com.carworkz.dearo.login.addmobilenumber.LoginPresenterModule;
import com.carworkz.dearo.login.lead.LeadComponent;
import com.carworkz.dearo.login.lead.LeadPresenterModule;
import com.carworkz.dearo.login.readotp.ReadOtpComponent;
import com.carworkz.dearo.login.readotp.ReadOtpPresenterModule;
import com.carworkz.dearo.morecta.MoreCtaComponent;
import com.carworkz.dearo.morecta.MoreCtaPresenterModule;
import com.carworkz.dearo.mrn.MrnComponent;
import com.carworkz.dearo.mrn.MrnPresenterModule;
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryComponent;
import com.carworkz.dearo.mycustomers.cutomervehiclehistory.CustomerVehicleHistoryPresenterModule;
import com.carworkz.dearo.mycustomers.filterappointment.FilterActivity;
import com.carworkz.dearo.mycustomers.mycustomerlisting.CustomerListingComponent;
import com.carworkz.dearo.mycustomers.mycustomerlisting.CustomerListingPresenterModule;
import com.carworkz.dearo.notification.deviceregistration.DeviceRegistrarComponent;
import com.carworkz.dearo.notification.deviceregistration.DeviceRegistrarModule;
import com.carworkz.dearo.otc.OtcProformaComponent;
import com.carworkz.dearo.otc.OtcProfromaPresenterModule;
import com.carworkz.dearo.otc.customer.CustomerDetailsComponent;
import com.carworkz.dearo.otc.customer.CustomerDetailsPresenterModule;
import com.carworkz.dearo.othersyshistory.OtherHistoryComponent;
import com.carworkz.dearo.othersyshistory.OtherHistoryPresenterModule;
import com.carworkz.dearo.outwarding.OutwardingProcessComponent;
import com.carworkz.dearo.outwarding.OutwardingProcessPresenterModule;
import com.carworkz.dearo.partfinder.PartFinderComponent;
import com.carworkz.dearo.partfinder.PartFinderPresenterModule;
import com.carworkz.dearo.partpayment.PartPaymentComponent;
import com.carworkz.dearo.partpayment.PartPaymentPresenterModule;
import com.carworkz.dearo.predeliverycheck.PdcComponent;
import com.carworkz.dearo.predeliverycheck.PdcModule;
import com.carworkz.dearo.search.SearchComponent;
import com.carworkz.dearo.search.SearchPresenterModule;
import com.carworkz.dearo.serviceremainder.ServiceReminderComponent;
import com.carworkz.dearo.serviceremainder.ServiceReminderPresenterModule;
import com.carworkz.dearo.splashscreen.ConfigComponent;
import com.carworkz.dearo.splashscreen.ConfigPresenterModule;
import com.carworkz.dearo.thirdpartydetails.ThirdPartyDetailsComponent;
import com.carworkz.dearo.thirdpartydetails.ThirdPartyDetailsPresenterModule;
import com.carworkz.dearo.vehiclequery.VehicleQueryComponent;
import com.carworkz.dearo.vehiclequery.VehicleQueryPresenterModule;

import org.jetbrains.annotations.NotNull;

import dagger.Component;

/**
 * Created by Farhan on 27/7/17.
 */
@ApplicationScoped
@Component(dependencies = ApplicationComponent.class, modules = {DataRepositoryModule.class, AnalyticsModule.class})
public interface DataRepositoryComponent {

    LoginComponent COMPONENT(LoginPresenterModule loginPresenterModule);

    ReadOtpComponent COMPONENT(ReadOtpPresenterModule readOtpPresenterModule);

    CapturePictureComponent COMPONENT(ClickPicturePresenterModule clickPicturePresenterModule);

    CustomerCarSearchComponent COMPONENT(CustomerCarSearchPresenterModule customerCarSearchPresenterModule);

    VehicleDetailsComponent COMPONENT(VehicleDetailsPresenterModule vehicleDetailsPresenterModule);

    VoiceComponent COMPONENT(VoicePresenterModule voicePresenterModule);

    InventoryComponent COMPONENT(InventoryPresenterModule inventoryPresenterModule);

    AddCustomerComponent COMPONENT(AddCustomerPresenterModule addCustomerPresenterModule);

    EstimateComponent COMPONENT(EstimatePresenterModule estimatePresenterModule);

    CardListingComponent COMPONENT(CardListingPresenterModule cardListingPresenterModule);

    InspectionPresenterComponent COMPONENT(InspectionPresenterModule inspectionPresenterModule);

    DamageComponent COMPONENT(DamagePresenterModule damagePresenterModule);

    JobsComponent COMPONENT(JobPresenterModule jobPresenterModule);

    SearchComponent COMPONENT(SearchPresenterModule searchPresenterModule);

    OutwardingProcessComponent COMPONENT(OutwardingProcessPresenterModule outwardingProcessPresenterModule);

    AddPartComponent COMPONENT(AddPartPresenterModule addPartPresenterModule);

    AddLabourComponent COMPONENT(AddLabourPresenterModule addLabourPresenterModule);

    ViewJCComponent COMPONENT(ViewJCPresenterModule viewJCPresenterModule);

    UserConfigComponent COMPONENT(UserConfigPresenterModule userConfigPresenterModule);

    ForceUpdateComponent COMPONENT(ForceUpdatePresenterModule forceUpdatePresenterModuletPresenterModule);

    ConfigComponent COMPONENT(ConfigPresenterModule configPresenterModule);

    AppointmentCardDetailsComponent COMPONENT(AppointmentCardDetailsPresenterModule appointmentCardDetailsPresenterModule);

    AppointmentDetailsComponent COMPONENT(AppointmentDetailsPresenterModule appointmentDetailsPresenterModule);

    ServicePackageComponent COMPONENT(ServicePackagePresenterModule servicePackagePresenterModule);

    TimeSlotComponent COMPONENT(TimeSlotPresenterModule timeSlotPresenterModule);

    MoreCtaComponent COMPONENT(MoreCtaPresenterModule moreCtaPresenterModule);

    UpdateInfoComponent COMPONENT(UpdateInfoPresenterModule updateInfoPresenterModule);

    RescheduleComponent COMPONENT(ReschedulePresenterModule reschedulePresenterModule);

    CustomerListingComponent COMPONENT(CustomerListingPresenterModule customerListingPresenterModule);

    CustomerVehicleHistoryComponent COMPONENT(CustomerVehicleHistoryPresenterModule customerVehicleHistoryPresenterModule);

    DamageGalleryComponent COMPONENT(DamageGalleryPresenterModule damageGalleryPresenterModule);

    PartFinderComponent COMPONENT(PartFinderPresenterModule partFinderPresenterModule);

    WebPageActivity COMPONENT(WebPageActivity webPageActivity);

    FilterActivity COMPONENT(FilterActivity filterActivity);

    VehicleQueryComponent COMPONENT(VehicleQueryPresenterModule vehicleQueryPresenterModule);

    InvoiceRemarkComponent COMPONENT(InvoiceRemarksPresenterModule invoiceRemarksPresenterModule);

    AccidentalComponent COMPONENT(AccidentalPresenterModule accidentalPresenterModule);

    LeadComponent COMPONENT(LeadPresenterModule leadPresenterModule);

    com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage.ServicePackageComponent COMPONENT(com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage.ServicePackagePresenterModule servicePackagePresenterModule);

    ServiceReminderComponent COMPONENT(ServiceReminderPresenterModule serviceReminderPresenterModule);

    OtcProformaComponent COMPONENT(OtcProfromaPresenterModule otcProfromaPresenterModule);

    CustomerDetailsComponent COMPONENT(CustomerDetailsPresenterModule customerDetailsPresenterModule);

    SearchPartNumberComponent COMPONENT(SearchPartNumberPresenterModule searchPartNumberPresenterModule);

    DashboardComponent COMPONENT(DashboardPresenterModule dashboardPresenterModule);

    DigitalSignatureComponent COMPONENT(DigitalSignaturePresenterModule digitalSignaturePresenterModule);

    QuickJobCardComponent COMPONENT(QuickJobCardPresenterModule quickJobCardPresenterModule);

    QuickConcernComponent COMPONENT(QuickConcernPresenterModule quickConcernPresenterModule);

    ImageUploadComponent COMPONENT(ImageUploadPresenterModule imageUploadPresenterModule);

    DeviceRegistrarComponent COMPONENT(DeviceRegistrarModule deviceRegistrarModule);

    PartPaymentComponent COMPONENT(PartPaymentPresenterModule partPaymentPresenterModule);

    CustomerFeedbackComponent COMPONENT(CustomerFeedbackPresenterModule customerFeedbackPresenterModule);

    MrnComponent COMPONENT(MrnPresenterModule mrnPresenterModule);

    OtherHistoryComponent COMPONENT(OtherHistoryPresenterModule otherHistoryPresenterModule);

    InsuranceAddressSelectionComponent COMPONENT(InsuranceAddressSelectionPresenterModule insuranceAddressSelectionPresenterModule);

    AddMissingVehicleInfoComponent COMPONENT(AddMissingVehicleInfoPresenterModule addMissingVehicleInfoPresenterModule);

    ThirdPartyDetailsComponent COMPONENT(ThirdPartyDetailsPresenterModule thirdPartyDetailsPresenterModule);

    AmcComponent COMPONENT(AMCPresenterModule module);

    CompleteJobCardComponent COMPONENT(CompleteJobCardPresenterModule module);

    AmcDetailsComponent COMPONENT(AmcDetailsPresenterModule module);

    PdcComponent COMPONENT(PdcModule module);

    CustomerApprovalComponent COMPONENT(CustomerApprovalModule module);

    AppointmentStatusComponent COMPONENT(AppointmentStatusPresenterModule appointmentStatusPresenterModule);
}
