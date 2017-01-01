package sample.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import sample.domain.EmpDTO;
import sample.mgr.EmpMgr;

public class TestEmpMgr {
	private static EmpMgr mgr = new EmpMgr();
	private static EmpDTO emp;
	@BeforeClass
	public static void runBeforeClass(){
		emp = new EmpDTO(1005,"테스터");
	}
	
	@AfterClass
	public static void runAfterClass(){
		mgr.deleteEmp(emp.getEmpno());
	}
	
	@Before
	public void runBeforeEveryTest() {
	}

	@After
	public void runAfterEveryTest() {
	}
	
	@Test
	public void testInsertEmp(){
		assertEquals(1,mgr.insertEmp(emp));
	}
	
	@Test
	public void testSelectEmpByKey() {
		assertNotNull(mgr.selectEmpByKey(100));
		assertEquals(emp,mgr.selectEmpByKey(emp.getEmpno()));
	}
	
	@Test
	public void testUpdateEmp(){
		emp.setEname("테스터2");
		assertEquals(1,mgr.updateEmp(emp));
		assertEquals("테스터2",mgr.selectEmpByKey(emp.getEmpno()).getEname());
	}
	
	@Test
	public void testDeleteEmp(){
		assertEquals(1,mgr.deleteEmp(emp.getEmpno()));
	}
	
	@Test
	public void testSelectEmpList() {
		List<EmpDTO> list = mgr.selectEmpList();
		System.out.println("list >>> "+list);
		assertNotNull(list);
	}
	
	@Test(expected = PersistenceException.class)
	public void testDupInsertException(){
		mgr.insertEmp(emp);
		mgr.insertEmp(emp);
	}
	
	@Ignore("Not Ready to Run")   
	@Test(timeout = 1000)
	public void infinity() {
		while (true)
			;
	}

}
