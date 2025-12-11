package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.PersonalItem;
import org.delcom.app.entities.User;
import org.delcom.app.services.PersonalItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
// "any" dihapus karena tidak dipakai
import static org.mockito.ArgumentMatchers.argThat; 
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTests {

    @Mock
    private AuthContext authContext;

    @Mock
    private PersonalItemService personalItemService;

    @Mock
    private Model model;

    @InjectMocks
    private DashboardController dashboardController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Melakukan inject dependency manual karena field di controller private/protected
        // dan @InjectMocks terkadang butuh bantuan jika menggunakan field injection @Autowired
        ReflectionTestUtils.setField(dashboardController, "authContext", authContext);
        ReflectionTestUtils.setField(dashboardController, "personalItemService", personalItemService);

        mockUser = new User("Test User", "test@example.com", "password");
        mockUser.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Dashboard redirect ke login jika user belum terautentikasi")
    void testDashboard_Unauthenticated_RedirectsToLogin() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(false);

        // Act
        String viewName = dashboardController.dashboard(model);

        // Assert
        assertEquals("redirect:/auth/login", viewName);
        verifyNoInteractions(personalItemService);
    }

    @Test
    @DisplayName("Recent Items harus memfilter/membuang item yang updatedAt-nya null")
    void testDashboard_RecentItems_FiltersNullUpdatedAt() {
        // Arrange
        // 1. Item dengan tanggal valid
        PersonalItem validItem = new PersonalItem();
        validItem.setItemName("Valid Item");
        ReflectionTestUtils.setField(validItem, "updatedAt", LocalDateTime.now());

        // 2. Item dengan tanggal NULL (Penyebab Diamond Kuning)
        PersonalItem nullDateItem = new PersonalItem();
        nullDateItem.setItemName("Null Date Item");
        // Kita set null secara eksplisit (atau biarkan defaultnya null)
        ReflectionTestUtils.setField(nullDateItem, "updatedAt", null);

        List<PersonalItem> items = Arrays.asList(validItem, nullDateItem);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllItems(mockUser.getId(), null)).thenReturn(items);

        // Act
        dashboardController.dashboard(model);

        // Assert
        // Kita tangkap hasil yang dikirim ke view untuk memastikan item null dibuang
        org.mockito.ArgumentCaptor<List<PersonalItem>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("recentItems"), captor.capture());

        List<PersonalItem> resultList = captor.getValue();

        // Validasi: Seharusnya hanya tersisa 1 item (yang valid)
        assertEquals(1, resultList.size());
        assertEquals("Valid Item", resultList.get(0).getItemName());
    }

    @Test
    @DisplayName("Dashboard menampilkan data kosong jika user tidak memiliki item")
    void testDashboard_Authenticated_NoItems() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllItems(mockUser.getId(), null)).thenReturn(new ArrayList<>());

        // Act
        String viewName = dashboardController.dashboard(model);

        // Assert
        assertEquals("pages/home", viewName);
        verify(model).addAttribute("auth", mockUser);
        verify(model).addAttribute("totalValue", 0.0);
        verify(model).addAttribute("totalItems", 0);
        verify(model).addAttribute(eq("recentItems"), argThat(list -> ((List<?>) list).isEmpty()));
    }

    @Test
    @DisplayName("Dashboard menampilkan data kosong jika service mengembalikan null (Null Safety)")
    void testDashboard_ServiceReturnsNull_HandledSafely() {
        // Arrange
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        // Simulasi service error/null return
        when(personalItemService.getAllItems(mockUser.getId(), null)).thenReturn(null);

        // Act
        String viewName = dashboardController.dashboard(model);

        // Assert
        assertEquals("pages/home", viewName);
        verify(model).addAttribute("totalValue", 0.0);
        verify(model).addAttribute("totalItems", 0);
    }

    @Test
    @DisplayName("Dashboard menghitung total aset dan jumlah item dengan benar")
    void testDashboard_CalculatesTotalsCorrectly() {
        // Arrange
        PersonalItem item1 = new PersonalItem();
        item1.setPrice(1000000.0);
        // GUNAKAN ReflectionTestUtils UNTUK MENGISI updatedAt
        ReflectionTestUtils.setField(item1, "updatedAt", LocalDateTime.now());

        PersonalItem item2 = new PersonalItem();
        item2.setPrice(500000.0);
        ReflectionTestUtils.setField(item2, "updatedAt", LocalDateTime.now());

        // Item dengan harga null (untuk menguji filter stream)
        PersonalItem item3 = new PersonalItem();
        item3.setPrice(null); 
        ReflectionTestUtils.setField(item3, "updatedAt", LocalDateTime.now());

        List<PersonalItem> items = Arrays.asList(item1, item2, item3);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllItems(mockUser.getId(), null)).thenReturn(items);

        // Act
        dashboardController.dashboard(model);

        // Assert
        // Total = 1.000.000 + 500.000 + 0 = 1.500.000
        verify(model).addAttribute("totalValue", 1500000.0);
        // Jumlah item = 3
        verify(model).addAttribute("totalItems", 3);
    }

    @Test
    @DisplayName("Dashboard menampilkan 3 item terbaru berdasarkan updated_at")
    @SuppressWarnings("unchecked")
    void testDashboard_RecentItemsLogic() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Buat 5 item dengan waktu update berbeda
        PersonalItem item1 = createItemWithTime("Item 1", now.minusDays(5)); // Terlama
        PersonalItem item2 = createItemWithTime("Item 2", now.minusDays(1)); // Ke-2 Terbaru
        PersonalItem item3 = createItemWithTime("Item 3", now.minusHours(1)); // Paling Baru
        PersonalItem item4 = createItemWithTime("Item 4", now.minusDays(3));
        PersonalItem item5 = createItemWithTime("Item 5", now.minusDays(2)); // Ke-3 Terbaru

        // List acak
        List<PersonalItem> items = Arrays.asList(item1, item2, item3, item4, item5);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(personalItemService.getAllItems(mockUser.getId(), null)).thenReturn(items);

        // Act
        dashboardController.dashboard(model);

        // Assert
        // Kita tangkap argumen yang dikirim ke model "recentItems"
        verify(model).addAttribute(eq("recentItems"), argThat(argument -> {
            List<PersonalItem> recentList = (List<PersonalItem>) argument;
            
            // Validasi 1: Ukuran harus maksimal 3
            if (recentList.size() != 3) return false;

            // Validasi 2: Urutan harus descending (Terbaru -> Terlama)
            // Urutan yang benar: Item 3 (1 jam lalu), Item 2 (1 hari lalu), Item 5 (2 hari lalu)
            boolean orderCorrect = recentList.get(0).getItemName().equals("Item 3") &&
                                   recentList.get(1).getItemName().equals("Item 2") &&
                                   recentList.get(2).getItemName().equals("Item 5");
            return orderCorrect;
        }));
    }

    // Helper method untuk membuat item dummy
    private PersonalItem createItemWithTime(String name, LocalDateTime updatedAt) {
        PersonalItem item = new PersonalItem();
        item.setItemName(name);
        item.setPrice(100.0);
        // Set properti via Reflection karena di Entity updatedAt diset via @PreUpdate/@PrePersist
        ReflectionTestUtils.setField(item, "updatedAt", updatedAt);
        return item;
    }
}