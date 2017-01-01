package sample.mgr;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import sample.dao.EmpDAO;
import sample.domain.EmpDTO;

import com.casmall.common.db.DBManager;

/**
 * emp 테이블을 이용한 CRUD 샘플 Mgr
 * @author OBERAK
 */
public class EmpMgr {
	protected static Log logger = LogFactory.getLog(EmpMgr.class);

	private DBManager dbm = DBManager.getInstance();

	public int insertEmp(EmpDTO emp) {
		SqlSession session = dbm.openSession();
		int cnt = 0;
		try{
			EmpDAO empDao = session.getMapper(EmpDAO.class);
			cnt = empDao.insertEmp(emp);
		}finally{
			session.commit();
			session.close();
		} // try

		return cnt;
	} // insertEmp()

	public int updateEmp(EmpDTO emp) {
		SqlSession session = dbm.openSession();
		int cnt = 0;
		try{
			EmpDAO empDao = session.getMapper(EmpDAO.class);
			cnt = empDao.updateEmp(emp);
		}finally{
			session.commit();
			session.close();
		} // try

		return cnt;
	} // updateEmp()

	public int deleteEmp(int empno) {
		SqlSession session = dbm.openSession();
		int cnt = 0;
		try{
			EmpDAO empDao = session.getMapper(EmpDAO.class);
			cnt = empDao.deleteEmp(empno);
		}finally{
			session.commit();
			session.close();
		} // try

		return cnt;
	} // deleteEmp()

	public List<EmpDTO> selectEmpList() {
		List<EmpDTO> list = null;

		SqlSession session = dbm.openSession();
		EmpDAO empDao = session.getMapper(EmpDAO.class);
		list = empDao.selectEmpList();
		session.close();

		return list;
	} // selectEmpList()

	public EmpDTO selectEmpByKey(int empno) {
		EmpDTO empDto = null;

		SqlSession session = dbm.openSession();
		EmpDAO empDao = session.getMapper(EmpDAO.class);
		empDto = empDao.selectEmpByKey(empno);
		session.close();

		return empDto;
	} // selectEmpByKey()
	
} // class