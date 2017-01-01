package sample.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import sample.domain.EmpDTO;

public interface EmpDAO {
	public EmpDTO selectEmpByKey(@Param("empno") Integer empno);

	public List<EmpDTO> selectEmpList();

	public int insertEmp(EmpDTO emp);

	public int updateEmp(EmpDTO emp);

	public int deleteEmp(@Param("empno") Integer empno);
}// interface
