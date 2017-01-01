package com.casmall.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.casmall.biz.domain.CmMsgAttrDTO;
import com.casmall.biz.domain.CmMsgDTO;
import com.casmall.biz.domain.CmOsMcDTO;
import com.casmall.biz.mgr.CommonManager;
import com.casmall.common.ByteQueue;
import com.casmall.common.DConstants;
import com.casmall.common.StringUtil;

/**
 * RS232/422로부터 데이터를 읽는 클래스 (명령어 전송방식)
 * 
 * @author 원길호
 * 
 */
public class CmSerial {
	protected static Log logger = LogFactory.getLog(CmSerial.class);
	private static CmSerial instance = null;

	/** port 및 msg 환경 */
	private CommonManager sem = null;

	/** instrument id */
	private String mcId = DConstants.DEFAULT_MC_ID;
	/** port env */
	private CmOsMcDTO portEnv = null;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean isOpen = false;
	/** Call back work flag */
	private boolean isCallbackWork = true;
	private boolean isDataInput = false;

	/** data queue */
	private ByteQueue bq;
	private Thread offerQueueThread;

	/** Call-back list */
	private ArrayList<CmSerialCallbackIF> callback = new ArrayList<CmSerialCallbackIF>();

	/**
	 * 생성자<br>
	 * 디폴트 MC ID 사용<br>
	 */
	private CmSerial() throws IOException {
		sem = CommonManager.getInstance();
	} // CmSerial

	/**
	 * MC ID를 이용한 생성자
	 */
	private CmSerial(String inId) throws IOException {
		this();
		this.mcId = inId;
	} // CmSerial

	/**
	 * 싱글톤 객체 반환
	 * 
	 * @return
	 */
	public static synchronized CmSerial getInstance() throws IOException {
		if (instance == null) {
			instance = new CmSerial();
		}
		return instance;
	} // getInstance

	/**
	 * MC ID로 싱글톤 객체 반환
	 * 
	 * @param inId
	 * @return
	 * @throws IOException
	 */
	public static synchronized CmSerial getInstance(String inId) throws IOException {
		if (instance == null) {
			instance = new CmSerial(inId);
		}
		return instance;
	} // getInstance

	/**
	 * 포트 열기 및 Stream 얻기
	 * 
	 * @throws PortInUseException
	 * 
	 * @throws Exception
	 */
	public void open() throws IOException, PortInUseException {
		if (isOpen)
			return;

		portEnv = sem.getOsMcEnv(mcId);

		boolean portFound = false;
		CommPortIdentifier portId = null;

		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(portEnv.getPort_nm())) {
					if (logger.isDebugEnabled())
						logger.debug("Found port: " + portEnv.getPort_nm());
					portFound = true;
					break;
				}
			}
		} // while

		if (!portFound) {
			if (logger.isErrorEnabled())
				logger.error("port " + portEnv.getPort_nm() + " not found.");
			throw new IOException("port " + portEnv.getPort_nm() + " not found.");
		} // if

		try {
			serialPort = (SerialPort) portId.open("CasmallApp", portEnv.getPort_open_wait_time());
		} catch (PortInUseException pe) {
			if (logger.isErrorEnabled())
				logger.error("Port In USE : " + pe.getMessage());
			throw pe;
		} // try

		try {
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			// TODO install 후 open 오류 확인!!
			if (logger.isDebugEnabled()) {
				logger.debug("setSerialPortParams:" + portEnv.getBaud_rate() + "/" + portEnv.getData_bits() + "/"
				        + portEnv.getStop_bits() + "/" + portEnv.getParity_bits());
			}
			serialPort.setSerialPortParams(portEnv.getBaud_rate(), portEnv.getData_bits(), portEnv.getStop_bits(),
			        portEnv.getParity_bits());
		} catch (IOException ie) {
			if (logger.isErrorEnabled())
				logger.error("StreamOpen Error : " + ie.getMessage());
			serialPort.close();
			throw ie;
		} catch (UnsupportedCommOperationException ue) {
			if (logger.isErrorEnabled())
				logger.error("setSerialPortParams Error");
			serialPort.close();
			throw new IOException(ue);
		} // try

		Runnable queueRunnable = new Runnable() {
			public void run() {
				bq = new ByteQueue(portEnv.getRead_buff_size());
				offerQqueue();
			}

			void offerQqueue() {
				byte[] readBuffer = new byte[portEnv.getRead_buff_size()];
				int read = 0;
				byte[] readBytes = null;
				isDataInput = false;
				int retry = 0;
				String retryMsg = "";
				while (!offerQueueThread.isInterrupted()) {
					try {
						Thread.sleep(portEnv.getRead_wait_time());
						if (inputStream.available() > 0 || bq.size()>0) {
							read = inputStream.read(readBuffer);

							if (read > 0) {
								isDataInput = true;
								readBytes = new byte[read];
								System.arraycopy(readBuffer, 0, readBytes, 0, read);

								// If queue is full, data remove
								if (bq.getRemainingCapacity() < read) {
									if (logger.isDebugEnabled()) {
										logger.debug("Queue is full : remove form queue - "
										        + (read - bq.getRemainingCapacity()));
									}
									bq.remove(read - bq.getRemainingCapacity());
								}

								// Queue add data
								bq.offer(readBytes);

								if (isCallbackWork()) {
									HashMap<String, Object> hm = new HashMap<String, Object>();
									try {
										// Queue Size가 전문 길이보다 적으면 추가로 읽기...
										if (sem.getMsgEnv().getMsg_tot_len() > bq.size()) {
											if (logger.isDebugEnabled()) {
												logger.debug("queue size is too short! re-read. (" + bq.size() + "/"
												        + sem.getMsgEnv().getMsg_tot_len() + ")");
											}
											continue;
										}

										if (sem.getOsMcEnv().getRead_retry_cnt() > 0) {
											if (retry > sem.getOsMcEnv().getRead_retry_cnt()) {
												throw new IOException("Retry error - " + retryMsg);
											}
										} else {
											if (retry > 0) {
												throw new IOException("Retry error - " + retryMsg);
											}
										}

										// read data
										byte[] o = readLine();

										// 전문을 읽지 못했을 경우(종료문자 못찾을 경우) 재시도
										if (o == null) {
											retry++;
											retryMsg = "read message is null! retry-" + retry;
											if (logger.isDebugEnabled()) {
												logger.debug(retryMsg);
											}
											continue;
										}
										
										// 읽은 전문길이가 정의된 길이보다 작을 경우 재시도
										if (o.length < sem.getMsgEnv().getMsg_tot_len()) {
											retry++;
											retryMsg = "read message size is to short! retry-" + retry;
											if (logger.isDebugEnabled()) {
												logger.debug(new String(o));
												logger.debug(retryMsg);
											}
											continue;
										}
										
										// 읽은 전문길이가 정의된 길이보다 클 경우 재시도
										if (o.length > sem.getMsgEnv().getMsg_tot_len()) {
											retry++;
											retryMsg = "read message size is to long! retry-" + retry;
											if (logger.isDebugEnabled()) {
												logger.debug(new String(o));
												logger.debug(retryMsg);
											}
											continue;
										}

										hm = parse(sem.getMsgEnv(), o);
									} catch (NumberFormatException ne) {
										hm.put(DConstants.MSG_ERROR, ne.getMessage());
									} catch (IOException ie) {
										hm.put(DConstants.MSG_ERROR, ie.getMessage());
									}

									if (logger.isDebugEnabled()) {
                                        logger.debug("parse  - "+hm);
                                    }
									boolean despose = callNotify(hm);
									
									// queue 초기
									bq.clear();
									
									if (!despose)
										break;

									retry = 0;
								}// if isCallbackWork
							} else {
								isDataInput = false;
							}// if read
						} else {
							HashMap<String, Object> hm = new HashMap<String, Object>();
							hm.put(DConstants.MSG_ERROR, "#N/A");
							if (logger.isDebugEnabled()) {
	                            logger.debug("inputStream not available!");
                            }
							boolean despose = callNotify(hm);
							if (!despose)
								break;
							retry = 0;
							isDataInput = false;
						}// if inputStream.available()
					} catch (NoSuchElementException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} // while
			} // offerQqueue
		};
		offerQueueThread = new Thread(queueRunnable);
		offerQueueThread.start();

		if (logger.isDebugEnabled())
			logger.debug("Port open success");
		isOpen = true;
	} // open

	private synchronized boolean callNotify(HashMap<String, Object> hm) {
		for (CmSerialCallbackIF cb : callback) {
			if (cb.isDisposed())
				return false;
			if (cb.isWork()) {
				cb.notify(hm);
			}
		}
		return true;
	}

	/**
	 * Read line from Queue
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] readLine() throws IOException {
		return readLine(DConstants.DEFAULT_MC_ID);
	}

	/**
	 * Read line from Queue
	 * 
	 * @param msgId
	 * @return
	 * @throws IOException
	 */
	public byte[] readLine(String msgId) throws IOException {
		CmMsgDTO msgEnv = sem.getMsgEnv(msgId);
		return readLine(msgEnv);
	}

	/**
	 * Read line from Queue 라인 구분 문자만 체크
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] readLine(CmMsgDTO msgEnv) throws IOException {
		byte[] read = null;

		if (!isOpen) {
			throw new IOException("Port is not opened!!");
		}
		if (bq != null && bq.size() > 0) {
			int idx = bq.find(msgEnv.getEnd());
			if (idx != -1) {
				read = bq.poll(idx + msgEnv.getEnd().length);
			}
		} // if
		return read;
	} // readLine
	
	public byte[] readAll() throws IOException {
		return bq.poll(bq.size());
	}
	
	/**
	 * 전문 파싱
	 * 
	 * @param msgEnv
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Object> parse(CmMsgDTO msgEnv, byte[] msg) throws IOException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		byte[] tmp = null;

		if (msg == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("message is null");
			}
			throw new IOException("message is null");
		}

		if (msg.length != msgEnv.getMsg_tot_len()) {
			if (logger.isDebugEnabled()) {
				logger.debug("message length is wrong. env:" + msgEnv.getMsg_tot_len() + "/msg:" + msg.length);
			}
			throw new IOException("message length is wrong. env:" + msgEnv.getMsg_tot_len() + "/msg:" + msg.length);
		}

		List<CmMsgAttrDTO> attrs = msgEnv.getAttrList();
		for (int i = 0; i < attrs.size(); i++) {
			CmMsgAttrDTO dto = attrs.get(i);
			tmp = new byte[dto.getAttr_len()];
			System.arraycopy(msg, dto.getStt_pos(), tmp, 0, dto.getAttr_len());

			// type 체크. Number일 경우 +,-,숫자 체크
			if (PConstants.MSG_TYPE_N.equals(dto.getAttr_type_cd())) {
				String str = new String(tmp);
				if (!StringUtil.isDigit(str)) {
					throw new NumberFormatException("Number data exception(" + dto.getAttr_nm() + ") : " + new String(tmp));
				}
				data.put(dto.getAttr_nm(), String.valueOf(StringUtil.parseDouble(str)));
			} else if (PConstants.MSG_TYPE_S.equals(dto.getAttr_type_cd())) {
				data.put(dto.getAttr_nm(), new String(tmp));
			} else if (PConstants.MSG_TYPE_B.equals(dto.getAttr_type_cd())) {
				data.put(dto.getAttr_nm(), tmp);
			} else {
				data.put(dto.getAttr_nm(), new String(tmp));
			}
			// if
		}// for

		return data;
	}// parse

	/**
	 * InputStream 객체 반환
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputStream getIinputStream() throws IOException {
		return inputStream;
	}

	/**
	 * SerialPort 객체 반환
	 * 
	 * @return
	 */
	public SerialPort getSerialPort() {
		return serialPort;
	}

	/**
	 * Callback 동작 여부 (MessageConfig에서 사용)
	 * 
	 * @return
	 */
	public boolean isCallbackWork() {
		return isCallbackWork;
	}

	/**
	 * Callback 동작 여부 설정 (MessageConfig에서 사용)
	 * 
	 * @param isCallbackWork
	 */
	public void setCallbackWork(boolean isCallbackWork) {
		this.isCallbackWork = isCallbackWork;
	}

	public synchronized void addCallback(CmSerialCallbackIF cb) {
		callback.add(cb);
	}

	/**
	 * 객체 닫기
	 */
	public void close() {
		try {
			if (serialPort != null) {
				serialPort.close();
				isOpen = false;
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (offerQueueThread != null && offerQueueThread.isAlive()) {
				offerQueueThread.interrupt();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // close

	public boolean isDataInput() {
		return isDataInput;
	}

	public boolean isOpen() {
		return isOpen;
	}
}
