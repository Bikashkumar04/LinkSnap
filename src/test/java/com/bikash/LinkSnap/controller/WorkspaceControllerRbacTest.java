package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.WorkspaceDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerRbacTest {

    @Mock
    private WorkspaceService workspaceService;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private WorkspaceController workspaceController;

    @Test
    void getWorkspaceShouldThrowForbiddenWhenUserCannotViewWorkspace() {
        Authentication auth = authenticationForUser(7L);
        when(authorizationService.canViewWorkspace(7L, 99L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> workspaceController.getWorkspace(99L, auth)
        );

        assertEquals(403, ex.getStatusCode().value());
        verify(workspaceService, never()).getWorkspaceById(any());
    }

    @Test
    void getWorkspaceShouldReturnDataWhenAuthorized() {
        Authentication auth = authenticationForUser(7L);
        WorkspaceDTO workspaceDTO = new WorkspaceDTO(
                99L, "Acme", "acme", 7L, "FREE", LocalDateTime.now(), LocalDateTime.now()
        );
        when(authorizationService.canViewWorkspace(7L, 99L)).thenReturn(true);
        when(workspaceService.getWorkspaceById(99L)).thenReturn(workspaceDTO);

        WorkspaceDTO response = workspaceController.getWorkspace(99L, auth).getBody();

        assertNotNull(response);
        assertEquals(99L, response.getId());
        assertEquals("Acme", response.getName());
    }

    private Authentication authenticationForUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setStatus("ACTIVE");
        return new UsernamePasswordAuthenticationToken(user, null);
    }
}
