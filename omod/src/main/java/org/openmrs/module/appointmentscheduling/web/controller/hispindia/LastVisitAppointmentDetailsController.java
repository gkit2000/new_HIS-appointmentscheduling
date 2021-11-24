/**
 * 
 */
package org.openmrs.module.appointmentscheduling.web.controller.hispindia;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Ghanshyam
 *
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/lastVisitAppointmentDeatils")
public class LastVisitAppointmentDetailsController {
	// AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
	@RequestMapping(value = "/patient", method = RequestMethod.GET)
	public void getLastVisitAppointmentDeatils(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "visit") String visitUuid)
			throws ResponseException, JsonGenerationException, JsonMappingException, IOException, ParseException {
		LastVisitAppointmentDeatils lastVisitAppointmentDeatils = new LastVisitAppointmentDeatils();
		ServletOutputStream out = response.getOutputStream();
		Visit visit = Context.getService(VisitService.class).getVisitByUuid(visitUuid);
		if (visit != null) {
			Appointment appointment = Context.getService(AppointmentService.class).getAppointmentByVisit(visit);
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			if (appointment != null) {
				lastVisitAppointmentDeatils.setRegistrationDate(formatter.format(appointment.getDateCreated()));
				lastVisitAppointmentDeatils.setDepartmentName(appointment.getAppointmentType().getName());
				lastVisitAppointmentDeatils
						.setReportingPlace(appointment.getTimeSlot().getAppointmentBlock().getLocation().getName());
				lastVisitAppointmentDeatils
						.setDoctorName(appointment.getTimeSlot().getAppointmentBlock().getProvider().getName());
				List<Encounter> encounters = Context.getService(EncounterService.class).getEncountersByVisit(visit,
						false);
				for (Encounter encounter : encounters) {
					if (encounter.getEncounterType().getName().equalsIgnoreCase("Check In")) {
						lastVisitAppointmentDeatils.setReportingTime(formatter.format(encounter.getDateCreated()));
					}
				}
			}
		}
		new ObjectMapper().writeValue(out, lastVisitAppointmentDeatils);
	}
}
