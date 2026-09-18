package com.opstrack.worknote;

import com.opstrack.security.AppUser;
import com.opstrack.security.AppUserRepository;
import com.opstrack.security.Role;
import com.opstrack.technician.Technician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkNoteController.class)
@WithMockUser(
        username = "technician",
        roles = "TECHNICIAN"
)
public class WorkNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkNoteService workNoteService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void shouldCreateWorkNoteForAuthenticatedTechnician() {

        Long maintenanceTaskId = 1L;
        Long technicianId = 2L;

        String noteText =
                "Replaced damaged hydraulic fitting.";

        Technician technician =
                mock(Technician.class);

        when(
                technician.getId()
        ).thenReturn(technicianId);

        when(
                technician.getEmployeeNumber()
        ).thenReturn("TECH-005");

        AppUser appUser = new AppUser(
                "technician",
                "encoded-password",
                Role.TECHNICIAN,
                true
        );

        appUser.setTechnician(
                technician
        );

        Authentication authentication =
                mock(Authentication.class);

        when(
                authentication.getName()
        ).thenReturn("technician");

        when(
                appUserRepository.findByUsername(
                        "technician"
                )
        ).thenReturn(
                Optional.of(appUser)
        );

        WorkNote workNote =
                new WorkNote();

        workNote.setNote(
                noteText
        );

        workNote.setTechnician(
                technician
        );

        when(
                workNoteService.createWorkNote(
                        maintenanceTaskId,
                        technicianId,
                        noteText
                )
        ).thenReturn(
                workNote
        );

        WorkNoteController controller =
                new WorkNoteController(
                        workNoteService,
                        appUserRepository
                );

        WorkNote result =
                controller.createWorkNote(
                        maintenanceTaskId,
                        noteText,
                        authentication
                );

        assertEquals(
                noteText,
                result.getNote()
        );

        assertEquals(
                "TECH-005",
                result.getTechnician()
                        .getEmployeeNumber()
        );

        verify(
                appUserRepository
        ).findByUsername(
                "technician"
        );

        verify(
                workNoteService
        ).createWorkNote(
                maintenanceTaskId,
                technicianId,
                noteText
        );
    }

    @Test
    void shouldGetAllWorkNotes()
            throws Exception {

        WorkNote note1 =
                new WorkNote();

        note1.setNote(
                "Inspected hydraulic lines."
        );

        WorkNote note2 =
                new WorkNote();

        note2.setNote(
                "Replaced damaged fitting."
        );

        when(
                workNoteService.getAllWorkNotes()
        ).thenReturn(
                List.of(
                        note1,
                        note2
                )
        );

        mockMvc.perform(
                        get("/api/work-notes")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].note")
                                .value(
                                        "Inspected hydraulic lines."
                                )
                )
                .andExpect(
                        jsonPath("$[1].note")
                                .value(
                                        "Replaced damaged fitting."
                                )
                );
    }

    @Test
    void shouldGetWorkNotesByMaintenanceTaskId()
            throws Exception {

        Long maintenanceTaskId = 1L;

        WorkNote note1 =
                new WorkNote();

        note1.setNote(
                "Inspected hydraulic lines."
        );

        WorkNote note2 =
                new WorkNote();

        note2.setNote(
                "Replaced damaged fitting."
        );

        when(
                workNoteService
                        .getWorkNotesByMaintenanceTaskId(
                                maintenanceTaskId
                        )
        ).thenReturn(
                List.of(
                        note1,
                        note2
                )
        );

        mockMvc.perform(
                        get(
                                "/api/work-notes/maintenance-task/1"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].note")
                                .value(
                                        "Inspected hydraulic lines."
                                )
                );
    }

    @Test
    void shouldDeleteWorkNote()
            throws Exception {

        mockMvc.perform(
                        delete("/api/work-notes/1")
                )
                .andExpect(status().isOk());

        verify(
                workNoteService
        ).deleteWorkNote(1L);
    }
}