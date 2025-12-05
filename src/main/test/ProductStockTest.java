package main.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.java.ProductStock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Execution(value = ExecutionMode.CONCURRENT)
class ProductStockTest {

	static ProductStock stock;
	
	@BeforeAll
	static void setUpClass() throws Exception {
		System.out.println("Starting ProductStock tests...");
	}
	@AfterAll
	static void tearDownClass() throws Exception {
		System.out.println("All tests completed!");
	}

	

	@BeforeEach
	void setUpMethod() throws Exception {
		stock = new ProductStock("P001", "WH-1-A1", 50, 20, 100);
		
	}
	@AfterEach
	void tearDownMethod() throws Exception {
		System.out.println("Test finished.");
	
	}
	
	
	@Order(1)
	@Tag("sanity")
	@Timeout(value=500,unit=TimeUnit.MILLISECONDS)
	@DisplayName("Valid Constructor")
	@Test
	void ValidConstructorTest() {
		 ProductStock s = new ProductStock("P002", "WH-2-B2", 10, 5, 50);
	        assertAll("Constructor values",
	            () -> assertEquals("P002", s.getProductId()),
	            () -> assertEquals("WH-2-B2", s.getLocation()),
	            () -> assertEquals(10, s.getOnHand()),
	            () -> assertEquals(0, s.getReserved()),
	            () -> assertEquals(5, s.getReorderThreshold()),
	            () -> assertEquals(50, s.getMaxCapacity())
	        );
	}
	
	@Order(2)
	@Tag("regression")
	@DisplayName("Invalid Constructor Parameters")
	@Test
	void InvalidConstructorTest() {
		 assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock(null, "WH-1", 10, 5, 50));
	        assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock("P1", "", 10, 5, 50));
	        assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock("P1", "WH-1", -1, 5, 50));
	        assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock("P1", "WH-1", 10, -5, 50));
	        assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock("P1", "WH-1", 10, 5, 0));
	        assertThrows(IllegalArgumentException.class,
	                () -> new ProductStock("P1", "WH-1", 100, 5, 50));
	}
	
	
	@Order(3)
	@Tag("regression")
	@DisplayName("Normal addStock")
	@Test
	void addStockNormalTest() {
		  stock.addStock(30);
	      assertEquals(80, stock.getOnHand());
	}
	
	@Order(4)
	@Tag("regression")
    @DisplayName("addStock beyond maxCapacity")
	@Test
	void addStockErrorTest() {
		 assertThrows(IllegalStateException.class, () -> stock.addStock(100));
	}
	
	@Order(5)
	@Tag("sanity")
	@DisplayName("Parameterized addStock")
	@ParameterizedTest
	@ValueSource(ints= {10,20,30})
	void addStockParameterizedTest(int value) throws Exception {
		  stock.addStock(value);
		  assertEquals(value + 50, stock.getOnHand());
		  setUpMethod();
	}
	
	@Order(6)
	@Tag("regression")
	@DisplayName("addStock invalid amount")
	@Test
	void addStockInvalidTest() {
		 assertThrows(IllegalArgumentException.class, () -> stock.addStock(0));
		 assertThrows(IllegalArgumentException.class, () -> stock.addStock(-100));
	}
	
	@Order(7)
	@Tag("regression")
	@DisplayName("Reserve normal")
	@Test
	void reserveTest() {
		stock.reserve(20);
		assertEquals(20, stock.getReserved());
	    assertEquals(30, stock.getAvailable());
	}
	
	@Order(8)
	@Tag("regression")
	@Test
	@DisplayName("Reserve more than available")
    void reserveErrorTest() {
	    assertThrows(IllegalStateException.class, () -> stock.reserve(60));
    }
	
	@Order(9)
	@Tag("regression")
	@Test
	@DisplayName("Reserve invalid value")
    void ReserveInvalidTest() {
	    assertThrows(IllegalArgumentException.class, () -> stock.reserve(0));
	    assertThrows(IllegalArgumentException.class, () -> stock.reserve(-30));
    }
	
	@Order(10)
	@Tag("regression")
	@DisplayName("Release Reservation normal")
	@Test
	void releaseReservationTest() {
		  stock.reserve(20);
	      stock.releaseReservation(10);
	      assertEquals(10, stock.getReserved());
	}
	
	@Order(11)
	@Tag("regression")
	@DisplayName("Release Reservation error")
	@Test
	void releaseReservationErrorTest() {
		  assertThrows(IllegalStateException.class, () -> stock.releaseReservation(10));
	}
	
	@Order(12)
	@Tag("regression")
	@DisplayName("Release Reservation invalid value")
	@Test
	void releaseReservationInvalidTest() {
		  assertThrows(IllegalArgumentException.class, () -> stock.releaseReservation(0));
		  assertThrows(IllegalArgumentException.class, () -> stock.releaseReservation(-20));
	}
	
	@Order(13)
	@Tag("regression")
	@DisplayName("Ship Reserved normal")
	@Test
	void shipReservedTest() {
		  stock.reserve(20);
	      stock.shipReserved(15);
	      assertEquals(35, stock.getOnHand());
	      assertEquals(5, stock.getReserved());
	}
	
	@Order(14)
	@Tag("regression")
	@DisplayName("Ship Reserved error")
	@Test
	void shipReservedErrorTest() {
	      assertThrows(IllegalStateException.class, () -> stock.shipReserved(15));
	}
	
	@Order(15)
	@Tag("regression")
	@DisplayName("Ship Reserved invalid value")
	@Test
	void shipReservedInvalidTest() {
		 assertThrows(IllegalArgumentException.class, () -> stock.shipReserved(0));
		 assertThrows(IllegalArgumentException.class, () -> stock.shipReserved(-30));
	}
	
	@Order(16)
	@Tag("regression")
	@DisplayName("Remove Damaged normal")
	@Test
	void removeDamagedTest() {
		stock.removeDamaged(10);
        assertEquals(40, stock.getOnHand());
	}
	
	@Order(17)
	@Tag("regression")
	@DisplayName("Remove Damaged error")
	@Test
	void removeDamagedErrorTest() {
		assertThrows(IllegalStateException.class, () -> stock.removeDamaged(150));
	}
	
	@Order(18)
	@Tag("regression")
	@DisplayName("Remove Damaged invalid value")
	@Test
	void removeDamagedInvalidTest() {
		assertThrows(IllegalArgumentException.class, () -> stock.removeDamaged(0));
		assertThrows(IllegalArgumentException.class, () -> stock.removeDamaged(-40));
	}
	
	@Order(19)
	@Tag("regression")
	@DisplayName("Is Reorder Needed")
	@Test
	void isReorderNeededTest() {
		assertFalse(stock.isReorderNeeded());
        stock.removeDamaged(40);
        assertTrue(stock.isReorderNeeded());
	}
	
	
	@Order(20)
	@Tag("regression")
	@DisplayName("Update Reorder Threshold normal")
	@Test
	void updateReorderThresholdTest() {
		stock.updateReorderThreshold(10);
		assertEquals(10, stock.getReorderThreshold());
	}
	
	@Order(21)
	@Tag("regression")
	@DisplayName("Update Reorder Threshold error")
	@Test
	void updateReorderThresholdErrorTest() {
		assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(200));
		
	}
	
	@Order(22)
	@Tag("regression")
	@DisplayName("Update Reorder Threshold invalid value")
	@Test
	void updateReorderThresholdInvalidTest() {
		assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(-20));
		
	}
	
	@Order(23)
	@Tag("regression")
	@DisplayName("Update Max Capacity normal")
	@Test
	void updateMaxCapacityTest() {
		stock.updateMaxCapacity(200);
		assertEquals(200,stock.getMaxCapacity());
	}
	
	@Order(24)
	@Tag("regression")
	@DisplayName("Update Max Capacity error")
	@Test
	void updateMaxCapacityErrorTest() {
		assertThrows(IllegalStateException.class, () -> stock.updateMaxCapacity(10));
	}

	
	@Order(25)
	@Tag("regression")
	@DisplayName("Update Max Capacity invalid value")
	@Test
	void updateMaxCapacityInvalidTest() {
		assertThrows(IllegalArgumentException.class, () -> stock.updateMaxCapacity(0));
		assertThrows(IllegalArgumentException.class, () -> stock.updateMaxCapacity(-40));
	}
	
    @Disabled("Future feature test")
    @DisplayName("Future Feature Test")
    @Test
    void futureFeatureTest() {
        assertTrue(true);
    }
}
