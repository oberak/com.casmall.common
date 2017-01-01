package sample.domain;

import com.casmall.common.BaseObject;

public class EmpDTO extends BaseObject{

	private static final long serialVersionUID = 1L;
	private int empno;
	private String ename;
	
	public EmpDTO(){
	}
	
	public EmpDTO(int empno, String ename) {
		super();
		this.empno = empno;
		this.ename = ename;
	}
	
	public int getEmpno() {
		return empno;
	}
	public void setEmpno(int empno) {
		this.empno = empno;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
}
