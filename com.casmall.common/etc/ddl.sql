
CREATE TABLE CM_MSG (
       MSG_ID               VARCHAR(20) NOT NULL,
       STT_CHAR             VARCHAR(20),
       END_CHAR             VARCHAR(20),
       MSG_TOT_LEN          INTEGER,
       DEL_YN               VARCHAR(1),
       RGN_DT               DATE,
       RGN_ID               VARCHAR(20),
       EDT_DT               DATE,
       EDT_ID               VARCHAR(20),
       SVR_TRN_DT           DATE
);

CREATE UNIQUE INDEX XPKCM_MSG ON CM_MSG
(
       MSG_ID
);


ALTER TABLE CM_MSG
       ADD PRIMARY KEY (MSG_ID);


CREATE TABLE CM_MSG_ATTR (
       MSG_ID               VARCHAR(20) NOT NULL,
       ATTR_SEQ             NUMERIC(10) NOT NULL,
       ATTR_PRIOR           NUMERIC(2),
       ATTR_NM              VARCHAR(30),
       ATTR_LEN             NUMERIC(3),
       ATTR_TYPE_CD         VARCHAR(2),
       DEL_YN               VARCHAR(1),
       RGN_DT               DATE,
       RGN_ID               VARCHAR(20),
       EDT_DT               DATE,
       EDT_ID               VARCHAR(20),
       SVR_TRN_DT           DATE
);

CREATE UNIQUE INDEX XPKCM_MSG_ATTR ON CM_MSG_ATTR
(
       MSG_ID,
       ATTR_SEQ
);


ALTER TABLE CM_MSG_ATTR
       ADD PRIMARY KEY (MSG_ID, ATTR_SEQ);


CREATE TABLE CM_OS_MC (
       MC_ID                VARCHAR(20) NOT NULL,
       PORT_NM              VARCHAR(10),
       MC_NM                VARCHAR(30),
       BAUD_RATE            INTEGER,
       DATA_BITS            INTEGER,
       PARITY_BITS          INTEGER,
       STOP_BITS            INTEGER,
       PORT_OPEN_WAIT_TIME  INTEGER,
       READ_BUFF_SIZE       INTEGER,
       READ_WAIT_TIME       INTEGER,
       READ_RETRY_CNT       INTEGER,
       DEL_YN               VARCHAR(1),
       RGN_DT               DATE,
       RGN_ID               VARCHAR(20),
       EDT_DT               DATE,
       EDT_ID               VARCHAR(20),
       SVR_TRN_DT           DATE
);

CREATE UNIQUE INDEX XPKCM_OS_MC ON CM_OS_MC
(
       MC_ID
);


ALTER TABLE CM_OS_MC
       ADD PRIMARY KEY (MC_ID);


ALTER TABLE CM_MSG_ATTR
       ADD FOREIGN KEY (MSG_ID)
                             REFERENCES CM_MSG;



