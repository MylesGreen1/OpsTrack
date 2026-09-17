package com.opstrack.worknote;

import com.opstrack.technician.Technician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(WorkNoteController.class)
@WithMockUser(roles = "TECHNICIAN")
public class WorkNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkNoteService workNoteService;

    @Test
    void shouldCreateWorkNote() throws Exception {
        Long maintenanceTaskId = 1L;
        Long technicianId = 2L;
        String noteText = "Replaced damaged hydraulic fitting.";

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-005",
                "Hydraulics",
                true
        );

        WorkNote workNote = new WorkNote();
        workNote.setNote(noteText);
        workNote.setTechnician(technician);

        when(workNoteService.createWorkNote(
                maintenanceTaskId,
                technicianId,
                noteText
        )).thenReturn(workNote);

        mockMvc.perform(
                        post("/api/work-notes")
                                .param(
                                        "maintenanceTaskId",
                                        maintenanceTaskId.toString()
                                )
                                .param(
                                        "technicianId",
                                        technicianId.toString()
                                )
                                .param("note", noteText)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.note")
                                .value(noteText)
                )
                .andExpect(
                        jsonPath("$.technician.employeeNumber")
                                .value("TECH-005")
                );
    }

    @Test
    void shouldGetAllWorkNotes() throws Exception {

        WorkNote note1 = new WorkNote();
        note1.setNote("Inspected hydraulic lines.");

        WorkNote note2 = new WorkNote();
        note2.setNote("Replaced damaged fitting.");

        when(workNoteService.getAllWorkNotes())
                .thenReturn(List.of(note1, note2));

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
                                .value("Inspected hydraulic lines.")
                )
                .andExpect(
                        jsonPath("$[1].note")
                                .value("Replaced damaged fitting.")
                );
    }

    @Test
    void shouldGetWorkNotesByMaintenanceTaskId() throws Exception {
        Long maintenanceTaskId = 1L;

        WorkNote note1 = new WorkNote();
        note1.setNote("Inspected hydraulic lines.");

        WorkNote note2 = new WorkNote();
        note2.setNote("Replaced damaged fitting.");

        when(
                workNoteService.getWorkNotesByMaintenanceTaskId(
                        maintenanceTaskId
                )
        ).thenReturn(List.of(note1, note2));

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
                                .value("Inspected hydraulic lines.")
                );
    }

    @Test
    void shouldDeleteWorkNote() throws Exception {

        mockMvc.perform(
                        delete("/api/work-notes/1")
                )
                .andExpect(status().isOk());

        verify(workNoteService).deleteWorkNote(1L);
    }

}